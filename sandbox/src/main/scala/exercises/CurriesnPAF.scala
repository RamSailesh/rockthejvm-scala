package exercises

object CurriesnPAF extends App {
  //curried function
  //multiple parameter lists
  val superAddr: (Int => Int => Int) = x => y => x+y
  val add3 = superAddr(3)
  println(add3(8))
  println(superAddr(3)(5))

  //method
  def curriedAdder(x: Int)(y: Int): Int = x+y
  // lifting (conversion of method to function or ETA expansion
  // functions != methods
  val add4: Int => Int = curriedAdder(4)

  val add5 = curriedAdder(5) _ // ETA Expansion -> informing compiler to convert method to function


  val simpleAddFunction = (x: Int, y: Int) => x+y
  def simpleAddMethod(x: Int, y:Int) = x+y
  def curriedAddMethod(x: Int)(y: Int) = x+y

  //add7 : Int -> Int = y => 7+y from above simpleAddFunction

  val add7 = (x: Int) => simpleAddFunction(7, x)
  val add7_2 = simpleAddFunction.curried(7)
  val add7_3 = curriedAddMethod(7) _ //ETA
  val add7_4 = curriedAddMethod(7)(_) //

  //ETA Expansion using _
  val add7_5 = simpleAddMethod(7, _:Int)
  val add7_6 = simpleAddFunction(7, _:Int)

  def concataner(a: String, b: String, c: String) = a+b+c
  val greeting = concataner("Hello ", _:String , " How are you")
  println(greeting("Ram"))

  //Process List of numbers and return their string representations different formats
  //%4.2f, %8.6f and %14.12f with curried formatter
  def curriedFormatter(s: String)(number: Double):String = s.format(number)
  val numbers = List[Double](Math.PI,Math.E,1,9.8)
  val simpleFormatter = curriedFormatter("%4.2f") _
  val seriousFormatter = curriedFormatter("%8.6f") _
  val preciseFormatter = curriedFormatter("%14.12d") _

  println(numbers.map(simpleFormatter))


  //difference between
  // functions vs methods
  // parameters byname vs 0-lamda
  def byName(n: => Int) = n+1
  def byFunction(f: () => Int) = f() + 1

  def method:Int = 42 // mthod return 42
  def method2():Int = 42 //method

  byName(12)
  byName(method)
  byName(method2) // equals byName(method2())
  // byName(()=>2) wont work, byName expects Int as parameter

  byFunction(method2) // compiler does ETA Expansion
  byFunction(()=>12)
  byFunction(method2 _) // compiler does ETA Expansion


  def checkStraightLine(coordinates: Array[Array[Int]]): Boolean = {
    def isStraightLine(p1: Array[Int], p2: Array[Int])(p3: Array[Int]):Boolean = {
      (p3(1)-p1(1))*(p2(0)-p1(0)) - (p3(0)-p1(0))*(p2(1)-p1(1)) == 0
    }
    if (coordinates.length > 2) {
      val firstwoPoints = coordinates.take(2)
      val isStraightLinePoints: Array[Int] => Boolean = isStraightLine(firstwoPoints(0), firstwoPoints(1))
      coordinates.takeRight(coordinates.length - 2).foldLeft(true) {
        (accumulator, point) => {
          if (!accumulator) accumulator
          else isStraightLinePoints(point)
        }
      }
    } else true
  }
}
