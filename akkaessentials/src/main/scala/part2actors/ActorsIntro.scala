package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App {
  val actorSystem = ActorSystem("firstActorSystem") // name should be unique
  println(actorSystem.name)

  //actor is uniquely identified
  //actors can only be interact via messages
  //messages are asynchronous
  //! means tell

  class WordCountActor extends Actor{
    var totalWords = 0

    def receive: PartialFunction[Any, Unit] = {
      case message: String => println(message);totalWords += message.split(" ").length
      case _ => println("Unable to understand the message")
    }
  }

  val wordCounter = actorSystem.actorOf(Props[WordCountActor], "wordCounter") // name should be unique per actorsystem
  wordCounter ! "Iam learning Akka!!!"  // completely async call
  println("asda")

  class Person(val name: String) extends Actor {
    def receive: PartialFunction[Any, Unit] = {
      case "hi" => println(s"hi! my name is $name")
    }
  }

  object Person {
    def props(name: String) = Props(new Person(name))
  }
  val personActor = actorSystem.actorOf(Person.props("ram"))
  personActor ! "hi"

}
