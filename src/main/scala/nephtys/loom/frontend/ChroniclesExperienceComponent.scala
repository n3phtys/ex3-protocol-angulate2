package nephtys.loom.frontend

import angulate2.core.{EventEmitter, Input, Output}
import angulate2.core.OnChanges.SimpleChanges
import angulate2.std.{@@@, Component, Injectable, OnChanges}
import nephtys.loom.protocol.chronicles.solar.Experiences
import nephtys.loom.protocol.chronicles.solar.Experiences.{ExperienceBeatBox, ManualEntry}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportAll}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

/**
  * Created by Christopher on 25.01.2017.
  */
@Component(
  selector = "chronicles-experience",
  template =
    """
      |
      |
      |<div>
      |
      |<div class="panel panel-primary">
      |<div class="panel-heading">Available experience</div>
      |<div class="panel-body">
      |<label>
      |Current: {{input.pointsLeft}} XP and {{input.beatsLeft}} Beats / Total: {{input.pointsTotal}} XP and {{input.beatsTotal}} Beats (already spent: {{input.pointsSpent}} XP)
      |
      |</label>
      |<p>
      |You are therefore <label>Essence {{essence}}</label>
      |</p>
      |<p>
      | <button (click)="endCGclicked()"  class="btn btn-success" *ngIf="! charGenFinishedState">Press this Button to permanently leave Character Generation. You cannot undo this step!</button>
      |</p>
      |</div>
      |</div>
      |
      |
      |<div class="panel panel-primary" *ngIf="charGenFinishedState">
      |<div class="panel-heading">Manual changes to experience</div>
      |<div class="panel-body">
      |<div>
      |
      |<form class="form-inline">
      |  <div class="form-group">
      |    <label for="email">Points (+/-)</label>
      |    <input type="number" [(ngModel)]="writtenAmount" name="amount" class="form-control" id="email">
      |  </div>
      |  <div class="form-group">
      |    <label for="pwd">Comment (optional)</label>
      |    <input type="text" [(ngModel)]="writtenNote" name="comment" class="form-control" id="pwd">
      |  </div>
      |  <button type="button" class="btn btn-success" (click)="addButtonPressed()">Add to character</button>
      |</form>
      |
      |
      |</div>
      |<div>
      |
      |<div class="panel-group">
      |  <div class="panel panel-default">
      |    <div class="panel-heading">
      |      <h4 class="panel-title">
      |        <a data-toggle="collapse" href="#collapse1">History (click to show)</a>
      |      </h4>
      |    </div>
      |    <div id="collapse1" class="panel-collapse collapse">
      |      <div class="panel-body">
      |
      |<ul>
      |<li *ngFor="let entry of history; let i = index" >
      | {{entry.stringText}}
      | </li>
      |</ul>
      |
      |      </div>
      |    </div>
      |  </div>
      |</div>
      |
      |</div>
      |</div>
      |</div>
      |
      |
      |</div>
      |
      |
    """.stripMargin,
  styles = @@@(
    """
      |.form-inline > * {
      |   margin:15px 15px;
      |}
      |.btn{
      |white-space:normal !important;
      |    /*max-width:200px;*/
      |}
    """.stripMargin)
)
class ChroniclesExperienceComponent extends OnChanges{


  var history : js.Array[ManualEntry] = js.Array()
  var essence : Int = -1

  var writtenNote : String = ""
  var writtenAmount : Int = 1

  @Input
  var input : ExperienceBeatBox = Experiences.emptyBeatBoxWithFree()

  @Input
  var charGenFinishedState : Boolean = false

  //show Beats and Experiences as label
  //show essence label
  //button to leave character generation (after confirm)
  //list of manual entries
  //possibility to add manual entries: amount of beats and text field for comment, plus button

  override def ngOnChanges(changes: SimpleChanges): Unit = {
    history = input.manualEntries.toJSArray
    essence = input.essenceLevel
  }


  def endCGclicked() : Unit = {
    if (org.scalajs.dom.window.confirm("Do you really want to permanently leave Character Generation? You cannot return to it.")) {
      charGenFinishedState = true
      charGenFinished.emit(charGenFinishedState)
    }
  }

  def addButtonPressed() : Unit = {

    var nr = writtenAmount
    val c: String = writtenNote

    println(s"AddButtonPressed, nr = $nr and c = %c")

    val tr = input.modifyBeatsManually(nr, System.currentTimeMillis(), c)
    println(s"tr = $tr")

    tr.foreach( b => {
      experienceChange.emit(ManualEntry(nr, c, System.currentTimeMillis()))
    })
    println("Add Button pressed with values")

  }


  @Output
  val experienceChange = new EventEmitter[ManualEntry]()

  @Output
  val charGenFinished = new EventEmitter[Boolean]()
}