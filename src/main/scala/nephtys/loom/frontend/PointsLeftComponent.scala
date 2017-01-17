package nephtys.loom.frontend

import angulate2.core.OnChanges.SimpleChanges
import angulate2.std._
import nephtys.loom.protocol.vanilla.solar.{Abilities, Intimacies, Solar}

import scala.collection.immutable.SortedSet

/**
  * Created by nephtys on 12/12/16.
  */
@Component(
  selector = "points-left-vanilla",
  template =
    """ <div>
      |<remark-component
      | [title]="title"
      | [remarksLowPrio]="lowPrio"
      | [remarksHighPrio] = "hiPrio"
      |></remark-component>
      |</div>
    """.stripMargin,
  styles = @@@(
    """
      |
    """.stripMargin)

)
class PointsLeftComponent extends OnChanges{


  @Input
  var title : String = "Open for spending:"

  @Input
  var character : Solar = _

  var lowPrio : Seq[String] = Seq.empty
  var hiPrio : Seq[String] = Seq.empty


  override def ngOnChanges(changes: SimpleChanges): Unit = {
    if(character != null) {
      lowPrio = generateLowPrio(character)
      hiPrio = generateHighPrio(character)
    }
  }

  private def generateLowPrio(solar : Solar) : Seq[String] = {
    if (solar.stillInCharGen) {
      val intimacies : Seq[String] = {
        val total = solar.intimacies.size
        val defining = solar.intimacies.count(s => s._2 == Intimacies.Defining)
        val major = solar.intimacies.count(s => s._2 == Intimacies.Major)
        if(total < 4 || defining < 1 || major < 1) {
          Seq(s"You should take at least ${4 - total} more Intimacies, and make sure to have at least one Defining and one Major")
        } else Seq.empty
      }
      val bonuspoints : Seq[String] = {
        if (solar.bonusPointsUnspent > 0) {
          Seq(s"Bonus Points spent: ${15 - solar.bonusPointsUnspent}/15")
        } else Seq.empty
      }
      val specialties : Seq[String] = {
        if (solar.abilities.specialties.values.flatten.size < 4) {
          Seq(s"Specialties: ${solar.abilities.specialties.values.flatten.size}/4")
        } else Seq.empty
      }

      intimacies ++ specialties ++ bonuspoints
    } else {
      Seq.empty //is this even ever needed for anything? keep for later functional plugins
    }
  }

  private def generateHighPrio(solar : Solar) : Seq[String] = {
    if (solar.stillInCharGen) {
      val attributes: Seq[String] = {
        val physicals: Int = solar.attributes.physicals.map(_.dots.number.toInt).sum - 3
        val socials: Int = solar.attributes.socials.map(_.dots.number.toInt).sum - 3
        val mentals: Int = solar.attributes.mentals.map(_.dots.number.toInt).sum - 3
        val vals = Seq(physicals, socials, mentals)
        val max = vals.max
        val min = vals.min
        val middle = vals.sorted.apply(1)
        if (vals.sorted.equals(Seq(4, 6, 8))) Seq.empty else {
          Seq(s"Primary Attributes: $max/8",
            s"Seconary Attributes: $middle/6",
            s"Terniary Attributes: $min/4"
          )
        }

      }

      val abilityTypes: Seq[String] = {
        val favored : Int = solar.abilities.types.values.count(Abilities.Favored == _)
        val caste : Int = solar.abilities.types.values.count(Abilities.Caste == _)
        val supernal : Boolean = solar.abilities.types.values.exists(Abilities.Supernal == _)
        (if (!supernal) {Seq("Select one Supernal Ability")} else Seq.empty) ++ (if (caste < 4) {Seq(s"Select ${4 - caste} more Caste Abilities")} else Seq.empty) ++ (if (favored < 5) {Seq(s"Select ${5 - favored} more Favored Abilities")} else Seq.empty)
      }
      val abilities: Seq[String] = {
        val sum = solar.abilities.spentWithFreePoints
        if (sum < 28) {
          Seq(s"Ability Dots: $sum/28")
        } else Seq.empty
      }

      val merits : Seq[String] = {
        val sum = solar.merits.map(_.rating.number.toInt).sum
        if (sum < 10) {
          Seq(s"Merits: $sum/10")
        } else Seq.empty
      }

      val charms : Seq[String] = {
        if ( solar.countCharmPurchases < 15) { //TODO: also count custom charms with freepoint cost
          Seq(s"Charms: ${ solar.countCharmPurchases}/15")
        } else Seq.empty
      }

      attributes ++ abilityTypes ++ abilities ++ merits ++ charms

    } else {
      val experiences : Seq[String] = {
        val k = solar.experience.formattedLeft
        if (k.nonEmpty) {
          k.map(a => s"You have ${a._2} ${a._1.toString} left to spend")
        } else Seq.empty
      }

      experiences
    }
  }

  //use remark control for this


  //this should get pinned somewhere on the side

  //it shows labels with all non-completely spent things current out of total

  //completely spent things disappear from this control

  //contains potential values for following:
  /*
   1) all kinds of experience (normally that's 2 or 3 with special xp)

  2) 8 dots for primary attribute, 6 dots for secondary attribute, 4 dots for ternary
  3) 4 caste abilities (should by default only be selectable for valid abilities)
  4) 5 favored abilities
  5) 1 supernal ability (again, only from valid abilities)
  6) 28 free ability points
  7) 4 specialties [low]

  8) 10 merits
  9) 15 charms
  10) >= 4 Intimacies (how to model this?) [low]
  11) 15 Bonus Points [low]


   */

  //so we need a pointer function based on the aggregate ("add 3 more Intimacies to flash out your character")
  //selectable ability types should be calculated anew everytime the set types OR the set caste changes

}
