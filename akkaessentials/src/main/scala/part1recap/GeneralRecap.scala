package part1recap

object GeneralRecap extends App {

  val res = for {
    n <- 1 to 10
    c <- List("A", "B", "C")
  } yield n + '-' + c

  val anotherres = (1 to 10).flatMap(n => List("A", "B", "C").map(c => n + '-' + c))

  println(res)
  println(anotherres)


  case class Person(name: String, age:Int)
  val ram = Person("ram", 30)

  ram match {
    case Person(_ , x) => println(x)
    case _ => ()
  }

}
