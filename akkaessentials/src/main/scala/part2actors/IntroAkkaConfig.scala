package part2actors

import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorSystem, Props,ActorLogging}
import akka.event.Logging

object IntroAkkaConfig extends App {
  val configString = ConfigFactory.load()

  class ActorwithLogging extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("loggingsystem")

  val inbuiltLogger = system.actorOf(Props[ActorwithLogging], "as1d")
  inbuiltLogger ! "logging a simple message"

  val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
  val system1 = ActorSystem("loggingsystem1", specialConfig)

  val inbuiltLogger1 = system1.actorOf(Props[ActorwithLogging], "as1d")
  inbuiltLogger ! "logging a simple message"

}