package exercises.Concurrency

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success, Try}

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
 // Thread.sleep(3000)



//function wh

  def insequence[A,B] (first: Future[A], second: Future[B]): Future[B] = {
    first.flatMap(_ => second) // ensures first future has run
  }

  //return first executed future
  def first[A](futureA: Future[A], futureB: Future[A]): Future[A] = {
    val promise = Promise[A]

    def tryComplete[A](promise: Promise[A], result: Try[A]) = result match {
      case Success(r) => try {
        promise.success(r)
      } catch {
        case _ => ()
      }
      case Failure(ex) => try {
        promise.failure(ex)
      } catch  {
        case _ => ()
      }
    }
    futureA.onComplete(tryComplete(promise, _))
    futureB.onComplete(tryComplete(promise, _))
    //same as above two lines
    futureA.onComplete(promise.tryComplete)
    futureB.onComplete(promise.tryComplete)

    promise.future
  }

  //return last selected future
  def last[A](futureA: Future[A], futureB: Future[A]): Future[A] = {
    val bothPromise = Promise[A]
    val lastPromise = Promise[A]

    futureA.onComplete(
      (res) => {
        if(!bothPromise.tryComplete(res)) {
          lastPromise.complete(res)
        }
      }
    )

    futureB.onComplete(
      (res) => {
        if(!bothPromise.tryComplete(res)) {
          lastPromise.complete(res)
        }
      }
    )

    lastPromise.future
  }

  val fast = Future {
    Thread.sleep(100)
    println("Finished fast future")
    42
  }

  val slow = Future {
    Thread.sleep(200)
    21
  }

  def testFuture[T](future: Future[T]) = {
    future.onComplete(x => println(s"Result -> $x"))
    Thread.sleep(1000)
  }

  //testFuture(fast)
  //testFuture(fast)
  //testFuture(fast)

    first(fast, slow).foreach(println)
    first(fast, slow).foreach(println)
    last(fast, slow).foreach(println)




  def retryUntil[T](action: () => Future[T], condition: T=>Boolean): Future[T] = {
    action()
      .filter(condition)
      .recoverWith { //Partial Function
        case _ => retryUntil(action, condition)
      }
  }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextValue = random.nextInt(100)
    println("Geenrated " + nextValue)
    nextValue
  }

  retryUntil(action, (x:Int) => x < 50).foreach(result => println("Settled " + result))

  Thread.sleep(3000)

}
