package nephtys.loom.frontend

import angulate2.core.OnChanges.SimpleChanges
import angulate2.core.{EventEmitter}
import nephtys.loom.protocol.vanilla.solar.Abilities.SpecialtyAble
import angulate2.std._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js
import scala.scalajs.js

/**
  * Created by nephtys on 12/11/16.
  */
@Component(
  selector = "string-pair-list",
  template =
    """<h5>{{title}}</h5>
      |<div class="form-inline">
      |<div class="form-group form-inline">
      |<label for="textinput">New
      |<input class="form-control" type="text" id="textinput" [(ngModel)]="writtenValue">
      |
      |<select [(ngModel)]="selectedValue" class="form-control" >
      |  <option *ngFor="let c of selectableValues" [ngValue]="c">{{c}}</option>
      |</select>
      |<button type="submit" class="btn btn-success" (click)="add()" [disabled]="! writtenValue"
      |      ><span class="glyphicon glyphicon-plus"></span></button>
      |      </label>
      |</div>
      | </div>
      |<ul *ngFor="let entry of listOfEntries; let i = index">
      | <li>{{entry.selected}}: {{entry.written}} <button type="button" *ngIf="canRemove" (click)="remove(i)" class="btn btn-danger"
      |      ><span class="glyphicon glyphicon-remove-sign"></span></button></li>
      |</ul>
    """.stripMargin
)
class StringPairComponent extends OnChanges {

  //useable for specialties and for intimacies (as they are just a combobox AND a text field)

  @Input
  var title : String = "This is StringPair title"


  @Input
  var canRemove : Boolean = true

  @Input
  var selectable : Seq[String] = Seq("Cat A", "Cat B", "Cat C")

  var selectableValues : js.Array[String] = js.Array()


  @Input
  var input : Seq[StringPair] = Seq(
    StringPair("Cat A", "Text A"),
    StringPair("Cat B", "Text B")
  )

  var listOfEntries : js.Array[StringPair] = js.Array()

  @Output
  var seqChange = new EventEmitter[Seq[StringPair]]()


  var selectedValue : String = "Cat A"
  var writtenValue : String = ""

  def inputChanged() : Unit = {
    selectableValues = selectable.toJSArray
    listOfEntries = input.toJSArray
    selectedValue = selectable.headOption.getOrElse("")
    writtenValue = ""
  }

  inputChanged()

  //todo: input verifier function to disable plus button




  def add() : Unit  = {
    println(s"writtenValue = $writtenValue")
    println(s"selectedValue = $selectedValue")

    if (writtenValue.nonEmpty ) {
      listOfEntries.push(StringPair(selectedValue, writtenValue))
      println(s"added element $writtenValue")
      writtenValue = ""
      seqChange.emit(listOfEntries.toSeq)
    }
  }

  def remove(index : Int) : Unit = {
    if (org.scalajs.dom.window.confirm("Do you really want to remove this item?")) {
      val r = listOfEntries.splice(index, 1)
      println(s"removed element $r")
      seqChange.emit(listOfEntries.toSeq)
    }
  }


  override def ngOnChanges(changes: SimpleChanges): Unit = inputChanged()
}
