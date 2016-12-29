package nephtys.loom.frontend

import angulate2.std._
import nephtys.loom.protocol.vanilla.solar.Solar

/**
  * Created by nephtys on 12/12/16.
  */
@Component(
  selector = "points-left-vanilla",
  template =
    """ <div>
      |this is the points left component
      |</div>
    """.stripMargin,
  styles = @@@(
    """
      |.form-inline > * {
      |   margin:15px 15px;
      |}
    """.stripMargin)

)
class PointsLeftComponent {

  @Input
  var character : Solar = _

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
  7) 4 specialties

  8) 10 merits
  9) 15 charms
  10) >= 4 Intimacies (how to model this?)
  11) 15 Bonus Points


   */

  //so we need a pointer function based on the aggregate ("add 3 more Intimacies to flash out your character")
  //selectable ability types should be calculated anew everytime the set types OR the set caste changes

}
