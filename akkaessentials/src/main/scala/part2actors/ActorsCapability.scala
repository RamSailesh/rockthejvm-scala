package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorsCapability extends App {

  case class SpecialMessage(msg: String)

  class SimpleActor extends Actor {

    //context.self -> self actor reference, equivalent of this
    println(context.self)
    println(self)
    println(context.system)
    println(context) // data about the env where actor is present

    override def receive : Receive = {
      case num: Int => println(s"I've received $num")
      case SpecialMessage(x) => println(s"Special message recieved $x"); self ! x
      case msg: String if msg.contains("bts") => println("It rains between 4 to 6pm")
      case msg: String => println(s"I've recieved $msg")
      case double: Double => sender() ! "its PI"
      case WirelessMessage(content, ref) => ref forward (content + "s")
    }
  }

  val system = ActorSystem("actorscapability")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleactor")
  simpleActor ! "Hello Actor"
  simpleActor ! 1231
  simpleActor ! Math.PI // Deadletters
  simpleActor ! SpecialMessage("What happens in bts?")

  //forwarding messages

  case class WirelessMessage(content: String, actorRef: ActorRef)

  val alice = system.actorOf(Props[SimpleActor], "alice")
  val bob = system.actorOf(Props[SimpleActor], "bob")
  alice ! WirelessMessage("Hi", bob)
}
