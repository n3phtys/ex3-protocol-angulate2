package nephtys.loom.frontend

import java.util.UUID

import angulate2.core.OnInitJS
import angulate2.router.ActivatedRoute
import angulate2.std.{Component, OnInit}
import nephtys.loom.protocol.vanilla.solar.Abilities.AbilityMatrix
import nephtys.loom.protocol.vanilla.solar.Attributes.AttributeBlock
import nephtys.loom.protocol.vanilla.solar.Experiences.ExperienceBox
import nephtys.loom.protocol.vanilla.solar.Misc.Caste
import nephtys.loom.protocol.vanilla.solar.{Intimacies, Solar}
import org.nephtys.loom.generic.protocol.InternalStructures.ID

import scala.util.Try

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "detail-edit-vanilla",
  template =
    """<h2 *ngIf="character">Customizing {{character.name}}</h2>
      |
      |<meta-control-component></meta-control-component>
      |
      |<h3>Metadata</h3>
      |<string-map [input]="metamap" (mapChange)="metaMapChanged($event)"></string-map>
      |
      |<solar-attributes [attributes]="character.attributes" (change)="attributeBlockChanged($event)" ></solar-attributes>
      |
      |<abilities-caste-vanilla  *ngIf="character"  (casteChange)="casteChanged($event)" (abilitiesChange)="abilityChanged($event)"   [input]="character.abilities" [caste]="character.caste" ></abilities-caste-vanilla>
      |
      |<string-pair-list title="Specialties"
      |[input]="specialties" [selectable]="possibleAbilities" (seqChange)="specialtiesChanged($event)"
      |></string-pair-list>
      |
      |<h3>Permanent Willpower</h3>
      |<dot-control name="Willpower" color="green" [max]="10" [min]="5" [value]="character.willpowerDots" (valueSelected)="willpowerChanged($event)"></dot-control>
      |
      |<string-pair-list title="Intimacies"
      |[input]="intimacies" [selectable]="possibleIntimacyIntensities" (seqChange)="intimaciesChanged($event)"
      |></string-pair-list>
      |
      |<string-list title="Notes" [input]="notes" (seqChange)="notesChanged($event)" ></string-list>
      |
      |<experience-component
      | [input]="character.experience"  [charGenFinishedState]="! character.stillInCharGen"
      | (charGenFinished)="charGenStateChange($event)" (experienceChange)="experienceBlockChanged($event)"
      |></experience-component>
      |<div> full: {{character}} </div>
      |<points-left-vanilla [character]="character"></points-left-vanilla>
    """.stripMargin
)
class EditVanillaComponent(  route: ActivatedRoute, vanillaAggregateService: VanillaAggregateService) extends OnInitJS{

  var character : Solar = _

  var specialties : Seq[StringPair] = Seq.empty
  var intimacies : Seq[StringPair] = Seq.empty
  var possibleAbilities : Seq[String] = Seq.empty
  var possibleIntimacyIntensities : Seq[String] = Intimacies.values.map(_.toString)


  def intimaciesChanged(newIntimacies : Seq[StringPair]) : Unit = {
    println(s"Intimacies changed to $newIntimacies")
  }

  def specialtiesChanged(newSpecialties : Seq[StringPair]) : Unit = {
    println(s"Specialties changed to $newSpecialties")
  }

  var metamap : Seq[(String, String)] = Seq.empty
  def metaMapChanged(newmap : Seq[(String, String)]) : Unit = {
      println("newmap changed in editvanilla component")
  }

  def experienceBlockChanged(value : ExperienceBox) : Unit = {
    println(s"Experience changed to $value")
  }
  def charGenStateChange(finished : Boolean) : Unit = {
    println(s"CharGen finished = $finished")
  }

  def abilityChanged(value : AbilityMatrix) : Unit = {
    println(s"Abilities changed to $value")
  }

  def casteChanged(value : Caste) : Unit =  {
    println(s"Caste changed to $value")
  }


  def willpowerChanged(newValue : Int) : Unit = {
    println(s"Willpower changed to $newValue")
  }

  def attributeBlockChanged(newblock : AttributeBlock): Unit = {
      println(s"new AttribtueBlock $newblock")
  }

  var notes : Seq[String] = Seq.empty
  def notesChanged(seq : Seq[String]) : Unit = {
    println(s"notes changed to $seq")
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
    character = solar
    metamap = Seq("Name" -> solar.name, "Player" -> solar.player, "Concept" -> solar.concept, "Anima" -> solar.anima, "Limit Trigger" -> solar.limitTrigger.trigger)
    specialties = solar.abilities.specialties.flatMap(a => a._2.map(b => StringPair(a._1.name, b.name))).toSeq
    possibleAbilities = solar.abilities.specialtyAbles
    intimacies = solar.intimacies.map(a => StringPair(selected = a._2.toString, written = a._1)).toSeq
  }

  override def ngOnInit(): Unit = {
    val s = route.params.map[Option[Solar]]((params, i) => {
      val id : String = params("id")
      val sid = ID[Solar](UUID.fromString(id))
      println(s"SID: $sid")
      vanillaAggregateService.getInstance(sid)
    }
      )
      .subscribe(o => {
        o.foreach(setCharacter)
      })
  }
}
