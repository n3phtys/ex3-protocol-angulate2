package nephtys.loom.frontend

import angulate2.router.ActivatedRoute
import angulate2.std.{Component, OnInit}

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "detail-edit-vanilla",
  template =
    """<h2>Here you can edit a vanilla aggregate</h2>"""
)
class EditVanillaComponent( route: ActivatedRoute, vanillaAggregateService: VanillaAggregateService) extends OnInit{
  override def ngOnInit(): Unit = {
    println("EditVanillaComponent initialized")
    val s = route.params.map((params, i) => "EditVanillaComponent received parameter = "+params("id")).subscribe(println)
  }
}
