package nephtys.loom.frontend

import java.util.UUID

import angulate2.core.EventEmitter
import angulate2.core.OnChanges.SimpleChanges
import angulate2.std._
import nephtys.dualframe.cqrs.client.StringListDif.{StringListAdd, StringListDelete, StringListDif, StringListEdit}
import nephtys.loom.protocol.shared.CharmRef
import nephtys.loom.protocol.vanilla.solar.{Characters, Solar}
import org.nephtys.loom.generic.protocol.InternalStructures.{Email, ID}

import scala.scalajs.js
import scala.scalajs.js.Array
import scala.scalajs.js.JSConverters._

/**
  * Created by Christopher on 05.01.2017.
  */
@Component(
  selector = "charm-component-vanilla",
  template =
    """ <div>
      | <tab-pane >
      |  <tab-control title="Purchase Listed Charm">
      |
      |
      |   <div class="form-group">
      |  <label for="sel1">Select Type:</label>
      |  <select  [(ngModel)]="selectedPowerType" class="form-control" id="sel1">
      |    <option *ngFor="let c of selectablePowerTypes" [ngValue]="c">{{c}}</option>
      |  </select>
      |</div>
      |
      |   <div>
      |   You have following Charms/Evocations:
      |<br>
      |
      |   You have following Spells:
      |<br>
      |
      |   Purchaseable Charms:
      |<br>
      |
      |   Purchaseable Spells:
      |<br>
      |
      |
      |   Charms not purchaseable (not qualified for):
      |
      |
      |
      |   </div>
      |  </tab-control>
      |  <tab-control title="Custom Charm">
      |
      |
      |   <div class="form-group">
      |  <label for="sel1">Select Type:</label>
      |  <select  [(ngModel)]="selectedPowerType" class="form-control" id="sel1">
      |    <option *ngFor="let c of selectablePowerTypes" [ngValue]="c">{{c}}</option>
      |  </select>
      |</div>
      |
      |
      |<div class="form-group">
      |  <label for="usr">Name:</label>
      |  <input type="text" class="form-control" id="name" [(value)]="customName">
      |  <label for="usr">Description:</label>
      |  <textarea class="form-control" id="description" [(value)]="customDescription"></textarea>
      |</div>
      |
      |
      |   <div class="form-group" *ngIf="selectedPowerType !== selectablePowerTypes[1]">
      |  <label for="sel1">Essence Rating:</label>
      |  <select class="form-control" [(ngModel)]="customEssenceRating" id="sel1">
      |    <option>1</option>
      |    <option>2</option>
      |    <option>3</option>
      |    <option>4</option>
      |    <option>5</option>
      |  </select>
      |</div>
      |
      |   <div class="form-group" *ngIf="selectedPowerType === selectablePowerTypes[1]">
      |  <label for="sel1">Circle:</label>
      |  <select class="form-control" [(ngModel)]="customCircle" id="sel1">
      |    <option>Terrestrial</option>
      |    <option>Celestial</option>
      |    <option>Solar</option>
      |  </select>
      |</div>
      |
      |
      |
      |<div class="form-inline" *ngIf="selectedPowerType === selectablePowerTypes[0]">
      |<div class="form-group">
      |  <label for="abilitysel1">Ability:</label>
      |  <select class="form-control" [(ngModel)]="customAbilitySelector" id="abilitysel1">
      |    <option *ngFor="let a of selectableAbilities">{{a}}</option>
      |  </select>
      |</div>
      |<!-- div class="form-group">
      |  <label for="ratingsel1">Ability Rating:</label>
      |  <select class="form-control" id="ratingsel1">
      |    <option>1</option>
      |    <option>2</option>
      |    <option>3</option>
      |    <option>4</option>
      |    <option>5</option>
      |  </select>
      |</div-->
      |</div>
      |
      |
      |<div *ngIf="selectedPowerType !== selectablePowerTypes[1]" class="form-group">
      |  <label for="sel1">Type:</label>
      |  <select class="form-control" [(ngModel)]="customTypeSelector" id="sel1">
      |    <option>Supplemental</option>
      |    <option>Simple</option>
      |    <option>Reflexive</option>
      |    <option>Permanent</option>
      |  </select>
      |</div>
      |
      |<div class="form-group">
      |  <label for="usr">Cost:</label>
      |  <input type="text" class="form-control" id="cost" [(value)]="customCost">
      |  <label  for="usr">Duration:</label>
      |  <input type="text" class="form-control" id="duration" [(value)]="customDuration">
      |</div>
      |
      |
      | <string-list title="Keywords" [input]="keywords" (seqChange)="keyWordChange($event)"></string-list>
      |
      |
      |
      |<div class="form-group">
      |  <label for="sel1">Purchase with:</label>
      |  <select class="form-control" [(ngModel)]="customCostType" id="sel1">
      |    <option *ngFor="let c of possibleCostTypes">{{c}}</option>
      |  </select>
      |  <label for="costselector">Number:</label>
      |  <input type="number" class="form-control" id="costselector" [(value)]="customCostAmount">
      |</div>
      |
      |
      |
      |<div>
      |   <button type="button" class="btn btn-primary" (click)="submitCustomPower()">Purchase Custom Charm</button>
      |  </div>
      |
      |  </tab-control>
      |</tab-pane>
      |
      |</div>
    """.stripMargin,
  styles = @@@(
    """
      |
    """.stripMargin)

)
class CharmComponent(val charmService: CharmService) extends OnChanges {

  val selectablePowerTypes : js.Array[String] = js.Array("Solar Charm", "Spell", "Other")
  val possibleCostTypes : js.Array[String] = js.Array("Experience", "Bonus Points", "Free CG Charms")
  var selectableAbilities : js.Array[String] = js.Array("Archery")



  var selectedPowerType : String = selectablePowerTypes(0)
  var customName : String = "Mindful Invention Prana"
  var customDescription : String = ""
  var customCost : String = "1m"
  var customDuration : String = "Instant"
  var customEssenceRating : Int = 1
  var customCircle : String = "Terrestrial"
  var customAbilitySelector : String = "Dodge"
  var customTypeSelector : String = "Reflexive"
  var customCostType : String = possibleCostTypes(0)
  var customCostAmount : Int = 1


  var keywords : Seq[Input] = Seq.empty


  //TODO: design concept for OK/Cancel / and to go back



  @Input
  var solar : Solar = Characters.emptySolar(ID[Solar](UUID.randomUUID()), Email("something@email.org"))

  @Output
  val purchasedListed = new EventEmitter[CharmRef]()

  @Output
  val createdCustom = new EventEmitter[(String, String, String)]()



  def keyWordChange(change : StringListDif) : Unit = change match {
    case StringListDelete(index) => ???
    case StringListAdd(value) => ???
    case StringListEdit(index, value) => ???
  }


  def submitCustomPower() : Unit = {
    ???
  }

  override def ngOnChanges(changes: SimpleChanges): Unit = {
    inputChanged()
  }

  private def inputChanged() : Unit = {
    if (solar != null) {
      selectableAbilities = solar.abilities.abilities.map(_.name).toSeq.sorted.toJSArray
    }
  }
  inputChanged()
}
