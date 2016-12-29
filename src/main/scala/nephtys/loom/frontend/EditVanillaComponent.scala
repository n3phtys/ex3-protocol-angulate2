package nephtys.loom.frontend

import java.util.UUID

import angulate2.core.OnInitJS
import angulate2.router.ActivatedRoute
import angulate2.std.{Component, OnInit}
import nephtys.loom.protocol.vanilla.solar.Attributes.AttributeBlock
import nephtys.loom.protocol.vanilla.solar.Solar
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
      |<abilities-caste-vanilla *ngIf="character" [input]="character.abilities" [caste]="character.caste" ></abilities-caste-vanilla>
      |
      |<string-pair-list title="Specialties"></string-pair-list>
      |
      |<h3>Permanent Willpower</h3>
      |<dot-control name="Willpower" color="green" [max]="10" [min]="5" [value]="character.willpowerDots" (valueSelected)="willpowerChanged($event)"></dot-control>
      |
      |<string-pair-list title="Intimacies"></string-pair-list>
      |
      |<string-list title="Notes" [input]="notes" (seqChange)="notesChanged($event)" ></string-list>
      |
      |<experience-component></experience-component>
      |<div> full: {{character}} </div>
      |<points-left-vanilla [character]="character"></points-left-vanilla>
    """.stripMargin
)
class EditVanillaComponent(  route: ActivatedRoute, vanillaAggregateService: VanillaAggregateService) extends OnInitJS{

  var character : Solar = _

  //TODO: connect abilities
  //TODO: connect specialties
  //TODO:  connnect intimacies
  //TODO: connect experience


  var metamap : Seq[(String, String)] = Seq.empty
  def metaMapChanged(newmap : Seq[(String, String)]) : Unit = {
      println("newmap changed in editvanilla component")
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
