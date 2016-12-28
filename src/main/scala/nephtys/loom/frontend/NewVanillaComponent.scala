package nephtys.loom.frontend

import java.util.UUID

import angulate2.core.OnInitJS
import angulate2.router.Router
import angulate2.std.Component
import nephtys.loom.protocol.vanilla.solar.Solar
import org.nephtys.loom.generic.protocol.InternalStructures.ID

import scala.scalajs.js
import scala.util.Random

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "create-new-vanilla",
  template =
    """<h2>Create a new Solar Character</h2>
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
class NewVanillaComponent(router: Router, vanillaAggregateService: VanillaAggregateService) extends OnInitJS {

  var writtenName : String = ""

  def createnew() : Unit = { //TODO: ask for email if nessecary / not given by tokenservice, and link to new page after creation was finished locally
      vanillaAggregateService.createNew(ID[Solar](UUID.randomUUID()), writtenName)
      val b : rxjs.RxPromise[Boolean] = router.navigate(js.Array("/"))
  }

  override def ngOnInit(): Unit = {
    //todo: generate useful but overkill names
    writtenName = (new Random().alphanumeric take 10 ).mkString
  }
}
