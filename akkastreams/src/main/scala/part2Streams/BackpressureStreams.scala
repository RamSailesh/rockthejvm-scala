package part2Streams

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}

object BackpressureStreams extends App {
  implicit val system = ActorSystem("BackPressureStreams")
  implicit val materialzier = ActorMaterializer()

  //elements flow w.r.t demand of consumer
  //sink should demand the flow

  val fastSource = Source(1 to 1000)
  val slowSink = Sink.foreach[Int](
    x => {
      Thread.sleep(1000)
      println(s"Sink $x")
    }
  )

  //fastSource.to(slowSink).run() no-backpressure
  //fastSource.async.to(slowSink).run() //backpressure applied

  val simpleflow = Flow[Int].map {
    x => {
      println(s"Flow $x")
      x
    }
  }

  //fastSource.async.via(simpleflow).async.to(slowSink).run() //back-pressure-applied, default buffer is 16 elements

  val bufferedFlow = simpleflow.buffer(10, OverflowStrategy.backpressure)
  //fastSource.async.via(bufferedFlow).async.to(slowSink).run()

  //dropHead -> drops oldest element
  //droptail -> Drops new element
  //dropNew -> drops current Element

  //throttling, controlling at source level
  import scala.concurrent.duration._
  fastSource.throttle(10, 1 second).runWith(Sink.foreach(println))
}
