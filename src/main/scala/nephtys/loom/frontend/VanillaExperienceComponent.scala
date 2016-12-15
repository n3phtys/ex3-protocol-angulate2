package nephtys.loom.frontend

import angulate2.std._
import nephtys.loom.protocol.vanilla.solar.Experiences
import nephtys.loom.protocol.vanilla.solar.Experiences._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js
import scala.util.Try

/**
  * Created by nephtys on 12/12/16.
  */
@Component(
  selector = "experience-component",
  template =
    """ <div>
      |<h3>Experience Management</h3>
      |
      |<div class="panel panel-primary">
      |<div class="panel-heading">Available experience</div>
      |<div class="panel-body">
      |<ul>
      |<li *ngFor="let va of experienceCategories; let i = index">
      |{{types[i]}}: Current: {{va.current}} / Total: {{va.total}} (already spent: {{va.spent}})
      |
      |</li>
      |</ul>
      |You are therefore <label>Essence {{essence}}</label>
      |
      |</div>
      |</div>
      |
      |
      |<div class="panel panel-primary">
      |<div class="panel-heading">Manual changes to experience</div>
      |<div class="panel-body">
      |<div>
      |
      |<form class="form-inline">
      |  <div class="form-group">
      |    <select [(ngModel)]="selectedType" name="xptype" class="form-control" >
      |  <option *ngFor="let c of possibleTypes" [ngValue]="c">{{c}}</option>
      |</select>
      |  </div>
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
    """.stripMargin,
  styles = @@@(
    """
      |.form-inline > * {
      |   margin:15px 15px;
      |}
    """.stripMargin)

)
class VanillaExperienceComponent {
  //TODO: add manual gains and manual spendings via buttons
  //TODO: show manual entries as collapsed list / log (LIFO)

  @Input
  var experience : ExperienceBox = Experiences.mockBox

  var types : js.Array[ExperienceType] = js.Array()
  var possibleTypes : js.Array[ExperienceType] = Experiences.types.toJSArray
  var selectedType : ExperienceType = GeneralXP
  var writtenNote : String = ""
  var writtenAmount : Int = 1
  var experienceCategories : js.Array[ExperienceCategory] = js.Array()
  var history : js.Array[ManualEntry] = js.Array()

  var essence : Int = 0

  def addButtonPressed() : Unit = {

      var nr = writtenAmount
      val t: ExperienceType = selectedType
      val c: String = writtenNote

      experience = experience.addManualEntry(nr, t, c, System.currentTimeMillis())
      inputChanged()

      manualChangeEvent(experience)


    println("Add Button pressed with values")
  }

  def inputChanged() : Unit = {
    types = js.Array()
    selectedType = possibleTypes.head
    writtenNote = ""
    writtenAmount = 1
    experienceCategories = js.Array()
    history = js.Array()
    if( experience != null) {
      essence = experience.essenceRating

      experience.categories.foreach(f => {
        types.push(f._1)
        experienceCategories.push(f._2)
      })
      experience.manualEntries.foreach(f => {
        history.push(f)
      })
    }
  }

  inputChanged()


  def manualChangeEvent(eb : ExperienceBox) : Unit = {
    //TODO: trigger eventemitter
  }
}
