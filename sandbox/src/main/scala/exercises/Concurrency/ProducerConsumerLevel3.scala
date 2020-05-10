package exercises.Concurrency

import scala.collection.mutable
import scala.util.Random


// limited buffer
// multiple producers
// multiple consumers
object ProducerConsumerLevel3 extends App {

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity:Int) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0
      while(true) {
        buffer.synchronized {

          while (buffer.size == capacity) {
            println(s"Producer$id: Buffer is full")
            buffer.wait()
          }

          println(s"Producer$id: Buffer producing " + i.toString)
          buffer.enqueue(i)

          buffer.notify()
          i+=1
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      while(true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println(s"Consumer$id: Buffer waiting")
            buffer.wait()
          }

          val x = buffer.dequeue()
          println(s"Consumer$id: Consumed $x")

          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  val buffer : mutable.Queue[Int] = new mutable.Queue[Int]
  val capacity = 20
  (1 until 30).foreach(x => new Producer(x, buffer, capacity).start)
  (1 until 2).foreach(x => new Consumer  (x, buffer).start)
}
