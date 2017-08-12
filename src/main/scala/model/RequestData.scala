package model
import spray.httpx.SprayJsonSupport
import spray.json._
/**
  * Created by David on 08.08.2017.
  */
case class RequestData(data:String)

object RequestDataProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val requestDataFormat = jsonFormat1(RequestData)
}

/**
  * Utility class to check if the data comes from let or right endpoint
  */
sealed trait RequestType
case object LeftRequest extends RequestType
case object RightRequest extends RequestType




