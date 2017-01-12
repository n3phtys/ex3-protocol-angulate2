package nephtys.loom.frontend

import angulate2.core.EventEmitter
import angulate2.core.OnChanges.SimpleChanges
import angulate2.std._
import nephtys.loom.protocol.shared.CharmRef

import scala.scalajs.js

/**
  * Created by Christopher on 05.01.2017.
  */
@Component(
  selector = "charm-component-vanilla",
  template =
    """ <div>
      | <tab-pane >
      |  <tab-control title="Purchase Listed Charm">
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
      |  <input type="text" class="form-control" id="name">
      |  <label for="usr">Duration:</label>
      |  <textarea class="form-control" id="description"></textarea>
      |</div>
      |
      |
      |   <div class="form-group">
      |  <label for="sel1">Essence Rating:</label>
      |  <select class="form-control" id="sel1">
      |    <option>1</option>
      |    <option>2</option>
      |    <option>3</option>
      |    <option>4</option>
      |    <option>5</option>
      |  </select>
      |</div>
      |
      |   <div class="form-group">
      |  <label for="sel1">Circle:</label>
      |  <select class="form-control" id="sel1">
      |    <option>Terrestrial</option>
      |    <option>Celestial</option>
      |    <option>Solar</option>
      |  </select>
      |</div>
      |
      |
      |   keyword selector,
      |
      |<div class="form-inline" *ngIf="selectedPowerType === selectablePowerTypes[0]">
      |<div class="form-group">
      |  <label for="abilitysel1">Ability:</label>
      |  <select class="form-control" id="abilitysel1">
      |    <option>Archery</option>
      |    <option>Occult</option>
      |    <option>War</option>
      |  </select>
      |</div>
      |<div class="form-group">
      |  <label for="ratingsel1">Ability Rating:</label>
      |  <select class="form-control" id="ratingsel1">
      |    <option>1</option>
      |    <option>2</option>
      |    <option>3</option>
      |    <option>4</option>
      |    <option>5</option>
      |  </select>
      |</div>
      |</div>
      |
      |
      |<div class="form-group">
      |  <label for="sel1">Type:</label>
      |  <select class="form-control" id="sel1">
      |    <option>Supplemental</option>
      |    <option>Simple</option>
      |    <option>Reflexive</option>
      |    <option>Permanent</option>
      |  </select>
      |</div>
      |
      |<div class="form-group">
      |  <label for="usr">Cost:</label>
      |  <input type="text" class="form-control" id="cost">
      |  <label for="usr">Duration:</label>
      |  <input type="text" class="form-control" id="duration">
      |</div>
      |
      |TODO: select cost of purchase (xp, bp, whatever)
      |   <button type="button" class="btn btn-primary">Add Custom Charm</button>
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
  var selectedPowerType : String = selectablePowerTypes(0)

  //TODO: design concept for OK/Cancel


  //two tabs
  //one to select from list
  //one to make custom charms/spells


  @Output
  val purchasedListed = new EventEmitter[CharmRef]()

  @Output
  val createdCustom = new EventEmitter[(String, String, String)]()



  def submitCustomPower() : Unit = {
    ???
  }

  override def ngOnChanges(changes: SimpleChanges): Unit = {}
}
