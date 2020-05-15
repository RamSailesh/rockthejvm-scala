package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props}

object TimersSchedulers extends App {

  class SimpleActor extends Actor with ActorLogging{
    override def receive: Receive = {
      case message =>log.info(message.toString)
    }
  }

  val system = ActorSystem("TimersSchedulers")
  val simpleActor = system.actorOf(Props[SimpleActor])

  system.log.info("Scheduling reminder for simpleactor")
  import scala.concurrent.duration._

  system.scheduler.scheduleOnce(1 second){
    simpleActor ! "reminder"
  }(system.dispatcher)

  val routine = system.scheduler.schedule(1 second, 2 seconds) {
    simpleActor ! "heartbeat"
  }(system.dispatcher)

  system.scheduler.scheduleOnce(5 seconds) {
    routine.cancel()
  }(system.dispatcher)


  //SelfClosing Actor
  //If the actor receives the messae

  class SelfStoppingActor extends Actor with ActorLogging {
    def selfCloseAfterOneSecond: Cancellable = {
      system.scheduler.scheduleOnce(1 seconds) {
        context.stop(self)
        log.info("actor stopped")
      }(system.dispatcher)
    }

    def recieveMessage(cancellalbe:Cancellable) : Receive = {
      case message => {
        if (cancellalbe != null) {
          cancellalbe.cancel()
        }
        context.become(recieveMessage(selfCloseAfterOneSecond))
      }
    }

    override def receive: Receive = recieveMessage(null)
  }

  val selfClosingActor = system.actorOf(Props[SelfStoppingActor])
  selfClosingActor ! "ASdads"

}
