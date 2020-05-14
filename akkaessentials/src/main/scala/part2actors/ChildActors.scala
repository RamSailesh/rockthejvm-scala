package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActors extends App {
  // Actors can create other actors
  object Parent {
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }

  class Parent extends Actor {
    import Parent._

    override def receive: Receive = parentReceive(List())

    def parentReceive(children: List[ActorRef]): Receive = {
      case CreateChild(name) =>
        println(s"${self.path}: Creating child")
        val childRef = context.actorOf(Props[Child], name)
        context.become(parentReceive(children :+ childRef))
      case TellChild(message) => children.foreach(_ ! message)
    }
  }

  class Child extends Actor {
    override def receive: Receive = {
      case message => println(s"${self.path}: I got message $message")
    }
  }

  val actorSystem = ActorSystem("ChildActors")
  val parentRef = actorSystem.actorOf(Props[Parent], "bob")
  import Parent._
  parentRef ! CreateChild("Jack")
  parentRef ! CreateChild("Jill")

  parentRef ! TellChild("Hello!!!")

  // actor heirarchies
  // parent -> child descendants

  //Guardian actors
  // /system => system guardian, handles logging etc....
  // /user => user level guardian, the actors are
  // / => root guardian, manages system and user guardian


  //Actor Selection, get the actor ref using actor path
  val child = actorSystem.actorSelection("/user/bob/Jill")
  child ! "Found you!!"

  val invalidPath = actorSystem.actorSelection("/user/bob/JillInvalid")
  invalidPath ! "Found you!!" // deadletters



}
