package exercises.Concurrency

//threads cannot
//threads are active but cannot continue
//no threads are blocked
//no threads are yeilding each other for execution
object Livelock extends App {
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
    var side = "right"
    def switchside = {
      if(side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend) = {
      while(this.side == other.side) {
        println(s"$this: Oh! $other Please feel free to pass....")
        switchside
        Thread.sleep(1000)
      }
    }
  }

  val ram = Friend("ram")
  val siva = Friend("siva")

  new Thread(() => siva.pass(ram)).start()
  new Thread(() => ram.pass(siva)).start()
}
