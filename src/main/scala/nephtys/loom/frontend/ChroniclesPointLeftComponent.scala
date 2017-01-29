package nephtys.loom.frontend

import angulate2.core.OnChanges.SimpleChanges
import angulate2.std.{Component, Input, OnChanges}
import nephtys.loom.protocol.chronicles.solar.{CharacterFactory, Experiences, Solar}
import nephtys.loom.protocol.vanilla.solar.{Abilities, Intimacies}

/**
  * Created by Christopher on 29.01.2017.
  */
@Component(
  selector = "points-left-chronicles",
  template = """<div>
               |<remark-component
               | [title]="title"
               | [remarksLowPrio]="lowPrio"
               | [remarksHighPrio] = "hiPrio"
               |></remark-component>
               |</div>
               |""".stripMargin
)
class ChroniclesPointLeftComponent extends OnChanges{

  @Input
  var character : Solar = CharacterFactory.emptyChroniclesSolar

  @Input
  var title : String = "Open for spending:"


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
      val specialties : Seq[String] = {
        if (solar.abilities.specialties.map(_.length).sum < 4) {
          Seq(s"Specialties: ${solar.abilities.specialties.map(_.length).sum}/4")
        } else Seq.empty
      }

      val aspirations : Seq[String] = {
        if (solar.aspirations.length < 3) {
          Seq("You should take 3 Aspirations for your character, one long- and two short-term")
        } else {
          Seq.empty
        }
      }

      intimacies ++ specialties ++ aspirations
    } else {
      Seq.empty //is this even ever needed for anything? keep for later functional plugins
    }
  }

  private def generateHighPrio(solar : Solar) : Seq[String] = {
    val experiences : Seq[String] = {
      val k =  Experiences.Point.asBeats(solar.experience.beatsLeftForSpending)
      if (k > 0) {
        Seq(s"You have $k Experience Points left to spend")
      } else Seq.empty
    }


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
        val favored : Int = solar.abilities.types.count(Abilities.Favored == _)
        val caste : Int = solar.abilities.types.count(Abilities.Caste == _)
        val supernal : Boolean = solar.abilities.types.exists(Abilities.Supernal == _)
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

      attributes ++ abilityTypes ++ abilities ++ merits ++ charms ++ experiences

    } else {
      experiences
    }
  }

}
