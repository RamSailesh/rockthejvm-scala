package exercises.Concurrency

//threads waiting for resource to execute
object Deadlock extends App {
  case class Friend(name: String) {
    def bow(other: Friend)= {
      this.synchronized {
        println(s"${this.name}: I'm bowing to ${other.name}")
        other.rise(this)
        println(s"${this.name}: My friend ${other.name} has risen")
      }
    }
    def rise(other: Friend) = {
      this.synchronized {
        println(s"${this.name}: I'm rising to ${other.name}")
      }
    }
  }

  val ram = Friend("ram")
  val siva = Friend("siva")

  new Thread(() => siva.bow(ram)).start()
  new Thread(() => ram.bow(siva)).start()
}
