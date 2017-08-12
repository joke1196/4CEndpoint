package service

import DAO.EndpointDAO
import akka.actor.Actor
import spray.routing._
import spray.http.StatusCodes._
import spray.http.MediaTypes._
import spray.http._
import model._
import spray.json.{JsNull, JsObject, JsValue}


// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class EndpointServiceActorV1 extends Actor with EndpointService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)


}

object EndpointService{
  /**
    * Get the data by its id
    * If the data is complete a comparison between the two data is done and the result is returned
    * @param id the id of the data to fetch
    * @return the result of the difference between the two data, or a NotFound Status if the data is not complete
    */
  def getData(id:Long):HttpResponse = {
    EndpointDAO.get(id).map{ data =>
      DiffData.diff(data)
    }.getOrElse(HttpResponse(NotFound))
  }

  /**
    * Insert the data to the database
    * @param data data to insert
    * @param id id of the data
    * @param either  if the data comes from the right or left endpoint
    * @return a HttpResponse if the data is insert
    */
  def putData(data:RequestData, id:Long, either: RequestType):HttpResponse = {
    either match{
      case LeftRequest =>
        val currentData = EndpointDAO.get(id).map(diffData => diffData.copy(left = Some(data))).getOrElse(DiffData(left = Some(data)))
        EndpointDAO.push(id, currentData)
        HttpResponse(Created)
      case RightRequest =>
        val currentData = EndpointDAO.get(id).map(diffData => diffData.copy(right = Some(data))).getOrElse(DiffData(right = Some(data)))
        EndpointDAO.push(id, currentData)
        HttpResponse(Created)
    }

  }
}


// this trait defines our service behavior
trait EndpointService extends HttpService {
  import RequestDataProtocol._
  def pathPrefix:String = "v1"
  def route:Route =
    pathPrefix(pathPrefix / "diff" / LongNumber) { id =>
      pathEnd{
        get {
          respondWithMediaType(`application/json`) {
            complete {
              EndpointService.getData(id)
            }
          }
        }
      }~
      path(Segment){ string =>
        put{
          entity(as[JsObject]){ data =>
            complete{
              data.fields.get("data") match{
                case Some(JsNull) =>  HttpResponse(BadRequest)
                case Some(d) =>
                  string match{
                  case "left" => EndpointService.putData(RequestData(d.convertTo[String]), id, LeftRequest)
                  case "right" => EndpointService.putData(RequestData(d.convertTo[String]), id, RightRequest)
                  case _ => HttpResponse(NotFound)
                }
                case None => HttpResponse(BadRequest)
              }

            }
          }
        }

      }


    }

}