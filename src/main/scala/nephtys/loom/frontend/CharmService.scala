package nephtys.loom.frontend

import angulate2.std.Injectable
import nephtys.loom.protocol.shared._
import nephtys.loom.protocol.vanilla.solar.{CharmLearnable, Solar}

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js
import scala.scalajs.js.Array
import scala.scalajs.js.JSConverters._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Christopher on 12.01.2017.
  */
@Injectable
class CharmService {

  println("CharmService initialized")

  def recalculateForCharacter(character : CharmLearnable) : Future[Unit] = {

    purchaseableSolarCharms = Powers.SolarCharms.solarCharms.toJSArray
    purchaseableSpells = Powers.Spells.spells.toJSArray
    purchaseableOtherCharms = (Powers.EclipseCharms.eclipseCharms ++ Powers.MartialArtsCharms.martialArtsCharms ++ Powers.Evocations.evocations).toJSArray

    Future {}
  }

  //TODO: split into three categories



  var purchaseableSolarCharms : js.Array[SolarCharm with Product with Serializable] = js.Array()

  var purchaseableSpells : js.Array[Spell with Product with Serializable] = js.Array()

  var purchaseableOtherCharms : js.Array[Charm with Product with Serializable] = js.Array()




  var purchasedSolarCharms : js.Array[SolarCharm with Product with Serializable] = js.Array()

  var purchasedSpells : js.Array[Spell with Product with Serializable] = js.Array()

  var purchasedOtherCharms : js.Array[Charm with Product with Serializable] = js.Array()



  var unpurchaseableSolarCharms : js.Array[SolarCharm with Product with Serializable] = js.Array()

  var unpurchaseableSpells : js.Array[Spell with Product with Serializable] = js.Array()

  var unpurchaseableOtherCharms : js.Array[Charm with Product with Serializable] = js.Array()

}
