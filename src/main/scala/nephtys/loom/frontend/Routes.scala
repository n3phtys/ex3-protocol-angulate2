package nephtys.loom.frontend

import angulate2.router.Route
import angulate2.std.%%
import nephtys.loom.protocol.vanilla.solar.Solar
import org.nephtys.loom.generic.protocol.InternalStructures.ID

import scala.scalajs.js

/**
  * Created by nephtys on 12/8/16.
  */
object Routes {
  //def editVanillaUrl(id : ID[Solar]) = s"/detail/vanilla/${id.id.toString}"


  val routes : js.Array[Route] = {
    js.Array(Route(
      path = "new/vanilla",
      component = %%[NewVanillaComponent]
    ),Route(
      path = "dashboard/vanilla",
      component = %%[DashboardVanillaComponent]
    ),Route(
      path = "",
      redirectTo = "/dashboard/vanilla",
      pathMatch = "full"
    ),Route(
      path = "detail/vanilla/:id",
      component = %%[EditVanillaComponent]
    ))
  }
}
