package DAO

import model.DiffData

/**
  * Created by David on 08.08.2017.
  * Access object of our database
  */
object EndpointDAO {
  var dataMap:Map[Long, DiffData] = Map()

  def get(key:Long) = dataMap.get(key)

  def push(key:Long, data:DiffData) = dataMap = dataMap + (key -> data)
}
