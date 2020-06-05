package part2Streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Source, Sink, Flow}

object OperatorFusion extends App {
  implicit val system = ActorSystem("operator-fusion")
  implicit val  materializer = ActorMaterializer()

  val simpleSource = Source(1 to 1000)
  val simpleFlow = Flow[Int].map(x => x+1)
  val simpleFlow2 = Flow[Int].map(_*10)
  val simpleSink = Sink.foreach[Int](println)

  //all the connections run on the same actor
//  val graph =
//    simpleSource.via(simpleFlow).via(simpleFlow2).to(simpleSink).run()

  val complexFlow = Flow[Int].map(x => {
    Thread.sleep(1000)
    x + 1
  })

  val complexFlow2 = Flow[Int].map(x => {
    Thread.sleep(1000)
    x * 10
  })


  //this will take time 2 seconds gap is present per print
  //simpleSource.via(complexFlow).via(complexFlow2).to(simpleSink).run()


  //async boundary, 1 second gap
//  simpleSource.via(complexFlow).async
//    .via(complexFlow2).async
//    .to(simpleSink).run()


  //ordering guarantees
  //operations are executed in inorder
  Source(1 to 3)
    .map(element => {println(s" Flow A $element");element}).async
    .map(element => {println(s" Flow B $element");element}).async
    .map(element => {println(s" Flow C $element");element}).async
    .runWith(Sink.ignore)
}
