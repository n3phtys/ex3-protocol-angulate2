package nephtys.loom.frontend

import java.util.UUID

import angulate2.router.ActivatedRoute
import angulate2.std.{Component, OnInit}
import nephtys.loom.protocol.vanilla.solar.Solar
import org.nephtys.loom.generic.protocol.InternalStructures.ID

import scala.util.Try

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "detail-edit-vanilla",
  template =
    """<h2>Here you can edit a vanilla aggregate</h2>
      |<div >Currently loaded: {{character?.id}}</div>
      |<div> full: {{character}} </div>
    """.stripMargin
)
class EditVanillaComponent(  route: ActivatedRoute, vanillaAggregateService: VanillaAggregateService) extends OnInit{

  var character : Solar = _

  override def ngOnInit(): Unit = {
    val s = route.params.map[Option[Solar]]((params, i) => {
      val id : String = params("id")
      val sid = ID[Solar](UUID.fromString(id))
      println(s"SID: $sid")
      vanillaAggregateService.getInstance(sid)
    }
      )
      .subscribe(o => {
        o.foreach(s => character = s)
      })
  }
}
