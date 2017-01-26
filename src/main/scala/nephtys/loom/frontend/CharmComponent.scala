package nephtys.loom.frontend

import java.util.UUID

import angulate2.core.EventEmitter
import angulate2.core.OnChanges.SimpleChanges
import angulate2.std._
import nephtys.dualframe.cqrs.client.StringListDif.{StringListAdd, StringListDelete, StringListDif, StringListEdit}
import nephtys.loom.protocol.shared.CharmDatastructures.{CharmType, Simple, Terrestrial}
import nephtys.loom.protocol.shared.CustomPowers._
import nephtys.loom.protocol.shared.{CharmDatastructures, CharmRef, Power, Powers}
import nephtys.loom.protocol.vanilla.solar.{Characters, CharmLearnable, Solar}
import org.nephtys.loom.generic.protocol.InternalStructures.{Email, ID}

import scala.scalajs.js
import scala.scalajs.js.Array
import scala.scalajs.js.JSConverters._
import scala.concurrent.ExecutionContext.Implicits.global

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
      |
      |
      |
      |
      |
      | <div *ngIf="selectedPowerType === selectablePowerTypes[0]">
      |   <div>
      |   <label>Already purchased Solar Charms:</label>
      |     <div *ngFor="let c of charmService.purchasedSolarCharms" > {{c}} </div>
      |   </div>
      |
      |   <div>
      |   <label>Available for purchase:</label>
      |     <div *ngFor="let c of charmService.purchaseableSolarCharms" >
      |
      |<button type="button" class="btn btn-success" (click)="purchaseClicked(c)"><span class="glyphicon glyphicon-ok"></span></button>
      |
      |     {{c}} (Essence {{c.essenceInt}}, {{c.abilityString}} {{c.abilityInt}})
      |
      |     </div>
      |
      |   </div>
      |
      |
      |   <div>
      |   <label>Unavailable:</label>
      |     <div *ngFor="let c of charmService.unpurchaseableSolarCharms" > {{c}} </div>
      |
      |   </div>
      |</div>
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      | <div *ngIf="selectedPowerType === selectablePowerTypes[1]">
      |   <div>
      |   <label>Already purchased Spells:</label>
      |<div *ngFor="let c of charmService.purchasedSpells" > {{c}} </div>
      |
      |   </div>
      |
      |   <div>
      |   <label>Available for purchase:</label>
      |<div *ngFor="let c of charmService.purchaseableSpells" >
      |<button type="button" class="btn btn-success" (click)="purchaseClicked(c)"><span class="glyphicon glyphicon-ok"></span></button>
      |{{c}} </div>
      |
      |   </div>
      |
      |
      |   <div>
      |   <label>Unavailable:</label>
      |<div *ngFor="let c of charmService.unpurchaseableSpells" > {{c}} </div>
      |
      |   </div>
      |</div>
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      | <div *ngIf="selectedPowerType === selectablePowerTypes[2]">
      |   <div>
      |   <label>Already purchased Non-Solar Charms:</label>
      |<div *ngFor="let c of charmService.purchasedOtherCharms" > {{c}} </div>
      |
      |   </div>
      |
      |   <div>
      |   <label>Available for purchase:</label>
      |<div *ngFor="let c of charmService.purchaseableOtherCharms" >
      |<button type="button" class="btn btn-success" (click)="purchaseClicked(c)"><span class="glyphicon glyphicon-ok"></span></button>
      |{{c}} </div>
      |
      |   </div>
      |
      |
      |   <div>
      |   <label>Unavailable:</label>
      |<div *ngFor="let c of charmService.unpurchaseableOtherCharms" > {{c}} </div>
      |
      |   </div>
      |</div>
      |
      |
      |
      |
      |
      |   </div>
      |  </tab-control>
      |  <tab-control title="Custom Charm">
      |
      |
      | <div *ngIf="customCharms.length > 0" >
      | <label>Already Purchased Custom Charms:</label>
      | <ol>
      | <li *ngFor="let c of customCharms">{{c}}</li>
      | </ol>
      | </div>
      |
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
      |  <input type="text" class="form-control" id="name" [(ngModel)]="customName">
      |  <label for="usr">Description:</label>
      |  <textarea class="form-control" id="description" [(ngModel)]="customDescription"></textarea>
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
      |  <input type="text" class="form-control" id="cost" [(ngModel)]="customCost">
      |  <label  for="usr">Duration:</label>
      |  <input type="text" class="form-control" id="duration" [(ngModel)]="customDuration">
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
      |  <input type="number" class="form-control" id="costselector" [(ngModel)]="customCostAmount">
      |
      |
      |    <div class="checkbox" *ngIf="customCostType === possibleCostTypes[0]">
      |        <label>
      |          <input type="checkbox" name="useSolarXP" [(ngModel)]="customUseSolarXP">
      |          Use Solar XP too
      |        </label>
      |      </div>
      |</div>
      |
      |
      |
      |<div>
      |   <button type="button" [disabled]="customName.length <= 2" class="btn btn-primary" (click)="submitCustomPower()">Purchase Custom Charm</button>
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
  val possibleCostTypes : js.Array[String] = js.Array("Experience", "Bonus Points", "Free Point Buy Charms")
  var selectableAbilities : js.Array[String] = js.Array("Archery")

  var customCharms : js.Array[CustomPower] = js.Array()

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
  var customUseSolarXP : Boolean = false


  var keywords : Seq[String] = Seq.empty



  def submitCustomPower() : Unit = {
    println("CustomName: " + customName)


    val cost : CustomCost = if (customCostType == possibleCostTypes(0)) {
      ExperiencePointCost(customCostAmount, customUseSolarXP)
    } else if (customCostType == possibleCostTypes(1)) {
      BonusPointCost(customCostAmount)
    } else if (customCostType == possibleCostTypes(2)) {
      FreePointCost(customCostAmount)
    } else {throw new IllegalArgumentException}
    val t : CharmType = CharmDatastructures.CharmTypes.getOrElse(customTypeSelector, Simple)
    val c : CustomPower = if (selectablePowerTypes(0) == selectedPowerType) {
      CustomSolarCharm(essence = customEssenceRating, ability = customAbilitySelector, abilityRating = 1,
        charmtype = t, name = customName, description = customDescription, cost = customCost,
        keyword = keywords.toSet, duration = customDuration, customCost = cost)
    } else if (selectablePowerTypes(1) == selectedPowerType) {
      CustomSpell(circle = CharmDatastructures.Circles.getOrElse(customCircle, Terrestrial),
        name = customName, description = customDescription, cost = customCost, keyword = keywords.toSet, duration = customDuration, customCost = cost)
    } else if (selectablePowerTypes(2) == selectedPowerType) {
      CustomEvocation(essence = customEssenceRating, charmtype = t,
        name = customName, description = customDescription,
        cost = customCost, keyword = keywords.toSet, duration = customDuration, customCost = cost)
    } else {
      throw new IllegalArgumentException
    }
    this.customName = ""
    this.createdCustom.emit(c)
  }



  //TODO: design concept for OK/Cancel / and to go back



  @Input
  var solar : CharmLearnable = Characters.emptySolar(ID[Solar](UUID.randomUUID()), Email("something@email.org"))

  @Output
  val purchasedListed = new EventEmitter[Power with Product with Serializable]()

  @Output
  val createdCustom = new EventEmitter[CustomPower]()



  def keyWordChange(change : StringListDif) : Unit = change match {
    case StringListDelete(index) => keywords = keywords.take(index) ++ keywords.drop(index + 1)
    case StringListAdd(value) => keywords = keywords.+:(value)
    case StringListEdit(index, value) => keywords = keywords.take(index).+:(value).++(keywords.drop(index + 1))
  }


  def purchaseClicked(power : Power with Product with Serializable ) : Unit = {
    println(s"purchaseClicked with $power")
    charmService.quickBuyListedPowerAndRecalculateSoon(solar, power)
    purchasedListed.emit(power)
  }

  override def ngOnChanges(changes: SimpleChanges): Unit = {
    inputChanged()
  }

  private def inputChanged() : Unit = {
    if (solar != null) {
      selectableAbilities = solar.selectableAbilities.sorted.toJSArray
      charmService.recalculateForCharacter(solar)//.foreach(s => println("Recalculated Charms"))
      customCharms = solar.customCharms.toJSArray
    }
  }
  inputChanged()
}
