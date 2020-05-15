package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.TestActors.BlackholeActor
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import part3testing.BasicSpec.{BlackHole, LabTestActor, SimpleActor1}

import scala.concurrent.duration._
import scala.util.Random

class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {
  // teardown for test cases
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  //test suite
  "Simple actor" should {
    "sendback the same message" in {
      //test scenario
      val echoActor = system.actorOf(Props[SimpleActor1])
      val message = "hello akka"
      echoActor ! message

      expectMsg(message)
    }

    "do another" in {
      // test scenario
    }
  }

  "Blackhole actor" should {
    "sendback the same message" in {
      //test scenario
      val echoActor = system.actorOf(Props[BlackHole])
      val message = "hello akka"
      echoActor ! message

      expectNoMessage(2 second)
    }

    "do another" in {
      // test scenario
    }
  }

  "A LabTestActor" should {
    val labTestActor = system.actorOf(Props[LabTestActor])
    "turn the string in uppercase" in {
      val msg = "hello"
      labTestActor ! msg
      val res = expectMsgType[String]
      assert(res == msg.toUpperCase())
    }

    "reply to greeting" in {
      labTestActor ! "greeting"
      expectMsgAnyOf("hi", "hello")
    }

    "reply with favtech" in {
      labTestActor ! "favouritetech"
      expectMsgAllOf("scala", "akka")
    }

    "reply with cool tech in diff way" in {
      labTestActor ! "favouritetech"
      val msgs = receiveN(2)
    }

    "reply with cool tech in pf way" in {
      labTestActor ! "favouritetech"
      expectMsgPF() { //decompose objects and do assertions
        case "scala" => ()
        case "akka" => ()
      }
    }

  }
}

object BasicSpec {
  // parameters for the test suite

  class SimpleActor1 extends Actor {
    override def receive: Receive = {
      case message => sender() ! message
    }
  }

  class BlackHole extends Actor {
    override def receive: Receive = {
      case _ => ()
    }
  }

  class LabTestActor extends Actor {
    val random = new Random()
    override def receive: Receive = {
      case "greeting" => sender() ! {if (random.nextBoolean()) "hi" else "hello"}
      case "favouritetech" => sender() ! "scala"; sender() ! "akka"
      case msg: String => sender() ! msg.toUpperCase()

    }
  }
}
