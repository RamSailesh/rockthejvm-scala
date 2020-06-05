package part1Recap

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Source, Sink}

object AkkaRecap extends App {
  /*
  Akka guarantees no race conditions inside raceCondition
  ActorSystem
  Actor
  -> Message to Actor using ! (Tell)
  -> Return value from the message ? (Ask)
  -> Messages in Actor are send asynchronously and on somethread
  -> Each message is processed atomically
  -> Change message handler using context.become
  -> sender() -> the ActorRef which send the message
  -> forward -> send to original sender in A (b ! "print") in B (c forward "print")
  -> stash and unstashAll ->
  -> Actors can spawn other actors (Parent, child relationship) using context.ActorOf
  -> actors have a defined lifecycle (Started, stopped, resumed, suspended and restarted)
  -> PoisonPill -> kills actor
  -> ActorLogging
  -> Supervision -> Decides how parent actor responds to child failures, supervisor strategy
  -> Configure Akka Infrastructure -> Dispatcher, Routers, MailBoxes
  -> Schedulers of programming tasks (ICancellables)
  -> Finite State Machine
  -> Ask Pattern
  -> Pipe Pattern
   */
}
