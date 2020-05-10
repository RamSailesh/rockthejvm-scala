package exercises.Concurrency

object ThreadCommunication extends App {
  //producer consumer problem


   class Container() {
    private var value:Int = 0

    def isEmpty: Boolean = value == 0
    def get = {
      val result = value
      value = 0
      result
    }
    def set(newValue: Int) = value = newValue
  }

  def classicProducerConsumer = {
    val container = new Container
    val consumer = new Thread(
      () => {
        println("Consumer waiting")
        while(container.isEmpty) {
          println("Consumer actively waiting...")
        }
        println("Consumer : I have consumed " + container.get)
      }
    )

    val producer = new Thread(
      () => {
        println("Producer computing")
        Thread.sleep(500)
        val value = 42
        println("Producer: I've produced value: " + value)
        container.set(value)
      }
    )

    producer.start()
    consumer.start()
  }

  def smartProducerConsumer = {
    val container = new Container
    val consumer = new Thread(
      () => {
        println("Consumer waiting")
        container.synchronized{
          container.wait()
        }
        println("Consumer : I have consumed " + container.get)
      }
    )

    val producer = new Thread(
      () => {
        println("Producer computing")
        Thread.sleep(500)
        val value = 42
        container.synchronized{
          println("Producer: I've produced value: " + value)
          container.set(value)
          container.notify()
        }
      }
    )

    consumer.start()
    producer.start()

  }

  //classicProducerConsumer
  smartProducerConsumer
}
