package part4faulttolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, PoisonPill, Props, Terminated}
import part4faulttolerance.StartingStoppingActors.Parent.StartChild

object StartingStoppingActors extends App {
  val actorSystem = ActorSystem("StartingStoppingActors")

  object Parent{
    case class StartChild(name: String)
    case class StopChild(name: String)
    case object Stop
  }

  class Parent extends Actor with ActorLogging {
    import Parent._
     def withChildren(children: Map[String, ActorRef]): Receive = {
      case StartChild(name: String) =>
        log.info(s"starting child $name")
        context.become(withChildren(children + (name -> context.actorOf(Props[Child], name))))
      case StopChild(name: String) =>
        log.info(s"stopping child $name")
        val childOption = children.get(name)
        childOption.foreach(childRef => context.stop(childRef))
        //context.become(withChildren(children + (name -> context.actorOf(Props[Child], name))))
      case Stop =>
         log.info("stopping ")
         context.stop(self)

    }


    override def receive: Receive = withChildren(Map[String, ActorRef]())

  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  import Parent._
  val parent = actorSystem.actorOf(Props[Parent], "parent")
  parent ! StartChild("c1")
  parent ! StartChild("c2")

  val child = actorSystem.actorSelection("/user/parent/c1")
  child ! "log"

  val child2 = actorSystem.actorSelection("/user/parent/c2")
  child2 ! "log2"


  parent ! StopChild("c1")
  parent ! Stop

  Thread.sleep(5000)

  child2 ! "log3" // deadletters

  val c3 = actorSystem.actorOf(Props[Child])
  c3 ! "asd"
  c3 ! PoisonPill
  c3 ! "ASdasd"

  val c4 = actorSystem.actorOf(Props[Child])
  c4 ! "asd"
  c4 ! Kill
  c4 ! "ASdasd"

  //deathwatch
  class Watcher extends Actor with ActorLogging{
    import Parent._
    def withChildren(children: Map[String, ActorRef]): Receive = {
      case StartChild(name: String) =>
        log.info(s"starting child $name")
        val c =  context.actorOf(Props[Child], name)
        context.become(withChildren(children + (name ->c)))
        context.watch(c)
      case Terminated(ref) => log.info(s"$ref is terminated")
      case StopChild(name: String) =>
        log.info(s"stopping child $name")
        val childOption = children.get(name)
        childOption.foreach(childRef => context.stop(childRef))
      //context.become(withChildren(children + (name -> context.actorOf(Props[Child], name))))
      case Stop =>
        log.info("stopping ")
        context.stop(self)
    }

    override def receive: Receive = withChildren(Map[String, ActorRef]())
  }

  val watcher = actorSystem.actorOf(Props[Watcher], "watcher")
  watcher ! StartChild("watchedchild")
  val watchedChild = actorSystem.actorSelection("/user/watcher/watchedchild")
  Thread.sleep(2000)
  watchedChild ! PoisonPill

}
