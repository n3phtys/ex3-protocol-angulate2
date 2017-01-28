package nephtys.loom.frontend

import angulate2.core.{EventEmitter, Input, OnInit, Output}
import angulate2.std.Component
import nephtys.dualframe.cqrs.client.StringListDif.{StringListAdd, StringListDif}
import nephtys.loom.protocol.chronicles.solar.Abilities
import nephtys.loom.protocol.chronicles.solar.Abilities.AbilitySet
import nephtys.loom.protocol.vanilla.solar.Abilities._
import nephtys.loom.protocol.vanilla.solar.{Abilities, Misc}
import nephtys.loom.protocol.vanilla.solar.Misc.Caste
import org.scalajs.dom.raw.Event

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.{Array, JSConverters}
/**
  * Created by Christopher on 25.01.2017.
  */@Component(
  selector = "chronicles-abilities",
  template =
    """
      |
      |
      |<div class="form-group">
      | <label for="sel1">Select Solar Caste</label>
      |    <select class="form-control"  id="sel1" name="caste" [ngModel]="selectedCasteStr" (ngModelChange)="casteChanged($event)">
      |        <option *ngFor="let c of possibleCastes" [value]="c">
      |        {{c}}
      |        </option>
      |    </select>
      |    </div>
      |<div *ngIf="selectedCasteStr" >caste abilities for {{selectedCasteStr}}: <label *ngFor="let s of casteAbilities" > - {{s}} - </label>
      |</div>
      |<hr>
      |
      |
      |<div>
      |<h4>Abilities & Specialties</h4>
      |
      |<div *ngFor="let a of abilities; let i = index">
      |
      |<select [name]="a" [ngModel]="types[i]" (ngModelChange)="typeChanged(i, $event)">
      |        <option *ngFor="let t of possibleTypeStates; let j = index" [value]="t" [disabled]="! typeAble[i][j]">
      |        {{t}}
      |        </option>
      |    </select>
      |
      |
      |
      |<dot-control [name]="a" [allowInconsistentState]="true" (valueSelected)="ratingChanged(i,$event)"  color="purple" [value]="ratings[i]">
      |</dot-control>
      |<string-list title=""  [input]="" [enableEdit]="false"  [enableRemove]="false"       (seqChange)="specialtySeqChange(i, $event)" ></string-list>
      |
      |<hr *ngIf="abilities.length != i + 1">
      |</div>
      |
      |
      |
      |
      |
      |<button (click)="addAbilityBtnPressed()" >Add Ability</button>
      |</div>
      |
    """.stripMargin
)
class ChroniclesAbilityComponent extends OnInit{


  //one caste select

  //per ability:
  //name label
  //type select
  //dot control
  //specialty list

  //also one button to add abilities


  def specialtySeqChange(index : Int, sld : StringListDif) : Unit = {
    //can actually only add (or at least should)
    assert(sld.isInstanceOf[StringListDif])
    abilitiesChange.emit(ChroniclesAddSpecialty(abilities(index), sld.asInstanceOf[StringListAdd].value))
  }

  def addAbilityBtnPressed() : Unit = {
    val s = org.scalajs.dom.window.prompt("Name of new Ability? Keep empty to abort")

    if (s != null && s.trim.nonEmpty && inputMatrix.abilities.exists(_.name.equalsIgnoreCase(s))) {
      abilitiesChange.emit(ChroniclesAddAbility(s))
    }
  }

  def ratingChanged(index : Int, value : Int) : Unit = {
    abilitiesChange.emit(ChroniclesSetAbilityRating(abilities(index), value))
  }

  def casteChanged(obj : Event) : Unit = {
    val c : Caste = Misc.StrToCaste(obj.toString)
    abilitiesChange.emit(ChroniclesCasteChanged(c))
  }

  def typeChanged(index : Int, newValue : Event) : Unit = {
    val typ : Type = nephtys.loom.protocol.vanilla.solar.Abilities.strToTypes.getOrElse(newValue.toString, Normal)
    abilitiesChange.emit(ChroniclesSetAbilityType(abilities(index), typ))
  }


  var typeAble : js.Array[js.Array[Boolean]] = js.Array() // calculate for every typeGroup the 4 possible legal options

  val possibleTypeStates: js.Array[String] = nephtys.loom.protocol.vanilla.solar.Abilities.types.map(_.toString).toJSArray
  val possibleCastes: js.Array[String] = Misc.Castes.map(_.toString).toJSArray

  var abilities : js.Array[String] = js.Array()
  var types : js.Array[String] = js.Array()
  var specialties : js.Array[Seq[String]] = js.Array()
  var ratings : js.Array[Int] = js.Array()

  var selectedCasteStr : String = _


  //output: ChroniclesAbilityChange
  @Output
  val abilitiesChange = new EventEmitter[ChroniclesAbilityChange]()

  //input: abilitymatrix
  @Input
  var inputMatrix: AbilitySet = nephtys.loom.protocol.chronicles.solar.Abilities.emptyMatrix

  //input: caste
  @Input
  var inputCaste : Option[Caste] = None


  var numberOfSupernalAbilitiesSet : Int = 0
  var numberOfCasteAbilitiesSet : Int = 0
  var numberOfFavoredAbilitiesSet : Int = 0

  override def ngOnInit(): Unit = {

    numberOfCasteAbilitiesSet = 0
    numberOfFavoredAbilitiesSet = 0
    numberOfSupernalAbilitiesSet = 0
    inputMatrix.abilities.zipWithIndex.foreach(pack => inputMatrix.types(pack._2) match {
      case Normal =>
      case Favored => numberOfFavoredAbilitiesSet += 1
      case Caste => numberOfCasteAbilitiesSet += 1
      case Supernal => numberOfSupernalAbilitiesSet += 1
    })

    typeAble = inputMatrix.abilities.zipWithIndex.map(pack => Array(
      true,
      inputMatrix.types(pack._2) == Caste || (numberOfCasteAbilitiesSet < 4 && nephtys.loom.protocol.vanilla.solar.Abilities.preprogrammed.isCasteAbility(inputCaste, pack._1.name)),
      inputMatrix.types(pack._2) == Favored || numberOfFavoredAbilitiesSet < 5,
      inputMatrix.types(pack._2) == Supernal || (numberOfSupernalAbilitiesSet < 1 && nephtys.loom.protocol.vanilla.solar.Abilities.preprogrammed.isCasteAbility(inputCaste, pack._1.name))
    ) ).toJSArray


    selectedCasteStr = inputCaste.map(_.toString).getOrElse("")
    abilities = inputMatrix.abilities.map(_.name).toJSArray
    types = inputMatrix.types.map(_.toString).toJSArray
    ratings = inputMatrix.ratings.toJSArray
    specialties = inputMatrix.specialties.map(_.toSeq).toJSArray
  }
}

sealed trait ChroniclesAbilityChange
final case class ChroniclesCasteChanged(caste : Caste) extends ChroniclesAbilityChange
final case class ChroniclesAddAbility(ability : String) extends ChroniclesAbilityChange
final case class ChroniclesAddSpecialty(ability : String, specialty : String) extends ChroniclesAbilityChange
final case class ChroniclesSetAbilityRating(ability : String, rating : Int) extends ChroniclesAbilityChange
final case class ChroniclesSetAbilityType(ability : String, typ : Type) extends ChroniclesAbilityChange

