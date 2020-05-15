package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.dispatch.{ControlMessage, PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.{Config, ConfigFactory}

object AkkaMailBox extends App {

  val system = ActorSystem("mailboxdemo" ,ConfigFactory.load().getConfig("mailboxedsDemo") )

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val supportTickLogger = system.actorOf(Props[SimpleActor].withDispatcher("supportTicketDispatcher"))
  supportTickLogger ! "P3 : sdads"
//  supportTickLogger ! "P3 : sdads"
//  supportTickLogger ! PoisonPill
//  Thread.sleep(1000)
//  supportTickLogger ! "P3 : sdads"
//  supportTickLogger ! "P0 : sdads"
//  supportTickLogger ! "P1 : sdads"


  //custom priority mailbox
  class TicketsMailBox (settings: ActorSystem.Settings, config : Config)
    extends UnboundedPriorityMailbox(
      PriorityGenerator {
        case message: String if (message.startsWith("P0")) => 0
        case message: String if (message.startsWith("P1")) => 1
        case message: String if (message.startsWith("P2")) => 2
        case message: String if (message.startsWith("P3")) => 3
        case _ =>  4
      }
    )

  case object ManagementTicket extends ControlMessage

  val controlAwareActor = system.actorOf(Props[SimpleActor].withMailbox("control-mailbox"))

  controlAwareActor ! "P3 : sdads"
  controlAwareActor ! "P3 : sdads"
  controlAwareActor ! ManagementTicket


}
