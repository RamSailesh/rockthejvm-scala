package exercises

object Basics extends App {
  // instructions vs expressions
  val aCondition: Boolean = false
  val aBlock = {
    if(aCondition) 10
    else 20
  }
  // type-inference -> compiler infers type
  val num = 20

  // Unit return type equivalent to void
  val result = println("hello")

  //functions
  def aFunction(x: Int): Int = x+1

  //recursion and @tailrec
  //TBD

  //OOPS
  //classes
  //object -> singleton object for the class
  //trait -> abstract data type, behaves like an abstract class, class can extend multiple traits
  //companion objects -> class and object with same name
  class Person(val name: String) {
  }
  object Person {
    def apply(name: String): Person = new Person(name)
  }
  case class Student(name: String)

  val ram = new Person("ram")
  val sailesh = Person("sailesh")
  val s1 = Student("rs")

  // method notations (syntactic sugars)
  // infix, prefix and postfix notations
  // operators are methods (+, / - etc)

  //anonymous classes
  trait Animal {
    def eat(): String
  }
  val dog = new Animal {
    override def eat(): String = "crunch crunch"
  }

  //generics
  //covariance +A
  //contravariance A+
  //invariance A
  // see List source code

  //case classes
  //serializable, all parameters are fields, default apply method, tostring, equals etc

  //exceptions - try, catch, finally
  //try and catch blocks can return a value

  //FP
  //Apply methods -> classes/instances are called as functions
  //Functions are objects and has apply methods

  val inc = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }
  //Lambda functions
  val incrementer = (x: Int) => x+1

  //HOF -> functions return another function or take function as parameter
  //Currying

  //map, flatMap, filter
  // for comprehension
  // Collections -> Seq, Array, List, Vector, Map, Tuple
  // Options (Some and None), Try (Success, Failure)
  val option = Some(2)
  val nulloption = None

  // pattern matching using match keyword
  // formatting strings using s interpolation s"$name"
}
