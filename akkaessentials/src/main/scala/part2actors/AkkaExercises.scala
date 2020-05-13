package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.scalactic.Fail
import part2actors.AkkaExercises.Bank.{Deposit, Success, Withdraw}

object AkkaExercises extends App {
  val actorSystem = ActorSystem("AkkaExercises")

  // Counter actor
  // - increment and decrement message
  // - Print its value


  //Domain
  object Counter {
    case class Increment(value: Int = 1)
    case class Decrement(value: Int = 1)
    case class Print()
  }

  class Counter extends Actor {
    import  Counter._
    private var currValue = 0
    override def receive: Receive = {
      case Increment(x) => currValue+=x
      case Decrement(x) => currValue-=x
      case Print => println(s"Curr Value $currValue")
    }
  }

  import  Counter._
  val counter = actorSystem.actorOf(Props[Counter])
  counter ! Increment(30)
  counter ! Decrement(1)
  counter ! Print

  /*
    Bank Account
    - Deposit   Respond with success or a failure
    - Withdraw  Respond with success or a failure
    - Statement

    - Interact with some other kind of actor
  */

  object Bank {
    case class Deposit(amount: Int)
    case class Withdraw(amount: Int)
    case class Statement()

    case class Success(message: String)
    case class Failure(message:String)
  }

  class Bank extends Actor {
    private var funds = 0
    import scala.collection.mutable.MutableList
    import Bank._
    private val log: MutableList[String] = MutableList[String]()

    override def receive: Receive = {
      case Statement => println(log); sender() ! s"Your funds are $funds"
      case Deposit(amount: Int) => funds += amount; log+=(s"Deposited $amount");sender() ! Success("Money deposited")
      case Withdraw(amount:Int) => {
        if (funds < amount) {
          log+=(s"Failed Withdrawal $amount"); sender()! Failure("Insufficient Funds")
        }
        else {
          funds -= amount;
          log += (s"Withdrawn $amount");
          sender() ! Success("Money Withdrawn")
        }
      }

    }
  }

  object User {
    case class LivetheLife(bank: ActorRef)
  }
  class User extends Actor {
    import User._
    import Bank._
    override def receive: Receive = {
      case LivetheLife(bank) => {
        bank ! Deposit(1000)
        bank ! Withdraw(100)
        bank ! Statement
        bank ! Withdraw(2000)
      }
      case message => println(message)
    }
  }

  val userRef = actorSystem.actorOf(Props[User])
  val bank = actorSystem.actorOf(Props[Bank])
  userRef ! User.LivetheLife(bank)
}
