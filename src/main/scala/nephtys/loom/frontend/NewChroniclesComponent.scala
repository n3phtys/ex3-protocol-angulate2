package nephtys.loom.frontend

import java.util.UUID

import angulate2.router.Router
import angulate2.std.{Component, OnInit}
import nephtys.dualframe.cqrs.client.TokenService
import nephtys.loom.protocol.chronicles.solar.ChroniclesSolarProtocol.{Create, SetName, SolarCommand}
import nephtys.loom.protocol.chronicles.solar.Solar
import org.nephtys.loom.generic.protocol.InternalStructures.{Email, ID}

import scala.scalajs.js
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Christopher on 25.01.2017.
  */
@Component(
  selector = "chronicles-newcomponent",
  template = """chronicles newcomponent here"""
)
class NewChroniclesComponent(router: Router,
                             tokenService: TokenService,
                             chroniclesControlService: ChroniclesControlService) extends OnInit {

  var writtenName: String = ""

  private var email: Option[Email] = None

  private def setEmail(s: String): Unit = {
    val o: Option[Email] = Some(Email(s))
    email = o.filter(e => e.email.nonEmpty)
  }

  tokenService.currentEmail.subscribe(setEmail _)

  private def getEmail: Email = {
    //if not given, use manually stored, if not stored ask
    if (email.isDefined) {
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

  def createnew(): Unit = {
    val id = UUID.randomUUID()
    val c: Seq[SolarCommand] = Seq(Create(id = ID[Solar](id), owner = getEmail), SetName(ID[Solar](id), writtenName))

    val b = chroniclesControlService.enqueueCommands(c).onComplete(t => router.navigate(js.Array("/"))) //TODO: link to new insert

  }

  override def ngOnInit(): Unit = {
    //todo: generate useful but overkill names
    writtenName = (new Random().alphanumeric take 10).mkString
  }
}