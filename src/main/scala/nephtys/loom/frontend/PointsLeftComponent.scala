package nephtys.loom.frontend

import angulate2.core.OnChanges.SimpleChanges
import angulate2.core.OnChangesJS
import angulate2.std._
import nephtys.loom.protocol.vanilla.solar.Solar

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
      |.form-inline > * {
      |   margin:15px 15px;
      |}
    """.stripMargin)

)
class PointsLeftComponent extends OnChangesJS{


  @Input
  var title : String = "You may spend:"

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
      val intimacies : Seq[String] = Seq.empty
      val bonuspoints : Seq[String] = Seq.empty
      val specialties : Seq[String] = Seq.empty

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

      val abilityTypes: Seq[String] = Seq.empty
      val abilities: Seq[String] = Seq.empty

      val merits : Seq[String] = Seq.empty

      val charms : Seq[String] = Seq.empty

      attributes ++ abilityTypes ++ abilities ++ merits ++ charms

    } else {
      val experiences : Seq[String] = Seq.empty

      experiences
    }
  }

  //TODO: implement whole
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
