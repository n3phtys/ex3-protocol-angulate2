package nephtys.loom.frontend

import angulate2.std.Component

/**
  * Created by Christopher on 25.01.2017.
  */
@Component(
  selector = "protocol-overview",
  template =
    """<h3>Available Protocols for Character Generation:</h3>
      |
      |<div style="text-align:center">
      |<a routerLink="/dashboard/vanilla"><button type="button" class="btn btn-warning">Exalted 3rd Edition - Solar PC</button></a>
      |<hr>
      |<a routerLink="/dashboard/chronicles"><button type="button" class="btn btn-info">CofE Houserules - Solar PC</button></a>
      |</div>
      |
      |
    """.stripMargin
)
class AllProtocolsOverviewComponent {

}
