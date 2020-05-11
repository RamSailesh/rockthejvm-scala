package exercises.Concurrency

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration._

object Promises extends App {
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "rock the jvm banking app"
    def fetchUser(name: String): Future[User] = Future {
      Thread.sleep(5000)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future{
      Thread.sleep(250)
      Transaction(user.name, merchantName, amount, "success")
    }

    def purchase(user:String, item: String, merchantName: String, cost: Double): String = {
      // fetch user from db
      // validate
      // create transaction
      // wait for the transaction to finish
      // return status

      val statusFuture = for {
        user <- fetchUser(user)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(statusFuture, 2.seconds) // throws exception after duration is passed
      //Await.ready(statusFuture, 2.seconds)
    }
  }

  //println(BankingApp.purchase("ram", "iphone", "apple", 999.99))

  val promise = Promise[Int]() // controller of future
  val future = promise.future

  future.onComplete((r) => {
    println("I've received value " + r)
  })

  val producer = new Thread(() => {
    Thread.sleep(200)
    //promise.success(42)
    promise.failure(new RuntimeException("Invalid...."))
  })

  producer.start()

  Thread.sleep(5000)

}
