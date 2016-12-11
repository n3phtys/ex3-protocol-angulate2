package nephtys.loom.frontend

import nephtys.loom.protocol.vanilla.solar.Abilities.SpecialtyAble


import angulate2.std._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js

import scala.scalajs.js

/**
  * Created by nephtys on 12/11/16.
  */
@Component(
  selector = "solar-specialties",
  template = "<h3>solar-specialties are here</h3>"
)
class SpecialtyComponent {

  var specialties : js.Array[(String, String)] = js.Array()
  var possibleValues : js.Array[String] = js.Array()

  var selectedAbility : String = ""
  var inputTitle : String = ""

  def add() : Unit  = ???

  def remove(index : Int) : Unit = ???

}
