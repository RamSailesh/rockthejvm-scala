package part4faulttolerance

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props, Terminated}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SupervisionSpec extends TestKit(ActorSystem("SupervisionSpec"))
with ImplicitSender
with WordSpecLike with BeforeAndAfterAll
{
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  object SupervisionSpec {

    "A supervisor" should{
      "resume child in case of minor fault" in {
        val supervisor = system.actorOf(Props[Supervisor])
        supervisor ! Props[FussyWordCounter]
        val child = expectMsgType[ActorRef]
        child ! "hello scala"
        child ! Report
        expectMsg(3)
        child ! "SSda sdkjasndkja ds jk ad asdad k jas djasjasjd a skdajsd "
        child ! Report
        expectMsg(3)
      }

      "restart child in case of nullpointerexception" in {
        val supervisor = system.actorOf(Props[Supervisor])
        supervisor ! Props[FussyWordCounter]
        val child = expectMsgType[ActorRef]
        child ! "hello scala"
        child ! Report
        expectMsg(3)
        child ! ""
        child ! Report
        expectMsg(0)
      }

      "terminate child in case of major error" in {
        val supervisor = system.actorOf(Props[Supervisor])
        supervisor ! Props[FussyWordCounter]
        val child = expectMsgType[ActorRef]
        watch(child)
        child ! "hello scala"
        val terminatedMessage = expectMsgType[Terminated]
        assert(terminatedMessage.actor == child)
      }


      "escalate the exception from supervisor" in {
        val supervisor = system.actorOf(Props[Supervisor], "supervisor")
        supervisor ! Props[FussyWordCounter]
        val child = expectMsgType[ActorRef]
        watch(child)
        child ! 12
        val terminatedMessage = expectMsgType[Terminated]
        assert(terminatedMessage.actor == child)


      }
    }

    class Supervisor extends Actor {

      override val supervisorStrategy = OneForOneStrategy() {
        case _ : NullPointerException => Restart
        case _: RuntimeException => Resume
        case _ : IllegalArgumentException => Stop
        case _: Exception => Escalate
      }

      override def receive: Receive = {
        case props:Props =>
          val childRef = context.actorOf(props)
          sender() ! childRef
      }
    }

    case object Report

    class FussyWordCounter extends Actor {
      var words = 0
      override def receive: Receive = {
        case Report => sender() ! words
        case "" => throw new NullPointerException("Empty String")
        case sentence: String => {
          if (sentence.length > 20) throw new RuntimeException("Cannot process more than 20 characters")
          else if (!sentence(0).isUpper) throw new IllegalArgumentException("SAasd")
          else words += sentence.split(" ").length
        }
        case _ => new Exception("unknown parameters")
      }
    }
  }
}
