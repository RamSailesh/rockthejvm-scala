package exercises

object PatternMatching extends App {
  //constants
  //wildcards
  //caseclasses
  //tuples

  //make non case classes compatible to pattern matching
  class Person(val name: String, val age:Int)

  //PersonConverter should be renamed to Person so that it becomes companion object
  object PersonConverter {
    def unapply(arg: Person): Option[(String, Int)] =
      if (arg.age < 21) None
      else Some((arg.name, arg.age))
  }

  val bob = new Person("bob", 10)
  bob match {
    case PersonConverter(x,y) => println(s"Hi my name is $x")
    case _ => println("default")
  }


  //
  val n = 8
  val mathProperty = n match  {
    case x if x < 10 => "number is less than 10"
    case x if x %2 == 0 => "number is even"
    case _ => "no property"
  }

  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg < 10
  }

  val mathProp = n match {
    case even() => "number is even"
    case singleDigit() => "number is less than 10"
    case _ => "no property"
  }

  println(mathProp)
  println(mathProperty)

  //infix patterns
  case class Or[A, B](a: A, b:B)
  val either = Or(2, "two")
  val x =  either match {
    case number Or string => s"$number is written as $string"
  }
  println(x)

  //decomposing sequences
  val x1 = List(1,2,3) match {
    case List(1, _*) => "some list starts with 1"
  }
  println(x1)
}
