package part1recap

object MultithreadingRecap extends App {
  // threads on jvm
  val aThread = new Thread(()=>println("running a thread in parallel"))
  aThread.start()
  aThread.join()

  //synchronized block to synchronize executions
  //inter-thread communication on the JVM using wait, notify and notifyAll

  //Futures
  import scala.concurrent.Future
  import scala.concurrent._
  import scala.concurrent.ExecutionContext.Implicits.global
  //future will be executed when declared
  def createFuture = Future[Int] {
    //computation runs on a different thread
    println("executing")
    42
  }

  def callFutureByName(f: => Future[Int]) = {

  }

  callFutureByName(createFuture)
  callFutureByName(createFuture)


  def create42 = Future{42}
  create42.map(x => x*2).foreach(println)

  //Futures
  //Promises -> Writable Futures, completing futures manually

}
