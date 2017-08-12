import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import service.{EndpointServiceActorV1}
import spray.can.Http

import scala.concurrent.duration._

/**
  * Created by David on 08.08.2017.
  * Based on Spray Template:
  * https://github.com/spray/spray-template/tree/on_spray-can_1.3_scala-2.11
  */
object EndpointMain extends App{
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("spray-actorsystem")

  // create and start our service actor
  val service = system.actorOf(Props[EndpointServiceActorV1], "endpoint-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)

}
