package nephtys.loom.frontend

import java.util.{Date, UUID}
import java.util.concurrent.TimeUnit

import angulate2.core.OnInitJS
import angulate2.router.ActivatedRoute
import angulate2.std.{Component, OnInit}
import nephtys.dualframe.cqrs.client.StringListDif.{StringListAdd, StringListDelete, StringListDif, StringListEdit}
import nephtys.loom.protocol.vanilla.solar.Abilities.{AbilityMatrix, Specialty}
import nephtys.loom.protocol.vanilla.solar.Attributes.AttributeBlock
import nephtys.loom.protocol.vanilla.solar.Experiences.ExperienceBox
import nephtys.loom.protocol.vanilla.solar.Misc.Caste
import nephtys.loom.protocol.vanilla.solar.SolarProtocol._
import nephtys.loom.protocol.vanilla.solar.{Abilities, Intimacies, Solar}
import org.nephtys.loom.generic.protocol.InternalStructures.{Email, ID}
import rxscalajs.subjects.BehaviorSubject

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{FiniteDuration, TimeUnit}
import scala.util.Try

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "detail-edit-vanilla",
  template =
    """<h2 *ngIf="character">Customizing {{character.name}}</h2>
      |
      |<meta-control-component  *ngIf="character"  [input]="character"
      | (ownerChanged)="ownederChanged($event)" (deleteEvent)="aggregateDeleted($event)"
      | (readersChanged)="readersChanged($event)" (publicStateChanged)="publicStateChanged($event)"
      |></meta-control-component>
      |
      |<h3>Metadata</h3>
      |<string-map [input]="metamap" (mapChange)="metaMapChanged($event)"></string-map>
      |
      |<solar-attributes  *ngIf="character"  [attributes]="character.attributes" (change)="attributeBlockChanged($event)" ></solar-attributes>
      |
      |<abilities-caste-vanilla  *ngIf="character"  (casteChange)="casteChanged($event)" (abilitiesChange)="abilityChanged($event)"   [input]="character.abilities" [caste]="character.caste" ></abilities-caste-vanilla>
      |
      |<string-pair-list title="Specialties"
      |[input]="specialties" [selectable]="possibleAbilities" (seqChange)="specialtiesChanged($event)"
      |></string-pair-list>
      |
      |<h3>Permanent Willpower</h3>
      |<dot-control name="Willpower"  *ngIf="character"  color="green" [max]="10" [min]="5" [value]="character.willpowerDots" (valueSelected)="willpowerChanged($event)"></dot-control>
      |
      |<string-pair-list title="Intimacies"
      |[input]="intimacies" [selectable]="possibleIntimacyIntensities" (seqChange)="intimaciesChanged($event)"
      |></string-pair-list>
      |
      |<string-list title="Notes" [input]="notes" (seqChange)="notesChanged($event)" ></string-list>
      |
      |<experience-component  *ngIf="character"
      | [input]="character.experience"  [charGenFinishedState]="! character.stillInCharGen"
      | (charGenFinished)="charGenStateChange($event)" (experienceChange)="experienceBlockChanged($event)"
      |></experience-component>
      |<div> full: {{character}} </div>
      |<points-left-vanilla  *ngIf="character"  [character]="character"></points-left-vanilla>
    """.stripMargin
)
class EditVanillaComponent(  route: ActivatedRoute, vanillaInMemoryService: VanillaInMemoryService, vanillaControlService: VanillaControlService) extends OnInitJS{

  var character : Solar = _

  private var selected : BehaviorSubject[Option[ID[Solar]]] = BehaviorSubject(None)


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


  def publicStateChanged(b : Boolean) : Unit = {
    val f = vanillaControlService.enqueueCommand(SetPublic(id, b))
  }

  def readersChanged(readers : Set[Email]) : Unit = {
    val f = vanillaControlService.enqueueCommand(SetReaders(id, readers))
  }

  def aggregateDeleted(yes : Boolean) : Unit = {
    if (yes) {
      val f = vanillaControlService.enqueueCommand(Delete(id))
    }
  }

  def ownerChanged(owner : Email) : Unit = {
    val f = vanillaControlService.enqueueCommand(SetOwner(id, owner))
  }

  def intimaciesChanged(newIntimacies : Seq[StringPair]) : Unit = {
    println(s"Intimacies changed to $newIntimacies")
    val innermap : Map[String, String] = newIntimacies.map(a => (a.written, a.selected)).toMap
    val add : Seq[SolarCommand] = newIntimacies.filter(a => !character.intimacies.get(a.written).exists(b => b != Intimacies.parse(a.selected))).map(a => SetIntimacy(id, a.written, Some(Intimacies.parse(a.selected))))
    val remove : Seq[SolarCommand] =  character.intimacies.toSeq.filterNot(a => innermap.contains(a._1)).map(a => SetIntimacy(id, a._1, None))                  //character.intimacies.toSeq.filter(a => !newSpecialties.contains(StringPair(selected = a._1.name, written = a._2.name))).map(a => RemoveSpecialty(id, a._1, a._2.name))
    val f = vanillaControlService.enqueueCommands(remove).flatMap(a => vanillaControlService.enqueueCommands(remove))
  }

  def specialtiesChanged(newSpecialties : Seq[StringPair]) : Unit = {
    println(s"Specialties changed to $newSpecialties")
    val add : Seq[SolarCommand] = newSpecialties.filter(a => character.abilities.specialties.get(Abilities.specialtyAble(a.selected)).filter(b => b.contains(Specialty(a.written))).isEmpty).map(a => AddSpecialty(id, specialtyAble = Abilities.specialtyAble(a.selected), title = a.written))
    val remove : Seq[SolarCommand] = character.abilities.specialties.toSeq.flatMap(a => a._2.map(b => (a._1, b))).filter(a => !newSpecialties.contains(StringPair(selected = a._1.name, written = a._2.name))).map(a => RemoveSpecialty(id, a._1, a._2.name))
    val f = vanillaControlService.enqueueCommands(remove).flatMap(a => vanillaControlService.enqueueCommands(remove))
  }

  def metaMapChanged(newmap : Seq[(String, String)]) : Unit = {
    val map = newmap.toMap
      println("newmap changed in editvanilla component")
    val c : mutable.Buffer[SolarCommand] = mutable.Buffer()
    println(s"newmap: $newmap")
    println(s"character: ${character.name} / ${character.player} / ${character.concept} / ${character.anima} / ${character.limitTrigger}")
    if (!map("Name").equals(character.name)) {
      c += SetName(character.id, name = map("Name"))
    }
    if (!map("Anima").equals(character.anima)) {
      c += SetAnima(character.id, anima = map("Anima"))
    }
    if (!map("Concept").equals(character.concept)) {
      c += SetConcept(character.id, concept = map("Concept"))
    }
    if (!map("Player").equals(character.player)) {
      c += SetPlayer(character.id, player = map("Player"))
    }
    if (!map("Limit Trigger").equals(character.limitTrigger)) {
      c += SetLimitTrigger(character.id, limitTrigger = map("Limit Trigger"))
    }
    println(s"calculated based on meta map, following changes: $c")
    val f = vanillaControlService.enqueueCommands(c)
  }

  def experienceBlockChanged(value : ExperienceBox) : Unit = {
    println(s"Experience changed to $value")
    val f = vanillaControlService.enqueueCommands(diff(character.experience, value))
  }
  def charGenStateChange(finished : Boolean) : Unit = {
    println(s"CharGen finished = $finished")
    if (finished) {
      val f = vanillaControlService.enqueueCommand(LeaveCharacterGeneration(character.id))
    }
  }

  def abilityChanged(value : AbilityMatrix) : Unit = {
    println(s"Abilities changed to $value")
    val f = vanillaControlService.enqueueCommands(diff(character.abilities, value))
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
    val f = vanillaControlService.enqueueCommands(diff(character.attributes, newblock))
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
  //merit
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
    metamap = Seq("Name" -> solar.name, "Player" -> solar.player, "Concept" -> solar.concept, "Anima" -> solar.anima, "Limit Trigger" -> solar.limitTrigger)
    specialties = solar.abilities.specialties.flatMap(a => a._2.map(b => StringPair(a._1.name, b.name))).toSeq
    possibleAbilities = solar.abilities.specialtyAbles
    intimacies = solar.intimacies.map(a => StringPair(selected = a._2.toString, written = a._1)).toSeq
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
