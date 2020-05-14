package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorsExercises extends App {
  /*
    Distributed word counting
   */
  object WordCounterMaster {
    case class Initialize(nChildren: Int)
    case class WordCountTask(text: String)
    case class WorkCountReply(count: Int)
  }
  class WordCounterMaster extends Actor {
    import WordCounterMaster._
    def withChildren(children: IndexedSeq[ActorRef], currChildCount: Int): Receive = {
      case WordCountTask(text: String) =>
        children(currChildCount) ! WordCountTask(text)
        context.become(withChildren(children, currChildCount+1%children.size))
      case WorkCountReply(count: Int) =>
        println(s"word count is $count")
    }

    override def receive: Receive = {
      case Initialize(count) =>
        val children = for {
          i <- 1 to count
        } yield context.actorOf(Props[WordCounterWorker], s"worker$i")
        context.become(withChildren(children, 0))
    }
  }

  class WordCounterWorker extends Actor {
    import WordCounterMaster._
    override def receive: Receive = {
      case WordCountTask(text: String) => {
        Thread.sleep(2000)
        sender() ! WorkCountReply(text.split(" ").length)
      }
    }
  }

  val actorSystem = ActorSystem("ChildActorsExercises")

  val wordCounterMaster = actorSystem.actorOf(Props[WordCounterMaster],"wordcountmaster")

  import WordCounterMaster._
  wordCounterMaster ! Initialize(10)

  wordCounterMaster ! WordCountTask ("This is a word")

  wordCounterMaster ! WordCountTask ("Quick Brown fox jumps over lazy dog")
  
}
