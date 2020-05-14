package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorsExercises extends App {
  /*
    Distributed word counting
   */
  object WordCounterMaster {
    case class Initialize(nChildren: Int)
    case class WordCountTask(id: Int, text: String)
    case class WorkCountReply(id: Int, count: Int)
  }
  class WordCounterMaster extends Actor {
    import WordCounterMaster._
    def withChildren(children: IndexedSeq[ActorRef], currChildCount: Int, currentTaskID: Int, requestMap: Map[Int, ActorRef]): Receive = {
      case WordCountTask(id1: Int, text: String) =>
        children(currChildCount) ! WordCountTask(currentTaskID, text)
        context.become(withChildren(children, currChildCount+1%children.size, currentTaskID+1, requestMap + (currentTaskID -> sender())))
      case WorkCountReply(id: Int, count: Int) =>
        //println(s"${requestMap(id).path} word count is $count")
        requestMap(id) ! count
    }

    override def receive: Receive = {
      case Initialize(count) =>
        val children = for {
          i <- 1 to count
        } yield context.actorOf(Props[WordCounterWorker], s"worker$i")
        context.become(withChildren(children, 0, 0, Map()))
    }
  }

  class WordCounterWorker extends Actor {
    import WordCounterMaster._
    override def receive: Receive = {
      case WordCountTask(id:Int, text: String) => {
        Thread.sleep(2000)
        sender() ! WorkCountReply(id, text.split(" ").length)
      }
    }
  }

  object Requester {
    case class SendMultipleMessages(master: ActorRef, statements: List[String])
  }
  class Requester extends Actor {
    import WordCounterMaster._
    import Requester._
    override def receive: Receive = {
      case SendMultipleMessages(master, input) =>
        input.foreach(master ! WordCountTask(0, _))
      case count: Int=>
        println(s"${self.path} word count is $count")
    }
  }



  val actorSystem = ActorSystem("ChildActorsExercises")

  val wordCounterMaster = actorSystem.actorOf(Props[WordCounterMaster],"wordcountmaster")

  import WordCounterMaster._
  wordCounterMaster ! Initialize(10)

  val r1 = actorSystem.actorOf(Props[Requester],"r1")
  val r2 = actorSystem.actorOf(Props[Requester],"r2")

  import Requester._
  r1 ! SendMultipleMessages(wordCounterMaster, List("This is a word", "Quick Brown fox jumps over lazy dog"))
  r2 ! SendMultipleMessages(wordCounterMaster, List("Scala rocks !!!!", "Functions are basic building blocks for a program", "Akka actors make concurrency distributed"))

}
