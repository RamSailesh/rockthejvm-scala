package part1recap

object ThreadModelLimitations extends App {
  //OOP Encapsulation is valid in single threading model
  //OOP Encapsulation involves synchronization, locks are used to avoid it,  (deadlocks, livelocks and many issues arise


  //Delegating tasks to thread is pain
  //Pass a runnable to a running thread
  var task: Runnable = null

  val t:Thread = new Thread(()=> {
    while (true) {
      while(task == null) {
        t.synchronized {
          println("waiting")
          t.wait()
        }
      }

      task.synchronized {
        println("bg i've a task")
        task.run()
        task = null
      }
      Thread.sleep(100)
    }
  })
  t.start


  def delegateTask(r:Runnable) =  {
    if (task == null) task = r
    t.synchronized{
      t.notify()
    }
  }

  delegateTask(() => println("running....."))
  Thread.sleep(1000)
  delegateTask(() => println("one more task....."))

  Thread.sleep(1000)

  //not scalable!!!

  //tracking and dealing with errors in a multithreading env is complex
  //exceptions are consumed, cannot be backtracked
}
