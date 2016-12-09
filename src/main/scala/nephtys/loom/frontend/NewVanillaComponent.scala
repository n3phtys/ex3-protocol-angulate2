package nephtys.loom.frontend

import java.util.UUID

import angulate2.router.Router
import angulate2.std.Component
import nephtys.loom.protocol.vanilla.solar.Solar
import org.nephtys.loom.generic.protocol.InternalStructures.ID

import scala.scalajs.js

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "create-new-vanilla",
  template =
    """<h2>Here will be a new component content</h2>
      |<button type="button" (click)="createnew()" class="btn btn-success"
      |      >Create a new instance</button>
    """.stripMargin
)
class NewVanillaComponent(router: Router, vanillaAggregateService: VanillaAggregateService) {

  def createnew() : Unit = {
      vanillaAggregateService.createNew(ID[Solar](UUID.randomUUID()), "This is a given name")
      val b : rxjs.RxPromise[Boolean] = router.navigate(js.Array("/"))
  }
}
