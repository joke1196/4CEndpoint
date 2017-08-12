package model

import model.ResultData.ResultType
import spray.httpx.SprayJsonSupport
import spray.json._


/**
  * Created by David on 08.08.2017.
  */

case class Diff(offset:Int, length:Int)

/**
  * The data that will be served back to the client
  */
case class ResultData(diffResultType: ResultType = "", diffs: Option[List[Diff]] = None)


object ResultData extends DefaultJsonProtocol with SprayJsonSupport{
  type ResultType = String
  val sizeDoNotMatch :ResultType= "SizeDoNotMatch"
  val contentMatch:ResultType = "Equals"
  val contentDoNotMatch:ResultType = "ContentDoNotMatch"
  implicit val diffFormat = jsonFormat2(Diff.apply)
  implicit val resultDataFormat = jsonFormat2(ResultData.apply)

}



