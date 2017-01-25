package nephtys.loom.frontend

import angulate2.router.Router
import angulate2.std.Component
import nephtys.loom.protocol.chronicles.solar.Solar
import org.nephtys.loom.generic.protocol.InternalStructures.ID

import scala.scalajs.js

/**
  * Created by Christopher on 25.01.2017.
  */
@Component(
  selector = "chronicles-maintable",
  template =
    """Character Generation for
      |<a target="_blank" href="https://docs.google.com/document/d/1YAwXs-kAl9j6Ucy9_je2oNlVUYHZLsXpFM2mxAoo6vI/edit?usp=sharing">these Houserules.</a>
      |<hr>
      |WORK IN PROGRESS (currently not useable)
      |<hr>
      |
      |<h3>Available Characters</h3>
      |
      |<ul>
      |<li *ngFor="let instance of chroniclesInMemoryService.allCharacters; let i = index">
      | <button type="button" (click)="idButtonClicked(instance.id)" class="btn btn-primary"
      |      >{{i + 1}}: {{instance.casteString}} {{instance.name}} (edit)</button><label> {{instance.id}} </label>
      |</li>
      |</ul>
      |
    """.stripMargin
)
class MainTableChroniclesComponent(val chroniclesInMemoryService: ChroniclesInMemoryService,
                                   router : Router) {


  def idButtonClicked(id: ID[Solar]) : Unit = {
    println(s"pressed Button with id = $id")
    val r : rxjs.RxPromise[Boolean] = router.navigate(js.Array(s"/detail/chronicles/${id.id}"))
  }
}
