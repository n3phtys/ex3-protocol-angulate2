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
  template = "<h2>All your Exalted 3rd Edition Solar Characters:</h2>" +
    "<main-table-vanilla></main-table-vanilla>"
)
class DashboardVanillaComponent( route: ActivatedRoute) {

}
