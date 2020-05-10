package exercises.Concurrency

object NotifyAll  extends App {
  def testNotifyAll = {
    val bell = new Object

    (1 to 10).foreach(
      i => new Thread({
        () => {
          bell.synchronized{
            println(s"Thread$i: waiting")
            bell.wait
            println(s"Thread$i: hurray")
          }
        }
      }).start
    )

    new Thread(
      () => {
        Thread.sleep(1000)
        bell.synchronized{
          //bell.notify
          bell.notifyAll
        }
      }
    ).start

  }

  testNotifyAll
}
