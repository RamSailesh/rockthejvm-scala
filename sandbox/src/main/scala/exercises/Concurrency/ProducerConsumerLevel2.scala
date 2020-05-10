package exercises.Concurrency

import scala.collection.mutable
import scala.util.Random

object ProducerConsumerLevel2 extends App {
  def prodConsLargeBuffer = {
    val buffer : mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(
      () => {
        val random = new Random()
        while(true) {
          buffer.synchronized {
            if (buffer.isEmpty) {
              println("Consumer: Buffer waiting")
              buffer.wait()
            }

            val x = buffer.dequeue()
            println("Consumer: Consumed " + x.toString)

            buffer.notify()
          }
          Thread.sleep(random.nextInt(500))
        }
      }
    )

    val producer = new Thread(
      () => {
        val random = new Random()
        var i = 0
        while(true) {
          buffer.synchronized {
            if (buffer.size == capacity) {
              println("Producer: Buffer is full")
              buffer.wait()
            }

            println("Producer: Buffer producing " + i.toString)
            buffer.enqueue(i)

            buffer.notify()
            i+=1
          }
          Thread.sleep(random.nextInt(500))
        }
      })

    consumer.start()
    producer.start()
  }

  prodConsLargeBuffer

}
