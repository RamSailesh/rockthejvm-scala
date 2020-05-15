package part4faulttolerance

import java.io.File

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}
import akka.pattern.{Backoff, BackoffSupervisor}

import scala.concurrent.duration._
import scala.io.Source

object BackoffSupervisorDemo extends App {
    object ReadFile

    class ReadFileActor extends Actor with ActorLogging {
      var dataSource:Source = null

      override def receive: Receive = {
        case ReadFile =>
          if (dataSource == null) {
            dataSource = Source.fromFile(new File("src/main/resources/testfiles/somefile1.txt"))
            log.info("-"+ dataSource.getLines().toList)
          }
      }

      override def preStart(): Unit = log.info("actor starting")

      override def postStop(): Unit = log.warning("actor stopped")

      override def preRestart(reason: Throwable, message: Option[Any]): Unit = log.info("actor restarted")
    }

   val system =  ActorSystem("BackOffSuperVisor")
   val actor = system.actorOf(Props[ReadFileActor])
   actor ! ReadFile

   val simpleSuperVisorProps = BackoffSupervisor.props(
    Backoff.onFailure(
      Props[ReadFileActor],
      "simplebackoffactor",
      3 seconds,
      30 seconds,
      0.2
    )
   )

  val stopSuperVisor = system.actorOf(stopSupervisorProps)
  stopSuperVisor ! ReadFile

  val stopSupervisorProps = BackoffSupervisor.props(
    Backoff.onStop(
      Props[ReadFileActor],
      "stopbackoffactor",
      3 seconds,
      30 seconds,
      0.2
    ).withSupervisorStrategy(
      OneForOneStrategy() {
        case _ => Stop
      }
    )
  )

}
