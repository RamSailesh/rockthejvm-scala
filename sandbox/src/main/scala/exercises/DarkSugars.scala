package exercises

object DarkSugars extends App {
  // methods with single parameters
  def greeting(name: String) = s"Hi! I'm $name"

  val description = greeting {
    //this block of code is executed in apply method
    "Ram"
  }
  println(description)

  print(List(1,2,3).map { x =>
    x+1
  })

  //single abstract method
  //if trait/abstract class has single not implemented method, anonymous function can be passed to create concrete implementations
  trait Action {
    def act(x: Int): Int
  }

  val anInstance = new Action {
    override def act(x: Int): Int = x+1
  }

  val anInstance1:Action = (x: Int) => x+1

  val newThread = new Thread(() => println("doing work"))

  // :: and #::
  val prependedList = 2 :: List(1,3)
  val prependedListSame = List(1,3).::(2)

  class MyStream[T] {
    def -->:(value: T) : MyStream[T] = this
  }
  val stream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  //mult-word methodnaming using back-tick `
  class TeenGirl(name: String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }
  val lilly = new TeenGirl("Lilly")
  lilly `and then said` ("Scala is sweet")

  //Infix types
  class -->[A,B]
  //val towards: Int --> String = ???

  //update method
  //used in mutable collections
  val someArray = Array(1,2,3)
  someArray(2) = 7
  someArray.update(2, 7)

  //setters for mutable collections
  class MutableInt {
    private var internalMember:Int = 0
    def member = internalMember
    def member_=(value:Int):Unit = internalMember = value
  }

  val someInt = new MutableInt
  println(someInt.member)
  someInt.member = 12
  println(someInt.member)

}
