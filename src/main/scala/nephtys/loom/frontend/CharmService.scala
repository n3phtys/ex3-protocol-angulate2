package nephtys.loom.frontend

import java.util.UUID

import angulate2.std.Injectable
import nephtys.loom.protocol.shared.Charms.Evocation
import nephtys.loom.protocol.shared.Powers.SolarCharms
import nephtys.loom.protocol.shared.{SolarCharm, _}
import nephtys.loom.protocol.vanilla.solar.Abilities.Supernal
import nephtys.loom.protocol.vanilla.solar._
import org.nephtys.loom.generic.protocol.InternalStructures.{Email, ID}
import rxscalajs.subjects.BehaviorSubject

import scala.concurrent.{Future, duration}
import scala.scalajs.js
import scala.scalajs.js
import scala.scalajs.js.Array
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.Array
import scala.scalajs.js.JSConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.Failure

/**
  * Created by Christopher on 12.01.2017.
  */
@Injectable
class CharmService {

  println("CharmService initialized")


  private val behaviorSubject = BehaviorSubject[CharmLearnable]( Characters.emptySolar(ID[Solar](UUID.randomUUID()), Email("something@email.org")) )
  private val debouncedTrigger = behaviorSubject.debounceTime(FiniteDuration(3, duration.SECONDS))
  private val subscription = debouncedTrigger.subscribe(unit => uncachedRecalc(unit).foreach(s => println("Charms recalculated for Solar")))

  def recalculateForCharacter(character : CharmLearnable) : Unit = {
    println("recalculating charms...")
    behaviorSubject.next(character)
  }

  def quickBuyListedPowerAndRecalculateSoon(character : CharmLearnable, power : Power with Product with Serializable) : Unit = {
    power match {
        //remove from purchaseable list and add to purchased list
      case s : Spell with Product with Serializable =>  {
        val indexOld : Int = purchaseableSpells.indexOf(s)
        val indexNew : Int = nephtys.loom.protocol.Search.binaryIndexOrElse(purchasedSpells, (a : Spell with Product with Serializable) => Powers.powersIndexMap.getOrElse(a, 0), s, purchasedSpells.size)
        purchaseableSpells.splice(indexOld, 1)
        purchasedSpells.splice(indexNew, 0, s)
      }
      case s : SolarCharm with Product with Serializable =>  {
        val indexOld : Int = purchaseableSolarCharms.indexOf(s)
        val indexNew : Int = nephtys.loom.protocol.Search.binaryIndexOrElse(purchasedSolarCharms, (a : SolarCharm with Product with Serializable) => Powers.powersIndexMap.getOrElse(a, 0), s, purchasedSolarCharms.size)
        purchaseableSolarCharms.splice(indexOld, 1)
        purchasedSolarCharms.splice(indexNew, 0, s)
      }
      case s : Charm with Product with Serializable =>  {
        val indexOld : Int = purchaseableOtherCharms.indexOf(s)
        val indexNew : Int = nephtys.loom.protocol.Search.binaryIndexOrElse(purchasedOtherCharms, (a : Charm with Product with Serializable) => Powers.powersIndexMap.getOrElse(a, 0), s, purchasedOtherCharms.size)
        purchaseableOtherCharms.splice(indexOld, 1)
        purchasedOtherCharms.splice(indexNew, 0, s)
      }
    }
    //order recalculation for later
    recalculateForCharacter(character)
  }

  //val Unknown : Int = 0
  val Owned : Option[Boolean] = None
  val Purchaseable : Option[Boolean] = Some(true)
  val Blocked : Option[Boolean] = Some(false)

  val powerStates : js.Array[Future[Option[Boolean]]] = Powers.powers.indices.map(i => -1).map(i => Future.successful(Some(false))).asInstanceOf[IndexedSeq[Future[Option[Boolean]]]].toJSArray
  fillArrays()

  def uncachedRecalc(solar : CharmLearnable) : Future[Unit] = {

    println("Beginning uncachedRecalc")

    //begin calculation for each single power in a unique future:
    powerStates.indices.foreach(i => powerStates(i) = calculateSingleCharm(solar, i))
    val f : Future[_] = Future.sequence(powerStates.toSeq)

    f.onComplete(t => if (t.isFailure) println(s"Error at collector! => ${t.asInstanceOf[Failure[_]].exception.getMessage}"))
    //after finishing all partial calculation, split up powers into 9 categories
    //finally: return future
    f.map(t => {
      println("Filling arrays in uncachedRecalc")
      fillArrays()
      ()
    })
  }

  private def getCharmType(power : Power ) : CharmType = power match {
    case s : SolarCharm => SolarCharmType
    case e : nephtys.loom.protocol.shared.EvocationCharm => EvocationType
    case m : MartialArtsCharm  => MartialArtsType
    case e : EclipseCharm => EclipseCharmType
    case t : TerrestrialCircleSpell => TerestrialSpellType
    case c : CelestialCircleSpell => CelestialSpellType
    case s : SolarCircleSpell => SolarCircleSpellType
  }

  private def calculateSingleCharm(solar : CharmLearnable, charmIndex : Int) : Future[Option[Boolean]] = {
    if (solar.has(charmIndex)) {
      Future.successful(None)
    } else {
      val f = Future {

        println(s"Fine calculating charm $charmIndex")

        val power = Powers.powers(charmIndex)
        val ability : Option[String] = power match {
          case s : SolarCharm => Some(s.abilityString)
          case _ => None
        }
        val abilityRatingOfCharacter : Option[Int] = ability.flatMap(s => solar.abilityRating(s))
        val isSupernal : Boolean = power match {
          case s : SolarCharm => solar.abilityType(ability.getOrElse("")).contains(Supernal)
          case m : MartialArtsCharm => solar.abilityType(Abilities.preprogrammed.BrawlMartialArtsComboLabel).contains(Supernal)
          case _ => false
        }
        val checkPowerType : Boolean = solar.canLearn(getCharmType(power))
        val checkAbilityRating : Boolean = power match {
          case s : SolarCharm => s.abilityRequirement.dots <=  abilityRatingOfCharacter.getOrElse(0)
          case m : MartialArtsCharm => m.martialArtsDotsRequired <= solar.abilityRating(Abilities.preprogrammed.MartialArts).getOrElse(0)
          case _ => true
        }

        if((isSupernal || power.essenceInt <= solar.essence.rating)
          && checkPowerType && checkAbilityRating
        ) {
          println("Early yes")
          (true, power)
        } else {
          println(s"Early no at $power because of missing essence dots = ${power.essenceInt - solar.essence.rating} & supernal = $isSupernal & checkPowerType = $checkPowerType & checkAbilityRating = $checkAbilityRating")
          (false, power)
        }
      }
      def checkPredef(set : Set[Int]) : Future[Option[Boolean]] = {
        println("checkPredef called")
        Future.sequence(set.map(i => powerStates(i))).map(seq => Some(seq.forall(_.contains(true))))
      }

      f.flatMap(a => if(a._1) checkPredef(a._2.prerequisite.map(p => Powers.powersIndexMap.getOrElse(p,0))) else Future.successful(Some(false)))
    }
  }



  //TODO: split into three categories


  private def clearArrays() : Unit = {
    purchaseableSolarCharms = js.Array()
    purchaseableSpells = js.Array()
    purchaseableOtherCharms = js.Array()
    purchasedSolarCharms = js.Array()
    purchasedSpells = js.Array()
    purchasedOtherCharms = js.Array()
    unpurchaseableSolarCharms = js.Array()
    unpurchaseableSpells = js.Array()
    unpurchaseableOtherCharms = js.Array()
  }

  private def fillArrays() : Unit = {
    clearArrays()
    this.powerStates.zipWithIndex.foreach(f => {
      val power = Powers.powers(f._2)
      println(s"Filling arrays with $power")
      power match {
        case s : Spell with Product with Serializable => f._1.foreach {
          case None => purchasedSpells.push(s)
          case Some(true) => purchaseableSpells.push(s)
          case Some(false) => unpurchaseableSpells.push(s)
        }
        case s : SolarCharm with Product with Serializable => f._1.foreach {
          case None => purchasedSolarCharms.push(s)
          case Some(true) => purchaseableSolarCharms.push(s)
          case Some(false) => unpurchaseableSolarCharms.push(s)
        }
        case s : Charm with Product with Serializable => f._1.foreach {
          case None => purchasedOtherCharms.push(s)
          case Some(true) => purchaseableOtherCharms.push(s)
          case Some(false) => unpurchaseableOtherCharms.push(s)
        }
      }
    })
  }

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
