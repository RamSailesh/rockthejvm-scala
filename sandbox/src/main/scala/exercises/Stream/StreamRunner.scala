package exercises.Stream

object StreamRunner extends App {
  val naturalNumbers = MyStream.from(1)(_ + 1)
  //println(naturalNumbers.head)
  //println(naturalNumbers.tail.head)

  val startFrom0 = 0 #:: naturalNumbers
  //println(startFrom0.head)

  //startFrom0.take(3).foreach(println)

  val evenNumbers = startFrom0.map(_*2)
  //evenNumbers.take(3).foreach(println)

  val flatMappedNumbers = startFrom0.flatMap(x => new Cons(x, new Cons(x*11, EmptyStream))).takeAsList(100)
  //flatMappedNumbers.foreach(println)

  val filter = startFrom0.filter(_ < 10).take(10).take(20).toList()
  println(filter)

  val fibonacciStream = naturalNumbers.map(fibonacciNumber)
  println(fibonacciStream.takeAsList(5))
  def fibonacciNumber(n: Int): Int = {
    def fibonacciNumberUtil(n: Int, a: Int, b: Int): Int = {
      if (n == 0) a
      else if (n ==1) b
      else fibonacciNumberUtil(n-1, b, a+b)
    }
    fibonacciNumberUtil(n, 0, 1)
  }

  def fibonacciStream(first: Int, second: Int): MyStream[Int] = {
    new Cons[Int](first, fibonacciStream(second, first + second))
  }
  println(fibonacciStream(1,1).takeAsList(5))

  val primeNumberStream = naturalNumbers.filter(isPrimeNumber)
  println(primeNumberStream.takeAsList(10))
  def isPrimeNumber(n: Int):Boolean = {
    def isPrimeNumberUtil(x:Int, limit:Int): Boolean = {
      if (n%x == 0) false
      else if(x >= limit) true
      else isPrimeNumberUtil(x+1, limit)
    }
    if (n == 1) false
    else if (n == 2) true
    else isPrimeNumberUtil(2, n/2)
  }

  //eratosthenes sieve
  def primeNumberSeries(numbers: MyStream[Int]): MyStream[Int] = {
    if(numbers.isEmpty) numbers
    else new Cons[Int](numbers.head, primeNumberSeries(numbers.tail.filter(_%numbers.head!=0)))
  }
  println(primeNumberSeries(MyStream.from(2)(_+1)).takeAsList(10))

}
