package part4faulttolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props}

object ActorLifeCycle extends App {

  object StartChild
  object FailChild
  object CheckChild
  class LifeCycleActor extends Actor with ActorLogging {
    override def preStart(): Unit = log.info(s"Iam starting $self")
    override def postStop(): Unit = log.info(s"Iam stopped $self")


    private val child:ActorRef = context.actorOf(Props[Child], "child")
    override def receive: Receive = {
      case StartChild =>
        context.actorOf(Props[Child], "child")
      case FailChild =>
        child ! Fail
      case CheckChild =>
        child ! Check
    }
  }
  object Fail
  object Check

  class Child extends Actor with ActorLogging {
    override def preStart(): Unit = log.info(s"Child starting $self")
    override def postStop(): Unit = log.info(s"Child stopped $self")

    override def preRestart(reason: Throwable, message:Option[Any]): Unit = log.info(s"Child preRestart ${reason.getMessage}")

    override def postRestart(reason: Throwable): Unit = log.info(s"supervised actor restarted")

    override def receive: Receive = {
      case StartChild =>
        context.actorOf(Props[LifeCycleActor], "child")
      case Fail => throw new RuntimeException("i failed....")
      case Check => log.info("alive and kicking....")
    }
  }


  val system = ActorSystem("lifecycle")
  val parent  = system.actorOf(Props[LifeCycleActor], "parent")
  parent ! FailChild
  parent ! CheckChild
 // parent ! StartChild
 // parent ! PoisonPill
}
