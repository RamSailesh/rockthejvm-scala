package part2actors

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem, Props,ActorLogging}
import akka.event.Logging

object ActorLoggingTest extends App {
  class SimpleActorWithExplicitLogger extends Actor {
    val logger = Logging(context.system, this)
    override def receive: Receive = {
      case message => logger.info(message.toString)
    }
  }
  
  class ActorwithLogging extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("loggingsystem")
  val simpleLogger = system.actorOf(Props[SimpleActorWithExplicitLogger], "asd")
  simpleLogger ! "logging a simple message"

  val inbuiltLogger = system.actorOf(Props[ActorwithLogging], "as1d")
  inbuiltLogger ! "logging a simple message"
}
