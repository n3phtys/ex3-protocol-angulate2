package nephtys.loom.frontend

import angulate2.core.EventEmitter
import angulate2.core.OnChanges.SimpleChanges
import angulate2.std._
import nephtys.loom.protocol.shared.CharmRef

/**
  * Created by Christopher on 05.01.2017.
  */
@Component(
  selector = "charm-component-vanilla",
  template =
    """ <div>
      | This is the Charm Component
      |</div>
    """.stripMargin,
  styles = @@@(
    """
      |
    """.stripMargin)

)
class CharmComponent extends OnChanges {

  //two tabs
  //one to select from list
  //one to make custom charms/spells


  @Output
  val purchasedListed = new EventEmitter[CharmRef]()

  @Output
  val createdCustom = new EventEmitter[(String, String, String)]()


  override def ngOnChanges(changes: SimpleChanges): Unit = {}
}
