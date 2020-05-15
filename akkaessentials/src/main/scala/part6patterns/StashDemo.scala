package part6patterns

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash}

object StashDemo extends App {
  val system = ActorSystem("StashDemo")

  case object Open
  case object Close
  case object Read
  case class Write(data:String)

  class Resource extends Actor with ActorLogging with Stash {
    def open : Receive = {
      case Close => context.become(closed)
      case message => log.info(s"message $message")
    }

    def closed: Receive = {
      case Open =>
        log.info("Opening")
        unstashAll()
        context.become(open)
      case message =>
        log.info(s"stashing message $message ")
        stash()
    }

    override def receive: Receive = closed
  }


  val resourceActor = system.actorOf(Props[Resource])

  resourceActor ! Open
  resourceActor ! Read
  resourceActor ! Close

  (1 to 1000000).foreach ((x) => {
    resourceActor ! Read
    resourceActor ! Write("Asdasads")
  })
  resourceActor ! Open

}
