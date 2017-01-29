package nephtys.loom.frontend

import java.util.concurrent.TimeUnit
import java.util.{Date, UUID}

import angulate2.router.{ActivatedRoute, Router}
import angulate2.std.{Component, OnInit}
import nephtys.dualframe.cqrs.client.DottedStringPairChange.DottedStringPairChange
import nephtys.dualframe.cqrs.client.StringListDif.{StringListAdd, StringListDelete, StringListDif, StringListEdit}
import nephtys.dualframe.cqrs.client.{DottedStringPair, DottedStringPairChange}
import nephtys.loom.protocol.chronicles.solar.{CharacterFactory, Solar}
import nephtys.loom.protocol.shared.CustomPowers.CustomPower
import nephtys.loom.protocol.shared.{Power, Powers}
import nephtys.loom.protocol.vanilla.solar.Attributes.AttributeBlock
import nephtys.loom.protocol.vanilla.solar.Misc.Caste
import nephtys.loom.protocol.vanilla.solar.{Intimacies, Merits}
import nephtys.loom.protocol.zprotocols.ZChroniclesSolarProtocol._
import org.nephtys.loom.generic.protocol.InternalStructures.{Email, ID}
import rxscalajs.subjects.BehaviorSubject

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

/**
  * Created by Christopher on 25.01.2017.
  */
@Component(
  selector = "chronicles-edit",
  template =
    """
      |
      |
      |<h3 *ngIf="character">Customizing CofE {{character.metaDescriptors.name}}{{character.casteString}}</h3>
      |
      |
      |
      |
      |<hr>
      |<points-left-chronicles  *ngIf="character"  [character]="character"></points-left-chronicles>
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
      |<collapsed-well title="Caste & Abilities">
      |<chronicles-abilities [inputCaste]="character.caste" [inputMatrix]="character.abilities" (abilitiesChange)="abilityChanged($event)">
      |</chronicles-abilities>
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
      |<dot-control name="Permanent Willpower"  *ngIf="character"  color="green" [max]="10" [min]="5" [value]="character.directDotValues.willpowerDots" (valueSelected)="willpowerChanged($event)"></dot-control>
      |</collapsed-well>
      |
      |<collapsed-well title="Intimacies">
      |<string-pair-list title=""
      |[input]="intimacies" [selectable]="possibleIntimacyIntensities" (seqChange)="intimaciesChanged($event)"
      |></string-pair-list>
      |</collapsed-well>
      |
      |
      |<collapsed-well title="Aspirations">
      |<string-list title="" [input]="aspirations" (seqChange)="aspirationsChanged($event)" ></string-list>
      |</collapsed-well>
      |
      |
      |<collapsed-well title="Notes">
      |<string-list title="" [input]="notes" (seqChange)="notesChanged($event)" ></string-list>
      |
      |</collapsed-well>
      |
      |<collapsed-well title="Experience">
      |<chronicles-experience
      | *ngIf="character"
      | [input]="character.experience"  [charGenFinishedState]="! character.stillInCharGen"
      | (charGenFinished)="charGenStateChange($event)" (experienceChange)="experienceBlockChanged($event)"
      |></chronicles-experience>
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
      |<div >
      |  <button type="button" class="btn btn-info" data-toggle="collapse" data-target="#demo">Full Solar:</button>
      |  <div id="demo" class="collapse">
      | {{character}}
      |  </div>
      |</div>
      |
      |
    """.stripMargin
)
class EditChroniclesComponent(  route: ActivatedRoute, chroniclesInMemoryService: ChroniclesInMemoryService, chroniclesControlService: ChroniclesControlService, router : Router) extends OnInit{

  //differences:
  //TODO: custom experience component
  //custom ability component
  //String list component for Aspirations

  //custom points-left component

  //copy all other components from vanilla component





  private def id : ID[Solar] = character.id


  var intimacies : Seq[StringPair] = Seq.empty
  var possibleAbilities : Seq[String] = Seq.empty
  var aspirations : Seq[String] = Seq.empty
  var possibleIntimacyIntensities : Seq[String] = Intimacies.values.map(_.toString)
  var metamap : Seq[(String, String)] = Seq.empty
  var meritsAsPairs : IndexedSeq[DottedStringPair] = IndexedSeq.empty
  val meritcategories : js.Array[String] = (Merits.availableCategories.map(_.string) ++ IndexedSeq("Other")).toJSArray




  def abilityChanged(ac : ChroniclesAbilityChange) : Unit = {
    ??? //TODO: implement
  }



  def publicStateChanged(b : Boolean) : Unit = {
    val f = chroniclesControlService.enqueueCommand(SetPublic(id, b))
  }

  def readersChanged(readers : Set[Email]) : Unit = {
    val f = chroniclesControlService.enqueueCommand(SetReaders(id, readers))
  }

  def aggregateDeleted(yes : Boolean) : Unit = {
    if (yes) {
      val f = chroniclesControlService.enqueueCommand(Delete(id))
      val r = router.navigate(js.Array("/"))
    }
  }

  def ownerChanged(owner : Email) : Unit = {
    val f = chroniclesControlService.enqueueCommand(SetOwner(id, owner))
  }


  def customCharmPurchased(newCharm : CustomPower) : Unit = {
    println(s"Custom Charm Purchased $newCharm")
    val f = chroniclesControlService.enqueueCommand(PurchaseCustomCharm(id, newCharm))
  }

  def listedCharmPurchased(newCharm : Power with Product with Serializable) : Unit = {
    println(s"Listed Charm Purchased $newCharm")
    val charmIndex : Int = Powers.powersIndexMap.getOrElse(newCharm, -1)
    val f = chroniclesControlService.enqueueCommand(PurchaseListCharm(id, charmIndex))
  }


  def meritPairsChanged(change : DottedStringPairChange) : Unit = {
    //TODO: implement
    ???
  }

  def intimaciesChanged(newIntimacies : Seq[StringPair]) : Unit = {
    val innermap : Map[String, String] = newIntimacies.map(a => (a.written, a.selected)).toMap
    val add : Seq[SolarCommand] = newIntimacies.filter(a => !character.intimacies.get(a.written).exists(b => b != Intimacies.parse(a.selected))).map(a => SetIntimacy(id, a.written, Some(Intimacies.parse(a.selected))))
    val remove : Seq[SolarCommand] =  character.intimacies.toSeq.filterNot(a => innermap.contains(a._1)).map(a => SetIntimacy(id, a._1, None))
    println(s"Intimacies changed to $innermap with add = $add and remove $remove")
    val f = chroniclesControlService.enqueueCommands(remove ++ add)
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
    val f = chroniclesControlService.enqueueCommands(c)
  }

  def experienceBlockChanged(value : js.Object) : Unit = {
    //TODO: Implement
    ???
  }
  def charGenStateChange(finished : Boolean) : Unit = {
    println(s"CharGen finished = $finished")
    if (finished) {
      val f = chroniclesControlService.enqueueCommand(LeaveCG(character.id))
    }
  }

  def abilityChanged(value : js.Object) : Unit = {
    //TODO: implement
    ???
  }

  def casteChanged(value : Caste) : Unit =  {
    println(s"Caste changed to $value")
    val f = chroniclesControlService.enqueueCommand(SetCaste(id, value))
  }


  def willpowerChanged(newValue : Int) : Unit = {
    println(s"Willpower changed to $newValue")
    val f = chroniclesControlService.enqueueCommand(SetWillpower(id, newValue))
  }

  def attributeBlockChanged(newblock : AttributeBlock): Unit = {
    println(s"new AttribtueBlock $newblock")
    //TODO: implement
    ???
  }

  var notes : Seq[String] = Seq.empty
  def notesChanged(dif : StringListDif) : Unit = {
    println(s"notes changed via $dif")
    val f = chroniclesControlService.enqueueCommands(
      dif match {
        case StringListAdd(s) => Seq(AddNote(id, s, notes.length))
        case StringListDelete(index) => Seq(RemoveNote(id, index))
        case StringListEdit(index, s) => Seq(RemoveNote(id, index), AddNote(id, s, index))
      }
    )
  }

  def aspirationsChanged(dif : StringListDif) : Unit = {
    val f = chroniclesControlService.enqueueCommands(
      dif match {
        case StringListAdd(s) => Seq(SetAspiration(id, character.aspirations.size, s))
        case StringListDelete(index) => Seq(SetAspiration(id, index, ""))
        case StringListEdit(index, s) => Seq(SetAspiration(id, index, s))
      }
    )
  }















  var character : Solar = _
  private var selected : BehaviorSubject[Option[ID[Solar]]] = BehaviorSubject(None)

  private def reselectCharacter(id : ID[Solar]) : Unit = {
    println(new Date().toString + ": Character changed and reloaded inside Edit Component")
    val o = chroniclesInMemoryService.get(id)
    println(s"searching aggregate with id $id, result = $o")
    o.foreach(solar => setCharacter(solar))
  }

  private val subscription = chroniclesInMemoryService.changes.combineLatest(selected).filter(a => a._1.isEmpty || a._2.map(_.id).contains(a._1.get.id)).debounceTime(FiniteDuration(200, TimeUnit.MILLISECONDS)).subscribe(a => a._2.foreach(id => reselectCharacter(id)))


  private def setCharacter(solar : Solar ) : Unit = {
    println(s"setCharacter ${solar.id} called in edit vanilla component")
    character = solar
    metamap = Seq("Name" -> solar.metaDescriptors.name, "Player" -> solar.metaDescriptors.player, "Concept" -> solar.metaDescriptors.concept, "Anima" -> solar.metaDescriptors.anima, "Limit Trigger" -> solar.metaDescriptors.limitTrigger)
    possibleAbilities = solar.abilities.abilities.map(_.name)
    intimacies = solar.intimacies.map(a => StringPair(selected = a._2.toString, written = a._1)).toSeq
    aspirations = solar.aspirations.map(_.title)
    notes = solar.notes
    meritsAsPairs = solar.merits.map(m => DottedStringPair(category = m.category.map(_.string).getOrElse(""), title = m.name, rating = m.rating.number.toInt)).toIndexedSeq
  }


  private val defaultSolarEmpty : Solar = CharacterFactory.emptyChroniclesSolar

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
  }
}
