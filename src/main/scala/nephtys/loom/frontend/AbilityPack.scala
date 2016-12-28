package nephtys.loom.frontend

import nephtys.loom.protocol.vanilla.solar.Abilities
import nephtys.loom.protocol.vanilla.solar.Abilities._
import nephtys.loom.protocol.vanilla.solar.Misc.Dots

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportAll}
/**
  * Created by nephtys on 12/13/16.
  */
@JSExport
@JSExportAll
class AbilityPack(var title : String, var typ : String, var abilities : js.Array[String], var ratings : js.Array[Int], var addable : Boolean) {

  var unique : Boolean = abilities.size == 1 && !addable

  def asAbilityLike : AbilityLike with Typeable = if (addable) { //ability family
    AbilityFamily(instances = abilities.toSeq.map(s => Ability(s)).toSet, familityName = title)
  } else if (abilities.length == 2) { //duo ability
    DuoAbilityGroup(Ability(abilities(0)), Ability(abilities(1)), title)
  } else { //singleability
    SingleAbility(Ability(abilities(0)))
  }

  def asRatings : Map[Ability, Dots] = (0 until abilities.length).map(i => (Ability(abilities(i)), Dots(ratings(i).toByte))).toMap

  def asType : (Typeable, Abilities.Type) = (asAbilityLike.asInstanceOf[Typeable], Abilities.strToTypes.getOrElse(typ, Abilities.Normal))

}
