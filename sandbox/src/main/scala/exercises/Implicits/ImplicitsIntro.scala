package exercises.Implicits

object ImplicitsIntro extends App {
  val pair = "ram" -> "sailesh"

  case class Person(name: String) {
    def greet() = s"Hi 'm $name"
  }

  implicit def stringToPerson(str: String) = Person(str)

  println("Ram".greet) // string has greet method now !!!


  def increment(x: Int)(implicit amount: Int) = x +amount
  implicit val defaultAmount = 10
  println(increment(3)) // not default parameter, compiler will find the implicit value from the searchscope

  println(increment(3)(40))
}
