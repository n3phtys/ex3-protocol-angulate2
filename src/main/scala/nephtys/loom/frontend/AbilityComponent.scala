package nephtys.loom.frontend

import angulate2.std._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import nephtys.loom.protocol.vanilla.solar.{Abilities, Misc}
import nephtys.loom.protocol.vanilla.solar.Abilities._
import nephtys.loom.protocol.vanilla.solar.Misc.Caste
import org.scalajs.dom.raw.Event

import scala.concurrent.Future
import scala.scalajs.js.Array

/**
  * Created by nephtys on 12/12/16.
  */
@Component(
  selector = "abilities-caste-vanilla",
  template =
    """ <div>
      |<h3>Caste</h3>
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
      |<h3>Abilities</h3>
      |<ul>
      |<li *ngFor="let pack of typeableHierarchy; let i = index" >
      |<div>
      |<select [name]="pack.title" [ngModel]="pack.typ" (ngModelChange)="typeChanged(i, $event)">
      |        <option *ngFor="let t of possibleTypeStates; let j = index" [value]="t" [disabled]="! typeAble[i][j]">
      |        {{t}}
      |        </option>
      |    </select>
      |    <label *ngIf="! pack.unique">{{pack.title}}</label>
      |<dot-control *ngIf="pack.unique" color="purple" [name]="pack.title" [value]="pack.ratings[0]"></dot-control>
      |<ol *ngIf="! pack.unique">
      |<li *ngFor="let ability of pack.abilities; let k = index" >
      |<div>
      |<dot-control [name]="ability" color="purple" [value]="pack.ratings[k]">
      |</dot-control>
      |</div></li>
      |</ol>
      |<button *ngIf="pack.addable" (click)="addNewMemberPressed(i)">Add new member Ability to {{pack.title}}</button>
      |</div>
      |</li>
      |</ul>
      |
      |<button type="button" (click)="saveBtnPressed()" class="btn btn-success"
      |      >Save changes</button>
      |
      |<button type="button" (click)="revertBtnPressed()" class="btn btn-info"
      |      >Revert changes</button>
      |</div>
    """.stripMargin,
  styles = @@@(
    """
      |
    """.stripMargin)

)
class AbilityComponent {

  @Input
  var input: AbilityMatrix = Abilities.emptyMatrix

  @Input
  var caste : Option[Caste] = None


  var typeAble : Array[Array[Boolean]] = Array() // calculate for every typeGroup the 4 possible legal options

  val possibleTypeStates: Array[String] = Abilities.types.map(_.toString).toJSArray
  val possibleCastes: Array[String] = Misc.Castes.map(_.toString).toJSArray

  var selectedCasteStr : String = _
  var selectedCaste : Option[Caste] = None

  var typeableHierarchy : Array[AbilityPack] = js.Array()

  var numberOfSupernalAbilitiesSet : Int = 0
  var numberOfCasteAbilitiesSet : Int = 0
  var numberOfFavoredAbilitiesSet : Int = 0


  var casteAbilities : Array[String] = Array()


  def setTypeableHierarchy(v :  Map[String, (Map[String, Int], Boolean, Abilities.Type)]) : Unit = {
      val ra : Seq[(String, (Seq[String], Seq[Int], Boolean, Abilities.Type))] = v.toSeq.map(e => {
        val keys : Seq[String] = e._2._1.keys.toSeq
        val values : Seq[Int] = keys.map(k => e._2._1(k))
        (e._1, (keys, values, e._2._2, e._2._3))
      })
    typeableHierarchy = ra.map(k => new AbilityPack(k._1 ,k._2._4.toString, k._2._1.toJSArray, k._2._2.toJSArray, k._2._3)).sortBy(_.title).toJSArray

    typeAble = (1 to typeableHierarchy.length).map(i => Array(false, false, false, false)).toJSArray
  }


  def casteChanged(obj : Event) : Unit = {
    println("received obj")
    val str = obj.toString
    println(s"casteChanged($str) called")
    selectedCasteStr = str
    Misc.StrToCaste.get(str).foreach(c => triggerCasteChange(c))
  }

  def typeChanged(indexOfTypeable : Int, newValue : Event) : Unit = {
    val typ : Type = Abilities.strToTypes.getOrElse(newValue.toString, Normal)
    println(s"Changing type of $indexOfTypeable to $typ")
    typeableHierarchy(indexOfTypeable).title = typ.toString
    recalcFullTypes()
  }


  def ratingChanged(indexOfTypeable : Int, indexOfSubability : Int, newValue : Int) : Unit = {
    ??? //todo: implement via events from dots control and write to temp object
  }

  def addNewMemberPressed(indexOfFamily : Int) : Unit = {
    //open text dialog to input name of new Ability, afterwards confirm

    val str : String = org.scalajs.dom.window.prompt(s"Enter the new of the new subcategory for ${typeableHierarchy(indexOfFamily).title}", "Fire")
    val ok : Boolean = org.scalajs.dom.window.confirm(s"Do you really want to create a new sub-ability with the name $str? Last chance!")
    if (ok) {
      val r1 = typeableHierarchy(indexOfFamily).abilities.push(str)
      val r2 = typeableHierarchy(indexOfFamily).ratings.push(0)
    }

  }

  def inputChanged() : Unit = {
    selectedCaste = caste
    selectedCasteStr = caste.map(_.toString).getOrElse("")
    caste.foreach(caste => casteAbilities = Abilities.preprogrammed.casteAbility(caste).map(_.name).toSeq.sorted.toJSArray)

    setTypeableHierarchy(input.buildTypeableTree)

    recalcFullTypes()


  }



  def saveBtnPressed() : Unit = {
    ??? //TODO: output changes to the world
  }

  def revertBtnPressed() : Unit = {
    inputChanged()
  }

  def recalcFullTypes() : Unit = {
    numberOfCasteAbilitiesSet = 0
    numberOfFavoredAbilitiesSet = 0
    numberOfSupernalAbilitiesSet = 0
    typeableHierarchy.foreach(pack => Abilities.strToTypes(pack.typ) match {
      case Normal =>
      case Favored => numberOfFavoredAbilitiesSet += 1
      case Caste => numberOfCasteAbilitiesSet += 1
      case Supernal => numberOfSupernalAbilitiesSet += 1
    }

    )
    println(s"recalculating typeAble for caste $selectedCaste")
    typeAble = typeableHierarchy.map(pack => Array(
      true,
      Abilities.strToTypes(pack.typ) == Caste || (numberOfCasteAbilitiesSet < 4 && Abilities.preprogrammed.isCasteAbility(selectedCaste, pack.title)),
      Abilities.strToTypes(pack.typ) == Favored || numberOfFavoredAbilitiesSet < 5,
      Abilities.strToTypes(pack.typ) == Supernal || (numberOfSupernalAbilitiesSet < 1 && Abilities.preprogrammed.isCasteAbility(selectedCaste, pack.title))
    ) )
  }

  def triggerCasteChange(caste : Caste) : Unit = {
    casteAbilities = Abilities.preprogrammed.casteAbility(caste).map(_.name).toSeq.sorted.toJSArray
    selectedCaste = Some(caste)
    println(s"Caste changed to $caste")
    recalcFullTypes()
    //TODO: trigger eventemitter
  }

  def triggerAbilityMatrixChange(changedMatrix : AbilityMatrix) : Unit = {
    println(s"Abilitymatrix changed to $changedMatrix")
    //TODO: trigger eventemitter
  }

  /*
  can add a new ability to a given list
  for general cases, you can select and set a dot rating

  selection contains as option the already set one
  AND normal
  AND favored (only if number of favored selects < 5
  AND caste (only if number of caste selects < 4 and is in caste list)
  AND supernal (only if number of supernal selects == 0 and is in caste list)

  use *ngIf on the options

  store cached values of "allowed element" in this component

  create number of array N*4 Boolean values to store this data
  a handler function is called everytime a select changes (with index)
  this index takes the previous value and the new and calculates the whole values anew


  There is a button below to save the changes (disabled without changes) and a cancel button (resets all changes)

  problem: Caste has to be set in here too, because this is needed. This is a normal full width form select



   */

  import scala.concurrent.ExecutionContext.Implicits.global
  val f = Future {
    inputChanged() //todo: call during OnChanges (once that is possible)
  }
}
