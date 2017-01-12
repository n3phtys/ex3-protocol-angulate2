package nephtys.loom.frontend

import angulate2.std.Injectable
import nephtys.loom.protocol.shared.CharmRef
import nephtys.loom.protocol.shared.Charms.{Charm, Power, Spell}
import nephtys.loom.protocol.vanilla.solar.{CharmLearnable, Solar}

import scala.concurrent.Future
import scala.scalajs.js

/**
  * Created by Christopher on 12.01.2017.
  */
@Injectable
class CharmService {

  println("CharmService initialized")

  def recalculateForCharacter(character : CharmLearnable) : Future[Unit] = ???

  def translate(charmRef : CharmRef) : Option[Power] = ???

  var purchaseableCharms : js.Array[Charm] = js.Array[Charm]()

  var purchaseableSpells : js.Array[Spell] = js.Array()

  var purchasedCharms : js.Array[Charm] = js.Array()

  var purchasedSpells : js.Array[Spell] = js.Array()

  var unpurchaseableCharms : js.Array[Charm] = js.Array()

  var unpurchaseableSpells : js.Array[Spell] = js.Array()

}
