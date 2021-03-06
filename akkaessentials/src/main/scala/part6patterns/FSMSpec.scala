package part6patterns

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Cancellable, FSM, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import part6patterns.FSMSpec.{RequestProduct, VendingMachine}

class FSMSpec extends TestKit(ActorSystem("FSMSpec"))
with WordSpecLike
with ImplicitSender
with BeforeAndAfterAll {
  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  import FSMSpec._

  def runTestSuite(props: Props) = {
    "error when not initilized" in {
      val vendingmachine = system.actorOf(props)
      vendingmachine ! RequestProduct("coke")
      expectMsg(VendingError("Vending machine not initalized "))
    }

    "report prodoct is not available" in {
      val vendingmachine = system.actorOf(props)
      vendingmachine ! Initialize(
        Map[String, Int](
          "coke" -> 10,
          "sprite" -> 10
        ),
        Map[String, Int](
          "coke" -> 1,
          "sprite" -> 2
        )
      )
      vendingmachine ! RequestProduct("pepsi")
      expectMsg(VendingError(s"pepsi is not found or ran out of stock"))
    }

    "throw timeout if i dont insert money" in {
      val vendingmachine = system.actorOf(props)
      vendingmachine ! Initialize(
        Map[String, Int](
          "coke" -> 10,
          "sprite" -> 10
        ),
        Map[String, Int](
          "coke" -> 1,
          "sprite" -> 2
        )
      )
      vendingmachine ! RequestProduct("coke")
      expectMsg(Instruction(s"coke costs 1. please input money"))
      Thread.sleep(1500)
      expectMsg(ReceiveMoneyTimeout)
    }

    "deliver product and ask for remaining money" in {
      val vendingmachine = system.actorOf(props)
      vendingmachine ! Initialize(
        Map[String, Int](
          "coke" -> 10,
          "sprite" -> 10
        ),
        Map[String, Int](
          "coke" -> 1,
          "sprite" -> 2
        )
      )
      vendingmachine ! RequestProduct("sprite")
      expectMsg(Instruction(s"sprite costs 2. please input money"))
      vendingmachine ! ReceiveMoney(1)
      expectMsg(Instruction(s"sprite costs 2. please input remaining money. 1"))
      vendingmachine ! ReceiveMoney(1)
      expectMsg(Deliver("sprite"))
    }

    "deliver product and return change" in {
      val vendingmachine = system.actorOf(props)
      vendingmachine ! Initialize(
        Map[String, Int](
          "coke" -> 10,
          "sprite" -> 10
        ),
        Map[String, Int](
          "coke" -> 1,
          "sprite" -> 2
        )
      )
      vendingmachine ! RequestProduct("sprite")
      expectMsg(Instruction(s"sprite costs 2. please input money"))
      vendingmachine ! ReceiveMoney(10)
      expectMsg(Deliver("sprite"))
      expectMsg(GiveBackChange(8))
    }

    "an annoying customer buys a 10 dollar product and puts 1 dollar 1 dollar nd last 100 dollar :P" in {
      val vendingmachine = system.actorOf(props)
      vendingmachine ! Initialize(
        Map[String, Int](
          "coke" -> 10,
          "sprite" -> 10
        ),
        Map[String, Int](
          "coke" -> 10,
          "sprite" -> 2
        )
      )
      vendingmachine ! RequestProduct("coke")
      expectMsg(Instruction(s"coke costs 10. please input money"))
      for (i <- 1 until 10) {
        val remainngAmount = 10 - i
        vendingmachine ! ReceiveMoney(1)
        expectMsg(Instruction(s"coke costs 10. please input remaining money. $remainngAmount"))
      }
      vendingmachine ! ReceiveMoney(100)
      expectMsg(Deliver("coke"))
      expectMsg(GiveBackChange(99))
    }
  }


  "A vending machine" should {
    runTestSuite(Props[VendingMachine])
  }

  "A vending machineFSM " should {
    runTestSuite(Props[VendingMachineFSM])
  }

}

object FSMSpec {

  case class Initialize(inventory: Map[String, Int], prices: Map[String, Int])
  case class RequestProduct(product: String)
  case class Instruction(instruction: String) // message the vending machine will show on screen
  case class ReceiveMoney(amount: Int)
  case class GiveBackChange(returnAmount: Int)
  case class Deliver(product: String)

  case class VendingError(reason: String)
  case object ReceiveMoneyTimeout

  import scala.concurrent.duration._

  trait VendingState
  case object Idle extends VendingState
  case object Operational extends VendingState
  case object WaitforMoney extends VendingState

  trait VendingData
  case object UnInitialized extends VendingData
  case object ReceiveMoneyTimeoutData extends VendingData
  case class Initialized(inventory: Map[String, Int], prices: Map[String, Int]) extends VendingData
  case class WaitforMoneyData(inventory: Map[String, Int],
                          prices: Map[String, Int],
                          product:String,
                          moneyInserted: Int,
                          sender: ActorRef
                         ) extends VendingData

  //handle events instead of
  class VendingMachineFSM extends FSM[VendingState, VendingData] {
    // no receive handler
    startWith(Idle, UnInitialized)
    when(Idle) {
      case Event(Initialize(inventory: Map[String, Int], prices: Map[String, Int]), UnInitialized) =>
        goto(Operational) using Initialized(inventory, prices)
      case _ =>
        sender() ! VendingError("Vending machine not initalized ")
        stay()
    }

    when(Operational) {
      case Event(RequestProduct(product:String), Initialized(inventory: Map[String, Int], prices: Map[String, Int])) => {
        val itemCount = inventory.getOrElse(product, 0)
        val price = prices.getOrElse(product, 0)
        if (itemCount > 0) {
          sender() ! Instruction(s"$product costs $price. please input money")
          goto(WaitforMoney) using WaitforMoneyData(inventory, prices, product, 0, sender())
        }
        else  {
          sender() ! VendingError(s"$product is not found or ran out of stock")
          stay()
        }
      }
    }

    when(WaitforMoney, stateTimeout = 1 second) {
      case Event(StateTimeout, WaitforMoneyData(inventory: Map[String, Int],
      prices: Map[String, Int],
      product:String,
      moneyInserted: Int,
      sender: ActorRef
      )) =>
        if (moneyInserted > 0) sender ! GiveBackChange(moneyInserted)
        else sender ! ReceiveMoneyTimeout//("Request timedout")
        goto(Operational) using Initialized(inventory, prices)

      case Event(ReceiveMoney(amount:Int), WaitforMoneyData(inventory: Map[String, Int],
      prices: Map[String, Int],
      product:String,
      moneyInserted: Int,
      sender: ActorRef
      )) =>
        val price = prices.getOrElse(product, 0)
        val diff = moneyInserted + amount - price
        if (diff >= 0) {
          sender ! Deliver(product)
          if (diff > 0) sender ! GiveBackChange(diff)
          goto(Operational) using Initialized(inventory + (product -> (inventory.getOrElse(product, 1) - 1)), prices)
        } else {
          sender ! Instruction(s"$product costs $price. please input remaining money. ${-1*diff}")
          stay() using
          WaitforMoneyData(inventory,
            prices,
            product,
            moneyInserted + amount,
            sender
          )
        }
    }
  }


  class VendingMachine extends Actor with ActorLogging {
    override def receive: Receive = idle
    implicit val executionContext = context.dispatcher

    def idle: Receive = {
      case Initialize(inventory: Map[String, Int], prices: Map[String, Int]) => context.become(operational(inventory, prices))
      case _ => sender() ! VendingError("Vending machine not initalized ")
    }

    def startReceiveMoneyTimeoutSchedule(sender: ActorRef):Cancellable = context.system.scheduler.scheduleOnce(1 second){
      sender ! ReceiveMoneyTimeout
    }

    def waitforMoney(inventory: Map[String, Int],
                     prices: Map[String, Int],
                     product:String,
                     moneyInserted: Int,
                     moneyTimeoutCancel: Cancellable,
                     sender: ActorRef
                    ): Receive = {
      case ReceiveMoneyTimeout =>
        if (moneyInserted > 0) sender ! GiveBackChange(moneyInserted)
        else sender ! VendingError("Request timedout")
        context.become(operational(inventory, prices))

      case ReceiveMoney(amount: Int) =>
      moneyTimeoutCancel.cancel()
      val price = prices.getOrElse(product, 0)
      val diff = moneyInserted + amount - price
      if (diff >= 0) {
        sender ! Deliver(product)
        if (diff > 0) sender ! GiveBackChange(diff)
        context.become(operational(inventory + (product -> (inventory.getOrElse(product, 1) - 1)), prices))
      } else {
        sender ! Instruction(s"$product costs $price. please input remaining money. ${-1*diff}")
        context.become(waitforMoney(inventory,
          prices,
          product,
          moneyInserted + amount,
          startReceiveMoneyTimeoutSchedule(sender),
          sender
        ))
      }

    }

    def operational(inventory: Map[String, Int], prices: Map[String, Int]): Receive = {
      case RequestProduct(product:String) =>
        val itemCount = inventory.getOrElse(product, 0)
        val price = prices.getOrElse(product, 0)
        if (itemCount > 0) {
          sender() ! Instruction(s"$product costs $price. please input money")
          context.become(waitforMoney(inventory,
            prices,
            product,
            0,
            startReceiveMoneyTimeoutSchedule(sender()),
            sender ()
          ))
        }
        else sender() ! VendingError(s"$product is not found or ran out of stock")

    }
  }
}