package exercises

object PartialFunctions extends App {
  val aFunction = (x: Int) => x+1

  //restrict function to be called for numbers between 1 and 5
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 12
  }

  println(aPartialFunction(1))
  println(aPartialFunction.isDefinedAt(12))
  println(aPartialFunction.isDefinedAt(1))

  val lifted = aPartialFunction.lift
  println(lifted(1))
  println(lifted(5))
  //println(aPartialFunction(5)) // crash


  //Exercise
  // Construct PF instance yourself
  //
  val anonymousPF = new PartialFunction[Int, Int] {
    override def apply(v1: Int):Int = v1 match {
      case 1 => 2
      case 2 => 2
    }

    override def isDefinedAt(x: Int): Boolean = {
      x == 1 || x == 2
    }
  }

  println(anonymousPF(2))

  //chatbot as PF

  val chatbot = new PartialFunction[String, String] {
    override def apply(v1: String): String = v1 match {
      case "hi" => "hi my name is jarvis"
      case "goodbye" => "bye bye"
    }

    override def isDefinedAt(x: String): Boolean = true
  }

  scala.io.Source.stdin.getLines().map(chatbot).foreach(println)
}
