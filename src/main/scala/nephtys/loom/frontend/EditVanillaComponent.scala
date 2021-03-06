package nephtys.loom.frontend

import java.util.{Date, UUID}
import java.util.concurrent.TimeUnit

import angulate2.router.{ActivatedRoute, Router}
import angulate2.std.{Component, OnInit}
import angulate2.std._
import nephtys.dualframe.cqrs.client.{DottedStringPair, DottedStringPairChange}
import nephtys.dualframe.cqrs.client.DottedStringPairChange.DottedStringPairChange
import nephtys.dualframe.cqrs.client.StringListDif.{StringListAdd, StringListDelete, StringListDif, StringListEdit}
import nephtys.loom.protocol.shared.CustomPowers.CustomPower
import nephtys.loom.protocol.shared.{Power, Powers}
import nephtys.loom.protocol.vanilla.solar.Abilities.{AbilityLikeSpecialtyAble, AbilityMatrix, Specialty}
import nephtys.loom.protocol.vanilla.solar.Attributes.AttributeBlock
import nephtys.loom.protocol.vanilla.solar.Experiences.ExperienceBox
import nephtys.loom.protocol.vanilla.solar.Misc.Caste
import nephtys.loom.protocol.zprotocols.ZSolarProtocol._
import nephtys.loom.protocol.vanilla.solar._
import org.nephtys.loom.generic.protocol.InternalStructures.{Email, ID}
import rxscalajs.subjects.BehaviorSubject

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, TimeUnit}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js
import scala.util.Try
import vis.Node

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "detail-edit-vanilla",
  template =
    """<h3 *ngIf="character">Customizing {{character.metaDescriptors.name}}{{charactercastestr}}</h3>
      |
      |
      |<div id="mynetwork"></div>
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |<hr>
      |<points-left-vanilla  *ngIf="character"  [character]="character"></points-left-vanilla>
      |<hr>
      |
      |
      |
      |
      |<collapsed-well title="Name & Co.">
      |<string-map [input]="metamap" (mapChange)="metaMapChanged($event)"></string-map>
      |</collapsed-well>
      |
      |
      |<collapsed-well title="Attributes">
      |<solar-attributes  *ngIf="character" [useTitle]="false" [attributes]="character.attributes" (change)="attributeBlockChanged($event)" ></solar-attributes>
      |</collapsed-well>
      |
      |
      |<collapsed-well title="Abilities">
      |<div>
      |<abilities-caste-vanilla  *ngIf="character"  (casteChange)="casteChanged($event)" (abilitiesChange)="abilityChanged($event)"   [input]="character.abilities" [caste]="character.caste" ></abilities-caste-vanilla>
      |<hr>
      |<string-pair-list title="Specialties"
      |[input]="specialties" [selectable]="possibleAbilities" (seqChange)="specialtiesChanged($event)"
      |></string-pair-list>
      |</div>
      |</collapsed-well>
      |
      |
      |<collapsed-well title="Charms & Spells">
      |<charm-component-vanilla *ngIf="character" [solar]="character" (createdCustom)="customCharmPurchased($event)" (purchasedListed)="listedCharmPurchased($event)"></charm-component-vanilla>
      |</collapsed-well>
      |
      |<collapsed-well title="Merits">
      |<dotted-string-list title=""
      |[selectableValues]="meritcategories"
      |[input]="meritsAsPairs"
      |(seqChange)="meritPairsChanged($event)"
      |></dotted-string-list>
      |</collapsed-well>
      |
      |
      |<collapsed-well title="Willpower">
      |<dot-control name="Permanent Willpower"  *ngIf="character"  color="green" [max]="10" [min]="5" [value]="character.willpowerDots" (valueSelected)="willpowerChanged($event)"></dot-control>
      |</collapsed-well>
      |
      |<collapsed-well title="Intimacies">
      |<string-pair-list title=""
      |[input]="intimacies" [selectable]="possibleIntimacyIntensities" (seqChange)="intimaciesChanged($event)"
      |></string-pair-list>
      |</collapsed-well>
      |
      |
      |<collapsed-well title="Notes">
      |<string-list title="" [input]="notes" (seqChange)="notesChanged($event)" ></string-list>
      |
      |</collapsed-well>
      |
      |<collapsed-well title="Experience">
      |<experience-component  *ngIf="character"
      | [input]="character.experience"  [charGenFinishedState]="! character.stillInCharGen"
      | (charGenFinished)="charGenStateChange($event)" (experienceChange)="experienceBlockChanged($event)"
      |></experience-component>
      |</collapsed-well>
      |
      |
      |
      |<collapsed-well title="Access Settings">
      |<meta-control-component [useTitle]="false" *ngIf="character"  [input]="character"
      | (ownerChanged)="ownederChanged($event)" (deleteEvent)="aggregateDeleted($event)"
      | (readersChanged)="readersChanged($event)" (publicStateChanged)="publicStateChanged($event)"
      |></meta-control-component>
      |</collapsed-well>
      |
      |
      |<!--div >
      |  <button type="button" class="btn btn-info" data-toggle="collapse" data-target="#demo">Full Solar:</button>
      |  <div id="demo" class="collapse">
      | {{character}}
      |  </div>
      |</div-->
      |
    """.stripMargin,
    styles = @@@(
"""
   | #mynetwork {
 |            height: 400px;
 |            border: 1px solid lightgray;
 |        }
  """.stripMargin)
)
class EditVanillaComponent(  route: ActivatedRoute, vanillaInMemoryService: VanillaInMemoryService, vanillaControlService: VanillaControlService, router : Router) extends OnInit{

  var character : Solar = _

  private var selected : BehaviorSubject[Option[ID[Solar]]] = BehaviorSubject(None)


  var charactercastestr : String = ""

  private def reselectCharacter(id : ID[Solar]) : Unit = {
    println(new Date().toString + ": Character changed and reloaded inside Edit Component")
    val o = vanillaInMemoryService.get(id)
    println(s"searching aggregate with id $id, result = $o")
      o.foreach(solar => setCharacter(solar))
  }

  private val subscription = vanillaInMemoryService.changes.combineLatest(selected).filter(a => a._1.isEmpty || a._2.map(_.id).contains(a._1.get.id)).debounceTime(FiniteDuration(200, TimeUnit.MILLISECONDS)).subscribe(a => a._2.foreach(id => reselectCharacter(id)))

  //after enqueueCommands returned one should reset the character variable to the new value, or listen to debounced changes

  private def id : ID[Solar] = character.id

  var specialties : Seq[StringPair] = Seq.empty
  var intimacies : Seq[StringPair] = Seq.empty
  var possibleAbilities : Seq[String] = Seq.empty
  var possibleIntimacyIntensities : Seq[String] = Intimacies.values.map(_.toString)
  var metamap : Seq[(String, String)] = Seq.empty
  var meritsAsPairs : IndexedSeq[DottedStringPair] = IndexedSeq.empty
  val meritcategories : js.Array[String] = (Merits.availableCategories.map(_.string) ++ IndexedSeq("Other")).toJSArray


  def publicStateChanged(b : Boolean) : Unit = {
    val f = vanillaControlService.enqueueCommand(SetPublic(id, b))
  }

  def readersChanged(readers : Set[Email]) : Unit = {
    val f = vanillaControlService.enqueueCommand(SetReaders(id, readers))
  }

  def aggregateDeleted(yes : Boolean) : Unit = {
    if (yes) {
      val f = vanillaControlService.enqueueCommand(Delete(id))
      val r = router.navigate(js.Array("/"))
    }
  }

  def ownerChanged(owner : Email) : Unit = {
    val f = vanillaControlService.enqueueCommand(SetOwner(id, owner))
  }


  def customCharmPurchased(newCharm : CustomPower) : Unit = {
    println(s"Custom Charm Purchased $newCharm")
    val f = vanillaControlService.enqueueCommand(PurchaseCustomCharm(id, newCharm))
  }

  def listedCharmPurchased(newCharm : Power with Product with Serializable) : Unit = {
    println(s"Listed Charm Purchased $newCharm")
    val charmIndex : Int = Powers.powersIndexMap.getOrElse(newCharm, -1)
    val f = vanillaControlService.enqueueCommand(PurchaseListCharm(id, charmIndex))
  }


  def meritPairsChanged(change : DottedStringPairChange) : Unit = {
    println(s"Merit pairs changed via $change")
    val f = vanillaControlService.enqueueCommand( change match {
      case DottedStringPairChange.Insert(value) => AddMerit(id, value.title, value.category)
      case DottedStringPairChange.Remove(index) => ChangeMerit(id, index, None)
      case DottedStringPairChange.Edit(index, value) => ChangeMerit(id, index, Some(value.rating))
    })
  }

  def intimaciesChanged(newIntimacies : Seq[StringPair]) : Unit = {
    val innermap : Map[String, String] = newIntimacies.map(a => (a.written, a.selected)).toMap
    val add : Seq[SolarCommand] = newIntimacies.filter(a => !character.intimacies.get(a.written).exists(b => b != Intimacies.parse(a.selected))).map(a => SetIntimacy(id, a.written, Some(Intimacies.parse(a.selected))))
    val remove : Seq[SolarCommand] =  character.intimacies.toSeq.filterNot(a => innermap.contains(a._1)).map(a => SetIntimacy(id, a._1, None))
    println(s"Intimacies changed to $innermap with add = $add and remove $remove")
    val f = vanillaControlService.enqueueCommands(remove ++ add)
  }

  def specialtiesChanged(newSpecialties : Seq[StringPair]) : Unit = {
    println(s"Specialties changed to $newSpecialties")
    val add : Seq[SolarCommand] = newSpecialties.filter(a => character.abilities.specialties.get(Abilities.specialtyAble(a.selected)).filter(b => b.contains(Specialty(a.written))).isEmpty).map(a => AddSpecialty(id, specialtyAble = Abilities.specialtyAble(a.selected), title = a.written))
    val remove : Seq[SolarCommand] = character.abilities.specialties.toSeq.flatMap(a => a._2.map(b => (a._1, b))).filter(a => !newSpecialties.contains(StringPair(selected = a._1.name, written = a._2.name))).map(a => RemoveSpecialty(id, a._1.asInstanceOf[AbilityLikeSpecialtyAble], a._2.name))
    println(s"add = $add and remove = $remove")
    val f = vanillaControlService.enqueueCommands(remove ++ add)
  }

  def metaMapChanged(newmap : Seq[(String, String)]) : Unit = {
    val map = newmap.toMap
      println("newmap changed in editvanilla component")
    val c : mutable.Buffer[SolarCommand] = mutable.Buffer()
    println(s"newmap: $newmap")
    println(s"character: ${character.metaDescriptors.name} / ${character.metaDescriptors.player} / ${character.metaDescriptors.concept} / ${character.metaDescriptors.anima} / ${character.metaDescriptors.limitTrigger}")
    if (!map("Name").equals(character.metaDescriptors.name)) {
      c += SetName(character.id, name = map("Name"))
    }
    if (!map("Anima").equals(character.metaDescriptors.anima)) {
      c += SetAnima(character.id, anima = map("Anima"))
    }
    if (!map("Concept").equals(character.metaDescriptors.concept)) {
      c += SetConcept(character.id, concept = map("Concept"))
    }
    if (!map("Player").equals(character.metaDescriptors.player)) {
      c += SetPlayer(character.id, player = map("Player"))
    }
    if (!map("Limit Trigger").equals(character.metaDescriptors.limitTrigger)) {
      c += SetLimitTrigger(character.id, limitTrigger = map("Limit Trigger"))
    }
    println(s"calculated based on meta map, following changes: $c")
    val f = vanillaControlService.enqueueCommands(c)
  }

  def experienceBlockChanged(value : VanillaExperienceManualEntry) : Unit = {
    println(s"Experience changed to $value")
    val f = vanillaControlService.enqueueCommands(Seq(AddManualExperienceChange(character.id,value.amountAdded, value.typ, value.note)))
  }
  def charGenStateChange(finished : Boolean) : Unit = {
    println(s"CharGen finished = $finished")
    if (finished) {
      val f = vanillaControlService.enqueueCommand(LeaveCharacterGeneration(character.id))
    }
  }

  def abilityChanged(value : AbilityMatrix) : Unit = {
    println(s"Abilities changed to $value")
    val f = vanillaControlService.enqueueCommands(diff(character.id, character.abilities, value))
  }

  def casteChanged(value : Caste) : Unit =  {
    println(s"Caste changed to $value")
    val f = vanillaControlService.enqueueCommand(SetCaste(id, value))
  }


  def willpowerChanged(newValue : Int) : Unit = {
    println(s"Willpower changed to $newValue")
    val f = vanillaControlService.enqueueCommand(SetWillpower(id, newValue))
  }

  def attributeBlockChanged(newblock : AttributeBlock): Unit = {
      println(s"new AttribtueBlock $newblock")
    vanillaControlService.enqueueCommands(diff(character.id,character.attributes, newblock)).onComplete(t => {
      //println(s"onComplete returns $t")
      //if(t.isFailure ||t.get.exists(_.isFailure)) {
      //  resetCharacter(character)
      //}
    })
  }

  var notes : Seq[String] = Seq.empty
  def notesChanged(dif : StringListDif) : Unit = {
    println(s"notes changed via $dif")
    val f = vanillaControlService.enqueueCommands(
      dif match {
        case StringListAdd(s) => Seq(AddNote(id, s, notes.length))
        case StringListDelete(index) => Seq(RemoveNote(id, index))
        case StringListEdit(index, s) => Seq(RemoveNote(id, index), AddNote(id, s, index))
      }
    )
  }


  //title
  //control
  //meta including limit trigger as stringmap
  //attribute
  //ability
  //specialties
  //merits
  //willpower
  //intimacies
  //TODO: (charms)
  //TODO: (equipment)
  //notes
  //experience
  //left-over

  private def setCharacter(solar : Solar ) : Unit = {
    println(s"setCharacter ${solar.id} called in edit vanilla component")
    character = solar
    metamap = Seq("Name" -> solar.metaDescriptors.name, "Player" -> solar.metaDescriptors.player, "Concept" -> solar.metaDescriptors.concept, "Anima" -> solar.metaDescriptors.anima, "Limit Trigger" -> solar.metaDescriptors.limitTrigger)
    specialties = solar.abilities.specialties.flatMap(a => a._2.map(b => StringPair(a._1.name, b.name))).toSeq
    possibleAbilities = solar.abilities.specialtyAbles
    intimacies = solar.intimacies.map(a => StringPair(selected = a._2.toString, written = a._1)).toSeq
    meritsAsPairs = solar.merits.map(m => DottedStringPair(category = m.category.map(_.string).getOrElse(""), title = m.name, rating = m.rating.number.toInt)).toIndexedSeq
    charactercastestr = solar.caste.map(c => " ( " + c.toString + " )").getOrElse("")
  }


  private val defaultSolarEmpty : Solar = Characters.emptySolar(ID[Solar](UUID.randomUUID()), Email("something@dot.com"))

  private def resetCharacter(solar : Solar) : Unit = {
    setCharacter(defaultSolarEmpty)
    import scala.concurrent.ExecutionContext.Implicits.global
    val f = Future{
      setCharacter(solar)
    }

  }

  override def ngOnInit(): Unit = {
    println("ngOnInit called in edit vanilla component")
    val s = route.params.map[ID[Solar]]((params, i) => {
      println(s"Param changed to ${params("id")}")
      val id : String = params("id")
      ID[Solar](UUID.fromString(id))
    }
      )
      .subscribe(o => {
        reselectCharacter(o)
        selected.next(Some(o))
      })

    buildNetwork()
  }


  def buildNetwork() : Unit = {
    // create an array with nodes
    var nodes = new vis.DataSet[vis.Node](js.Array(
      new Node(id = "1", label = "Node 1"),
      new Node(id = "2", label = "Node 2"),
      new Node(id = "3", label = "Node 3"),
      new Node(id = "4", label = "Node 4"),
      new Node(id = "5", label = "Node 5")
      ))

    // create an array with edges
    var edges = new vis.DataSet[vis.Edge](js.Array(
      new vis.Edge(from = "1", to = "3", toArrow = true),
      new vis.Edge(from = "1", to = "2", toArrow = true),
      new vis.Edge(from = "2", to = "1", toArrow = true),
      new vis.Edge(from = "2", to = "4"),
      new vis.Edge(from = "2", to = "5")
    ))

    // create a network
    var container = org.scalajs.dom.window.document.getElementById("mynetwork")

    // provide the data in the vis format
    var data = new vis.Data(
      nodes = nodes,
      edges = edges
    )
    var options = new vis.Options(
        layout = new vis.Layout(new vis.Hierarchical(vis.UD))
    )

    // initialize your network!
    var network = new vis.Network(container, data, options)

  }
}
