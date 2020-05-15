package part3testing


import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.util.Random

class TimedAssertionSpec extends TestKit(ActorSystem("timedassertionspec"))
with ImplicitSender
with WordSpecLike
with BeforeAndAfterAll
{
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TimedAssertionSpec._
  import scala.concurrent.duration._

  "A worker actor" should {
    val workerActor = system.actorOf(Props[WorkerActor])

    "reply with the meaning of life in a timely manner" in {
      within(500 millis, 1 second) { // timebox test
        workerActor ! "work"
        expectMsg(WorkResult(42))
      }
    }

    "reply with valid work at reasonable intervals" in {
      within(1 second) {
        workerActor ! "worksequence"

        val results = receiveWhile[Int](max = 2 seconds, idle = 500 millis, messages = 10) {
          case WorkResult(res) => res
        }

        assert(results.sum > 5)
      }
    }

  }



}

object TimedAssertionSpec {
  case class WorkResult(result: Int)
  class WorkerActor extends Actor {
    override def receive: Receive = {
      case "work" =>
        Thread.sleep(500)
        sender() ! WorkResult(42)
      case "worksequence" =>
        val r = new Random()
        for (i <- 1 to 10) {
          Thread.sleep(r.nextInt(50))
          sender() ! WorkResult(1)
        }
    }
  }
}
