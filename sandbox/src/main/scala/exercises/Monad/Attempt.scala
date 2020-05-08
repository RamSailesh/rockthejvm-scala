package exercises.Monad

/* left-identity
  unit applied to flatmap returns unit => unit.flatMap(f) = f(x)
  Attempt(x).flatMap(f) = f(x)
  Success(x).flatMap(f)
  f(x)
*/

/* right-identity
   attempt.flatMap(unit) = attempt

   Attempt(x).flatMap(unit)
   Success(x).flatMap(x => Attempt(x))
   Attempt(x)
   Success(x)
*/

/*
  Associativity
  Attempt(x).flatMap(f).flatMap(g) == Attempt(x).flatMap(x => f(x).flatMap(g))

  Success(x).flatMap(f).flatMap(g)
  f(x).flatMap(g)

  Attempt(x).flatMap(x => f(x).flatMap(g))
  Success(x).flatMap(x => f(x).flatMap(g))
  f(x).flatMap(g)

 */

trait Attempt[+A] {
  def flatMap[B] (f: A => Attempt[B]): Attempt[B]
}

object Attempt {
  def apply[A] (a: => A): Attempt[A] = {
    try {
      Success(a)
    } catch {
      case e: Throwable => Failure(e)
    }
  }
}

case class Success[+A](value: A) extends Attempt[A] {
  def flatMap[B](f: A => Attempt[B]): Attempt[B] = {
    try {
      f(value)
    } catch {
      case e: Throwable => Failure(e)
    }
  }
}

case class Failure(exception: Throwable) extends Attempt[Nothing] {
  def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
}

object AttemptRunner extends App {
  val attempt = Attempt {
    throw new RuntimeException("blah blah blah")
  }
  println(attempt)
}