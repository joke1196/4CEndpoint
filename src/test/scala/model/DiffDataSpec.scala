package model

import org.specs2.mutable._
/**
  * Created by David on 12.08.2017.
  */
object DiffDataSpec extends Specification {
  "compute diff " should {
    "return lists of differences" in {
      val left = RequestData("AAAAAA==")
      val right = RequestData("AQABAQ==")
      DiffData.computeDiff(left,right) mustEqual List(Diff(0,1), Diff(2,2))
    }
    "return an empty list if the data match" in {
      val left = RequestData("AAAAAA==")
      val right = RequestData("AAAAAA==")
      DiffData.computeDiff(left,right) mustEqual List()
    }
  }

}
