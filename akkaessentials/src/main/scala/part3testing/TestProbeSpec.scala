package part3testing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class TestProbeSpec extends TestKit(ActorSystem("TestProbeSpec"))
with ImplicitSender
with WordSpecLike
with BeforeAndAfterAll
{
  override def afterAll() = {
    TestKit.shutdownActorSystem(system)
  }

  import TestProbeSpec._

  "A master actor" should {
    "register a slave" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")
      master ! Register(slave.ref)
      expectMsg(RegistrationAck)
    }

    "send the work to slave actor" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegistrationAck)

      // interaction between master and slave actor
      val workloadString = "I love akka"
      master ! Work(workloadString)

      slave.expectMsg(SlaveWork(workloadString, testActor))
      slave.reply(WorkCompleted(3, testActor))
      expectMsg(Report(3))
    }

    "aggregate data correctly" in {
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegistrationAck)

      // interaction between master and slave actor
      val workloadString = "I love akka"
      master ! Work(workloadString)
      master ! Work(workloadString)

      slave.receiveWhile() {
        case SlaveWork(`workloadString`, `testActor`) => slave.reply(WorkCompleted(3, testActor))
      }

      expectMsg(Report(3))
      expectMsg(Report(6))
    }
  }

}

object TestProbeSpec {

  case class Work(text: String)
  case class SlaveWork(text:String, originalRequestor: ActorRef)
  case class Register(slaveRef : ActorRef)
  case object RegistrationAck
  case class WorkCompleted(count:Int, originalRequestor:ActorRef)
  case class Report(count:Int)

  /*class Slave extends Actor {
    def receive: Receive = {
      case SlaveWork(text:String, originalRequestor:ActorRef) => sender() ! WorkCompleted(text.split(" ").count(), originalRequestor)
    }
  }*/

  class Master extends Actor {
    def receive: Receive = {
      case Register(slaveRef) => {
        sender() ! RegistrationAck
        context.become(onlineReceive(slaveRef, 0))
      }
    }

    def onlineReceive(slaveRef: ActorRef, totalWordCount: Int): Receive = {
      case Work(text) => slaveRef ! SlaveWork(text, sender())
      case WorkCompleted(count:Int, originalRequestor:ActorRef) => {
        originalRequestor ! Report(totalWordCount+count)
        context.become(onlineReceive(slaveRef, count+totalWordCount))
      }
    }
  }
}
