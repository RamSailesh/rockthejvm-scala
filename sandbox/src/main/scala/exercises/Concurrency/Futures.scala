package exercises.Concurrency

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success, Try}

object Futures extends App {
  def calculateMeeaningOfLife = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future{
    calculateMeeaningOfLife
  }

  println(aFuture) //Future(<not completed>)
  println(aFuture.value)  //None

  aFuture.onComplete(t => t match {
    case Success(value) => println(s"Meaning of life is $value")
    case Failure(exception: Exception) => println(s"Failed with $exception")
  }) //oncomplete is executed by somethread

  Thread.sleep(3000)


  case class Profile(id: String, name: String) {
    def poke(other: Profile) = {
      println(s"${this.name} poking ${other.name}")
    }
  }
}
