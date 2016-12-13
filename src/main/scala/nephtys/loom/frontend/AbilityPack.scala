package nephtys.loom.frontend

import nephtys.loom.protocol.vanilla.solar.Abilities

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportAll}
/**
  * Created by nephtys on 12/13/16.
  */
@JSExport
@JSExportAll
class AbilityPack(var title : String, var typ : String, var abilities : js.Array[String], var ratings : js.Array[Int], var addable : Boolean) {

  var unique : Boolean = abilities.size == 1 && !addable


}
