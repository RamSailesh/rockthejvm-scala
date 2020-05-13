package playground

import akka.actor.ActorSystem

object AkkaPlayground extends App {
  val actorSystem = ActorSystem("HelloAkka")
  println(actorSystem.name)
}


