package exercises.Concurrency

import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {
  val parallelList = List(1,2,3).par

  val aparVector = ParVector[Int](1,2,3)

  // parallel versions of seq, array, map, list, set, hashmap

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 10000000).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  val parallelTime = measure {
    list.par.map(_ + 1)
  }

  println("Serial time " + serialTime)
  println("Parallel time " + parallelTime )
  /*
  Parallel collections work on map-reduce model
  split elements into chunks - Splitter
  operation
  recombine them - Combiner

  time will take more for smaller collection size ~ 10k
   */

  //ForkJoinPoolTaskSupport
  //https://docs.scala-lang.org/overviews/parallel-collections/configuration.html
  //ExecutionContext ->


  //Atomic Ops and References
  val atomic = new AtomicReference[Int](2)
  val current = atomic.get() // always thread-safe read
  atomic.set(24) // thread-safe write

  val newval = atomic.getAndSet(5)

  //compariions are shallow equality
  atomic.compareAndSet(39, 49) // if the value is 39 then set to 49 in a thread-safe way

  atomic.updateAndGet(_*2) // thread-safe function run

  atomic.getAndUpdate(_*3)

  atomic.accumulateAndGet(12, _ + _)

  atomic.getAndAccumulate(12, _+_)
}
