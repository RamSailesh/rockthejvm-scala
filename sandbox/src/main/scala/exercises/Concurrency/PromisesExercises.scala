package exercises.Concurrency

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object PromisesExercises extends App {

  def findJudge(N: Int, trust: Array[Array[Int]]): Int = {
    if(N == 1) trust(0)(0)
    else {
      val possibleJudges = trust.groupBy(x => x(1)).filter(x => x._2.length == (N-1)).map(x => x._1).toList
      if (possibleJudges.length == 1) {
        val persons = trust.filter(x => possibleJudges.contains(x(0)))
        if (persons.length == 0) possibleJudges(0)
        else -1
      } else -1
    }
  }

  //Future returns value immediately
  val immediateFuture = Future  {
    42
  }

  immediateFuture.onComplete(println)
  println("first print this")
  Thread.sleep(3000)




}
