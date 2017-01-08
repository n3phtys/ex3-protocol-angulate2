package nephtys.loom.frontend

import java.util.UUID

import angulate2.router.Router
import angulate2.std.{Component, OnInit}
import nephtys.dualframe.cqrs.client.TokenService
import nephtys.loom.protocol.vanilla.solar.Solar
import nephtys.loom.protocol.vanilla.solar.SolarProtocol.{Create, SetName, SolarCommand}
import org.nephtys.loom.generic.protocol.InternalStructures.{Email, ID}
import rxjs.Observer

import scala.scalajs.js
import scala.util.Random
import rxscalajs.Observable
import rxscalajs.subjects.BehaviorSubject

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "create-new-vanilla",
  template =
    """<h3>Create a new Solar Character</h3>
      |<form class="form-inline">
      |  <div class="form-group">
      |    <label for="email">Randomized Character Name:</label>
      |    <input type="text" [(ngModel)]="writtenName" name="writtenName" class="form-control" id="email">
      |  </div>
      |  <button type="button" (click)="createnew()" [disabled]="writtenName.length === 0" class="btn btn-success"
      |      >Create new Solar</button>
      |</form>
      |
    """.stripMargin
)
class NewVanillaComponent(router: Router,
                          tokenService: TokenService,
                          vanillaControlService: VanillaControlService) extends OnInit {

  var writtenName : String = ""

  private var email : Option[Email] = None

  private def setEmail(s : String) : Unit = {
    val o : Option[Email] = Some(Email(s))
    email = o.filter(e => e.email.nonEmpty)
  }

  tokenService.currentEmail.subscribe(setEmail _ )

  private def getEmail : Email = {
    //if not given, use manually stored, if not stored ask
    if(email.isDefined) {
      email.get
    } else {
      if (tokenService.manualSetEmail.isEmpty) {
        val s = org.scalajs.dom.window.prompt("You do not have an OpenID Connect Token stored. Please manually enter your email address to set this character as yours.")
        tokenService.manualSetEmail = Some(s)
        Email(s)
      } else {
        Email(tokenService.manualSetEmail.get)
      }

    }
  }

  def createnew() : Unit = {
      val id = UUID.randomUUID()
      val c : Seq[SolarCommand] = Seq(Create(id = ID[Solar](id), owner = getEmail), SetName(ID[Solar](id), writtenName))

      val b = vanillaControlService.enqueueCommands(c).onComplete(t => router.navigate(js.Array("/"))) //TODO: link to new insert

  }

  override def ngOnInit(): Unit = {
    //todo: generate useful but overkill names
    writtenName = (new Random().alphanumeric take 10 ).mkString
  }
}
