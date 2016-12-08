package nephtys.loom.frontend

import java.util.UUID

import angulate2.common.Location
import angulate2.router.ActivatedRoute
import angulate2.std.{Component, Input, OnInit}
import nephtys.loom.protocol.vanilla.solar.Solar
import org.nephtys.loom.generic.protocol.InternalStructures.ID

import scala.scalajs.js.Dictionary
import scala.util.Try

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "vanilla-solar-dashboard",
  template = "<h2>this is the dashboard</h2>"
)
class DashboardVanillaComponent( route: ActivatedRoute, vanillaAggregateService: VanillaAggregateService)
//  extends OnInit
{


  //var unchangedCharacter : Solar = _

  /*override def ngOnInit(): Unit = route.params
    .map[Option[Solar]]((params : Dictionary[String], i : Int) => Try(ID[Solar](UUID.fromString(params("id")))).toOption.flatMap((id : ID[Solar]) => vanillaAggregateService.getSolar(id)))
    .subscribe(o => o.foreach(k => this.unchangedCharacter = k ))*/
}
