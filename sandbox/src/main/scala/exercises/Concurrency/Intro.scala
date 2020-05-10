package exercises.Concurrency

object Intro extends App {

  def inceptionOfThreads(number: Int) = {

    def inceptionThreadUtil(curr: Int, limit:Int): Thread = new Thread {
      if (curr < limit) {
        val newthread = inceptionThreadUtil(curr+1, limit)
        newthread.start()
        newthread.join()
      }
      println("thread" + curr)
    }

    inceptionThreadUtil(1, number).start()
  }

  inceptionOfThreads(10)



}
