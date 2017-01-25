package nephtys.loom.frontend

import java.util.UUID

import angulate2.router.Router
import angulate2.std.Component
import nephtys.loom.frontend.IncrementalChanges.{Deletion, Insertion}
import nephtys.loom.protocol.vanilla.solar.Misc.Caste
import nephtys.loom.protocol.vanilla.solar.Solar
import org.nephtys.loom.generic.protocol.InternalStructures.ID

import scala.collection.mutable
import scala.scalajs.js.JSConverters._
import scala.scalajs.js

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "main-table-vanilla",
  template =
    """<!--h4>Here will be the vanilla main data table</h4-->
      |<ul>
      |<li *ngFor="let instance of vanillaInMemoryService.allCharacters; let i = index">
      | <button type="button" (click)="idButtonClicked(instance.id)" class="btn btn-primary"
      |      >{{i + 1}}: {{instance.casteString}} {{instance.name}} (edit)</button><label> {{instance.id}} </label>
      |</li>
      |</ul>
    """.stripMargin
)
class MainTableVanillaComponent(val vanillaInMemoryService: VanillaInMemoryService,
                                router : Router) {


  def idButtonClicked(id : ID[Solar]) : Unit = {
    println(s"pressed Button with id = $id")
    val r : rxjs.RxPromise[Boolean] = router.navigate(js.Array(s"/detail/vanilla/${id.id}"))
  }
}
