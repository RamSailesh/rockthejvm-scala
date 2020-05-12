package exercises.Implicits

object PimpmyLibrary extends App {

  //extending existing classes using implicits
  implicit class RichInt(value: Int) {
    def isEven = value % 2 == 0

    def squareRoot = Math.sqrt(value)

    def times(function: () => Unit) = {
      def timesAuxilary(n: Int): Unit = {
        if (n < 0) ()
        else {
          function
          timesAuxilary(n - 1)
        }
      }
    }

    def *[T](list: List[T]): List[T] = {
      def concatanate(n: Int): List[T] = {
        if (n < 0) List()
        else concatanate(n - 1) ++ list
      }

      concatanate(value)
    }

  }

  println(12.isEven)
  println(49.squareRoot)
  3.times(() => println("scala rocks"))
  println(4 * List(1, 2))

  //1 to 10
  // 3.seconds
object stringextender {
  implicit class StringExtender(value: String) {
    def asInt: Int = Integer.valueOf(value)

    def encrypt: String = value.toCharArray.map(x => (x.toInt + 3).toChar).mkString("")
  }

  println("3".asInt + 4)
  println("John".encrypt)
}

  implicit def stringToInt(value: String): Int = {
    Integer.valueOf(value)
  }






}
