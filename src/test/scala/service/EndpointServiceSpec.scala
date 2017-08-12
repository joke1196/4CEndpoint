/**
  * Created by David on 12.08.2017.
  */
import org.specs2.mutable.Specification
import service.EndpointService
import spray.http
import spray.http.{HttpEntity, MediaTypes}
import spray.http.StatusCodes._
import spray.json.{JsObject, JsString}
import spray.testkit.Specs2RouteTest

class EndpointServiceSpec extends Specification with Specs2RouteTest with EndpointService {
  def actorRefFactory = system
  sequential
  "EndpointService" should {

    "return not found if a get is perform on an empty or partially empty diff id" in {
      Get("/v1/diff/1") ~> route ~> check {
        response.status mustEqual NotFound
      }
      Put("/v1/diff/1/left", HttpEntity(MediaTypes.`application/json`, """{ "data": "AAAAAA==" }""")) ~>
        route ~> check {}
      Get("/v1/diff/1") ~> route ~> check {
        response.status mustEqual NotFound
      }
    }

    "return a Created for PUT requests to the correct path with data" in {
      Put("/v1/diff/1/left", HttpEntity(MediaTypes.`application/json`, """{ "data": "AAAAAA==" }""")) ~>
        route ~> check {
        response.status mustEqual Created
      }
    }
    "return a BadRequest for PUT requests with null data" in {
      Put("/v1/diff/1/left",   HttpEntity(MediaTypes.`application/json`,"""{ "data": null }""" )) ~>
        route ~> check {
        response.status mustEqual BadRequest
      }
    }
    "return a BadRequest for PUT requests with data which are not Base64 encoded" in {
      Put("/v1/diff/1/left", HttpEntity(MediaTypes.`application/json`, """{ "data": "?090$@Afds==" }""")) ~>
        route ~> check {
        response.status mustEqual BadRequest
      }
    }
    "return sizeDoNotMatch if diff have different sizes" in {

      Put("/v1/diff/1/left", HttpEntity(MediaTypes.`application/json`, """{ "data": "AAAAAA==" }""")) ~>
        route ~> check {}
      Put("/v1/diff/1/right", HttpEntity(MediaTypes.`application/json`, """{ "data": "AA==" }""")) ~>
        route ~> check {}
      Get("/v1/diff/1") ~> route ~> check {
        responseAs[String] must contain("SizeDoNotMatch")
      }
    }
    "return the contentMatch if data are a match" in {

      Put("/v1/diff/1/left", HttpEntity(MediaTypes.`application/json`, """{ "data": "AAAAAA==" }""")) ~>
        route ~> check {}
      Put("/v1/diff/1/right", HttpEntity(MediaTypes.`application/json`, """{ "data": "AAAAAA==" }""")) ~>
        route ~> check {}
      Get("/v1/diff/1") ~> route ~> check {
        responseAs[String] must contain("Equals")
      }
    }
    "return the difference if data are not a match" in {
      Put("/v1/diff/2/left", HttpEntity(MediaTypes.`application/json`, """{ "data": "AAAAAA==" }""")) ~>
        route ~> check {}
      Put("/v1/diff/2/right", HttpEntity(MediaTypes.`application/json`, """{ "data": "AQABAQ==" }""")) ~>
        route ~> check {}
      Get("/v1/diff/2") ~> route ~> check {
        responseAs[String] must contain("ContentDoNotMatch")
        responseAs[String] must contain("offset")
      }
    }

  }
}