package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.scalatest.StreamlinedXmlNormMethods
import part2actors.ChangingActorBehavior.Mom.MomStart

object ChangingActorBehavior extends App {


  object FuzzyKid{
    case class Accept()
    case class Reject()
    val happy = "happy"
    val sad = "sad"
  }

  class FuzzyKid extends Actor {
    import Mom._
    import FuzzyKid._
    var state = happy
    override def receive: Receive = {
      case Food(food) if food == veggie => state = sad
      case Food(food) if food == choco => state = happy
      case Ask(_) => sender() ! {if (state == happy) Accept else Reject}
    }
  }

  class StatelessFuzzyKid extends Actor {
    import Mom._
    import FuzzyKid._

    override def receive: Receive = happyReceive
    def happyReceive : Receive = {
      case Food(food) if food == veggie => context.become(sadReceive)
      case Ask(_) => sender() ! Accept
    }

    def sadReceive : Receive = {
      case Food(food) if food == choco => context.become(happyReceive)
      case Ask(_) => sender() ! Reject
    }
  }

  object Mom {
    case class Food(foodType: String)
    case class Ask(message: String)
    case class MomStart(kidRef: ActorRef)
    val veggie = "VEGGIE"
    val choco = "CHOCOLATE"
  }
  class Mom extends Actor {
    import Mom._
    import FuzzyKid._
    override def receive: Receive = {
      case MomStart(kid: ActorRef) => {
        kid ! Food(veggie)
        kid ! Ask("ASDASdasd")
        kid ! Food(choco)
        kid ! Ask("ASDASdasd")
      }
      case Accept => println("kid is happy")
      case Reject => println("kid is sad, but he is healthy")
    }
  }

  val actorSystem = ActorSystem("Momkid")
  val kidRef = actorSystem.actorOf(Props[FuzzyKid])
  val mom = actorSystem.actorOf(Props[Mom])
  mom ! MomStart(kidRef)

  val statelesskidRef = actorSystem.actorOf(Props[StatelessFuzzyKid])
  mom ! MomStart(statelesskidRef)

}
