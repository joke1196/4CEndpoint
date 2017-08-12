package model

import java.util.Base64

import spray.http.{HttpEntity, HttpResponse}
import spray.http.StatusCodes._
import spray.json._


/**
  * Created by David on 08.08.2017.
  */
/**
  * The representation of the data in our "database"
  */
case class DiffData(left:Option[RequestData] = None, right:Option[RequestData] = None){


  def isDefined = (left,right) match {
    case (Some(_), Some(_)) => true
    case _ => false
  }


}

object DiffData {
  /**
    * Performs check if the data is valid to perform a diff on it or returns a NotFound if the data is not complete
    * or returns a SizeDoNotMatch if the data sizes are different
   */
  def diff(diffData: DiffData): HttpResponse ={
    val data = for {
      left <- diffData.left
      right <- diffData.right
    } yield (left, right)

    data match {
      case Some((left, right)) if left.data.length != right.data.length =>
        HttpResponse(OK,  ResultData(diffResultType = ResultData.sizeDoNotMatch).toJson.toString)
      case Some((left, right)) =>
        getDiffResult(left, right)
      case _ => HttpResponse(NotFound)
    }
  }

  /**
    * The data passed into getDiffResult must not be empty and must be of same size
    * return an HttpResponse with either the data are a match or not
    */
  def getDiffResult(left:RequestData, right:RequestData):HttpResponse = {

     computeDiff(left, right) match {
      case Nil => HttpResponse(OK, HttpEntity(ResultData(ResultData.contentMatch).toJson.toString))
      case l => HttpResponse(OK, ResultData(ResultData.contentDoNotMatch, Some(l)).toJson.toString)
    }

  }

  /**
    * Perform the difference between the data
    * Decoding the data could cause memory problem and looses the advantages of the encoding
    * A better way would be to iterate on the data without having to decode the whole string but only part of it
    * Creates a list of Diff with the differences lengths and offsets
    */
  def computeDiff(left:RequestData, right:RequestData):List[Diff] = {
    val decodeLeft:Array[Int] = Base64.getDecoder.decode(left.data).map(_.toInt)
    val decodeRight:Array[Int] = Base64.getDecoder.decode(right.data).map(_.toInt)

    var map = Map[Int,Int]()
    var current = 0
    for( i <- decodeLeft.indices){
      if(decodeLeft(i) != decodeRight(i)){
        map = map.updated(current, map.getOrElse(current, 0) + 1)
      }else{
        current = i + 1
      }
    }
    map.map( tuple => model.Diff(tuple._1, tuple._2) ).toList
  }
}

