package nephtys.loom.frontend


import angulate2.core.OnChanges.SimpleChanges
import angulate2.core.{EventEmitter}
import angulate2.std._
import nephtys.loom.protocol.vanilla.solar.Attributes
import nephtys.loom.protocol.vanilla.solar.Attributes.{AttributeBlock, AttributeRating}
import nephtys.loom.protocol.vanilla.solar.Misc.Dots

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js

/**
  * Created by nephtys on 12/11/16.
  */
@Component(
  selector = "solar-attributes",
  template = "<h3>Attributes</h3>" +
             """
               | <div class="container">
               | <div class="container-column">
               | <div class="column-header">Physical</div>
               |<dot-control color="red" [value]="attributeMap[str]" [min]="minvalue" (valueSelected)="scoreChange(str, $event)" name="Strength" class="container-cell"></dot-control>
               | <dot-control color="red" [value]="attributeMap[dex]" (valueSelected)="scoreChange(dex, $event)" [min]="minvalue" name="Dexterity" class="container-cell"></dot-control>
               | <dot-control color="red" [value]="attributeMap[sta]" (valueSelected)="scoreChange(sta, $event)" [min]="minvalue" name="Stamina" class="container-cell"></dot-control>
               |
               |
               | </div>
               | <div class="container-column">
               | <div class="column-header">Social</div>
               |
               | <dot-control color="orange" [value]="attributeMap[cha]" (valueSelected)="scoreChange(cha, $event)" [min]="minvalue" name="Charisma" class="container-cell"></dot-control>
               | <dot-control color="orange" [value]="attributeMap[man]" (valueSelected)="scoreChange(man, $event)" [min]="minvalue" name="Manipulation" class="container-cell"></dot-control>
               | <dot-control color="orange" [value]="attributeMap[app]" (valueSelected)="scoreChange(app, $event)" [min]="minvalue" name="Appearance" class="container-cell"></dot-control>
               | </div>
               | <div class="container-column">
               | <div class="column-header">Mental</div>
               | <dot-control color="cyan" [value]="attributeMap[per]" (valueSelected)="scoreChange(per, $event)" [min]="minvalue" name="Perception" class="container-cell"></dot-control>
               | <dot-control color="cyan" [value]="attributeMap[int]" (valueSelected)="scoreChange(int, $event)" [min]="minvalue" name="Intelligence" class="container-cell"></dot-control>
               | <dot-control color="cyan" [value]="attributeMap[wit]" (valueSelected)="scoreChange(wit, $event)" [min]="minvalue" name="Wits" class="container-cell"></dot-control>
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
class AttributeComponent extends OnChanges {

  @Input
  var attributes : AttributeBlock = Attributes.emptyAttributeBlock

  var attributeMap : js.Array[Int] = js.Array()

  @Output
  val change = new EventEmitter[AttributeBlock]()

  @Input
  var minvalue = 1

  def scoreChange(attribute : Int, newValue : Int) : Unit = {
    println(s"Changing in Attribute index $attribute to value $newValue")
    attributeMap(attribute) = newValue
    val t = attributes.block.zipWithIndex.map(a => if (a._2 == attribute) {
      AttributeRating(a._1.attribute, Dots(newValue.toByte))
    } else {
      a._1
    })
    change.emit(AttributeBlock(t, attributes.ordering))
  }

  val str = 0
  val dex = 1
  val sta = 2
  val cha = 3
  val man = 4
  val app = 5
  val per = 6
  val wit = 7
  val int = 8

  def inputChanged() : Unit = {
    attributeMap.clear()
    attributes.block.foreach(c => attributeMap.push(c.dots.number.toInt))
  }

  inputChanged()

  override def ngOnChanges(changes: SimpleChanges): Unit = inputChanged()
}
