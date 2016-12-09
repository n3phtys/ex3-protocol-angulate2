package nephtys.loom.frontend

import angulate2.router.Router
import angulate2.std.Component
import nephtys.loom.protocol.vanilla.solar.Solar
import org.nephtys.loom.generic.protocol.InternalStructures.ID
import scala.scalajs.js.JSConverters._
import scala.scalajs.js

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "main-table-vanilla",
  template =
    """<h2>Here will be the vanilla main data table</h2>
      |<ul>
      |<li *ngFor="let instance of ids; let i = index">
      | <button type="button" (click)="idButtonClicked(instance)" class="btn btn-primary"
      |      >{{i}}. {{instance}}</button>
      |</li>
      |</ul>
    """.stripMargin
)
class MainTableVanillaComponent(vanillaAggregateService: VanillaAggregateService, router : Router) {
  var ids : js.Array[ID[Solar]] = js.Array[ID[Solar]]()
  vanillaAggregateService.allInstances.subscribe(m => ids = m.keys.toJSArray)

  def idButtonClicked(id : ID[Solar]) : Unit = {
    println(s"pressed Button with id = $id")
    val r : rxjs.RxPromise[Boolean] = router.navigate(js.Array(s"/detail/vanilla/${id.id}"))
  }
}
