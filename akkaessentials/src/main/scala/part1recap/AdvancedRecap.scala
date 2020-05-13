package part1recap

object AdvancedRecap extends App {
  // partial functions will work only for a subset of input values
  // partial functions will not provide an answer for every possible input
  // collections with collect method use partial functions will take only subset values

  val partialFunction : PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 65
    case 5 => 999
  }

  println(List(1,2,5,10).collect(partialFunction))

  println(List(1,2,5,10).collect(new PartialFunction[Int, Int] {
    override def apply(v1: Int): Int = v1 match {
      case i:Int if (i % 2 == 0) => i*2
    }
    override def isDefinedAt(x: Int): Boolean = x%2 == 0
  }))

  val pf = (x:Int) => x match {
    case 1 => 42
    case 2 => 65
    case 5 => 999
  }

  //lifting
  val lifted = partialFunction.lift // convert pf to total function, will return Option[Int]
  println(lifted(2))

  //extending the pf will be called if it doesnt match
  val pfChained = partialFunction.orElse[Int, Int] {
    case 7 => 100
  }

  //type aliases
  type ReceiveFunction = PartialFunction[Any, Unit]

  def receive: ReceiveFunction = {
    case 1 => println("receive")
    case _ => ()
  }

  //implicit
  implicit val timeout = 3000
  def setTimeout (f: () => Unit)(implicit timeout:Int) = {
    println(s"sleeping for $timeout")
    f
  }

  setTimeout(() => println("timeout"))
  setTimeout(() => println("timeout"))(200)


  //implicits
  //Write on your own exercises
  //define implict def, and class
  //scope of the defined implicits
}
