package part6patterns

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpecLike}
import akka.pattern.ask
import akka.util.Timeout


class AskSpec extends TestKit(ActorSystem("Asdasda"))
with WordSpecLike
with ImplicitSender
with BeforeAndAfterAll {

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "AskSepc Actor" should {
    "retrun value in read if ask is called" in {

      import AskSpec._
      import scala.concurrent.duration._

      val kv = system.actorOf(Props[KVActor])


      kv ! Write("Ram", "Sailesh")

      implicit val timeout: Timeout = 2 seconds

      val future = kv ? Read("Ram")


      import scala.util.Success
      import scala.concurrent.ExecutionContext.Implicits.global

      future.onComplete {
        case Success(Some(password)) =>
        assert(password == "Sailesh")
        println("ssadasd")
      }
    }
  }

}

object AskSpec {
  case class Read(key:String)
  case class Write(key:String, value:String)

  class KVActor extends Actor with ActorLogging {
    override def receive: Receive = online(Map[String, String]())
    def online(kv: Map[String, String]): Receive = {
      case Read(key) =>
        log.info(s"reading to key $key")
        log.info(s" ${kv.get(key)}")
        sender() ! kv.get(key)
      case Write(key, value) =>
        log.info(s"writing the value")
        context.become(online(kv + (key -> value)))
    }
  }
}