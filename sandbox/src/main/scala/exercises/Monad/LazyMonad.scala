package exercises.Monad

class LazyMonad [+A](value: => A) {
  def use:A = internalValue
  private lazy val internalValue = value
  def flatMap[B](f: (=>A) => LazyMonad[B]): LazyMonad[B] = f(value)
}

object LazyMonad {
  def apply[A](value: => A): LazyMonad[A] = new LazyMonad(value)
}

object LazyMonadRunner extends App {
  val lazyMonad = LazyMonad{
    println("somedummy print which should not be printed now")
    12
  }
  //println(lazyMonad.use)

  val someValue = lazyMonad.flatMap(x =>  LazyMonad{
    11*x
  })

  lazyMonad.use
  someValue.use

}