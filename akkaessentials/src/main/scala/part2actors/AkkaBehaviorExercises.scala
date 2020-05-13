package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object AkkaBehaviorExercises extends App {

    /*
    Recreate Counter Actor with no-mutable state
    */
    //Domain
    object Counter {
      case object Increment
      case object Decrement
      case object Print
    }

  class Counter extends Actor {
    import  Counter._

    def countReceive(value: Int): Receive = {
      case Decrement => context.become(countReceive(value-1))
      case Print => println(s"Curr Value $value")
      case Increment => context.become(countReceive(value+1))
    }

    override def receive: Receive = countReceive(0)

  }

  import  Counter._
  val actorSystem  = ActorSystem("AkkaBehaviorExercises")
  val counter = actorSystem.actorOf(Props[Counter])
  (1 to 5).foreach(_ => counter ! Increment)
  counter ! Print
  (1 to 2).foreach(_ => counter ! Decrement)
  counter ! Print
  // simplified voting system
  // People

  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidate: Option[String])
  class Citizen extends Actor {
    def votingRequest(candidate: Option[String]): Receive = {
      case VoteStatusRequest => sender() ! VoteStatusReply(candidate)
      case Vote(candidate: String) => context.become(votingRequest(Some(candidate)))
    }
    override def receive: Receive = votingRequest(null)
  }

  case class AggregateVotes(citizens: Set[ActorRef])
  class VoteAggregator extends Actor {

    def voteStatusResponseReceived(votes: List[String], count: Int): Receive = {
      case AggregateVotes(citizens: Set[ActorRef]) => {
        context.become(voteStatusResponseReceived(votes, citizens.size + count))
        citizens.foreach(_ ! VoteStatusRequest)
      }
      case VoteStatusReply(candidate: Option[String]) =>
        if (votes.length == count) println((votes :+ candidate.getOrElse("")).filter(!_.isEmpty).groupBy(x => x).map(x => (x._1, x._2.length)))
        else context.become(voteStatusResponseReceived(votes :+ candidate.getOrElse(""), count))
    }
    
    override def receive: Receive = voteStatusResponseReceived(List(), -1)
  }

  val alice = actorSystem.actorOf(Props[Citizen])
  val bob = actorSystem.actorOf(Props[Citizen])
  val charlie = actorSystem.actorOf(Props[Citizen])
  val lam = actorSystem.actorOf(Props[Citizen])

  alice ! Vote("martin")
  bob ! Vote("roland")
  charlie ! Vote("roland")
  lam ! Vote("dredd")

  val voteAggregator = actorSystem.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice, bob, charlie, lam))

}
