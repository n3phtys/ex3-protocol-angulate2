package nephtys.loom.frontend


import angulate2.std._
import nephtys.loom.protocol.vanilla.solar.Attributes.AttributeRating

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js

/**
  * Created by nephtys on 12/11/16.
  */
@Component(
  selector = "solar-attributes",
  template = "<h3>solar-attributes are here</h3>" +
             """
               | <div class="container">
               | <div class="container-column">
               | <div class="column-header">Physical</div>
               |<dot-control color="red" [value]="str" [min]="minvalue" name="Strength" class="container-cell"></dot-control>
               | <dot-control color="red" [value]="dex" [min]="minvalue" name="Dexterity" class="container-cell"></dot-control>
               | <dot-control color="red" [value]="sta" [min]="minvalue" name="Stamina" class="container-cell"></dot-control>
               |
               |
               | </div>
               | <div class="container-column">
               | <div class="column-header">Social</div>
               |
               | <dot-control color="orange" [value]="cha" [min]="minvalue" name="Charisma" class="container-cell"></dot-control>
               | <dot-control color="orange" [value]="man" [min]="minvalue" name="Manipulation" class="container-cell"></dot-control>
               | <dot-control color="orange" [value]="app" [min]="minvalue" name="Appearance" class="container-cell"></dot-control>
               | </div>
               | <div class="container-column">
               | <div class="column-header">Mental</div>
               | <dot-control color="cyan" [value]="per" [min]="minvalue" name="Perception" class="container-cell"></dot-control>
               | <dot-control color="cyan" [value]="int" [min]="minvalue" name="Intelligence" class="container-cell"></dot-control>
               | <dot-control color="cyan" [value]="wit" [min]="minvalue" name="Wits" class="container-cell"></dot-control>
               |
               |
               | </div>
               | </div>
             """.stripMargin,

  styles = @@@(    """
                     |.column-header {
                     |font-weight: bold;
                     |font-size: 150%;
                     |text-align: center;
                     |}
                     | .container {
                     |  display: flex;
                     |
                     |  flex-wrap : wrap;
                     | }
                     | .container-column {
                     |  display: flex;
                     |  flex-direction: column;
                     |  height: 300px; /* Or whatever */
                     |
                     |
                     |
                     | }
                     | .container-cell {
                     |  /* width: 100px;   Or whatever */
                     |  /* height: 100px;  Or whatever */
                     |  /*min-width: 150px;*/
                     |  margin: auto;  /* Magic! */
                     |  /*width:100px*/
                     |  padding:15px;
                     |
                     | }
                   """.stripMargin)

)
class AttributeComponent {

  val minvalue = 0

  var str = 2
  var dex = 3
  var sta = 2
  var cha = 5
  var man = 1
  var app = 3
  var per = 3
  var wit = 2
  var int = 4

  def triggerChanged(newRatings : IndexedSeq[AttributeRating]) : Unit = ???
}
