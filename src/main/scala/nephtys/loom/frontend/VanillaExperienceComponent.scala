package nephtys.loom.frontend

import angulate2.std._

/**
  * Created by nephtys on 12/12/16.
  */
@Component(
  selector = "experience-component",
  template =
    """ <div>
      |this is the vanilla experience component
      |</div>
    """.stripMargin,
  styles = @@@(
    """
      |.form-inline > * {
      |   margin:15px 15px;
      |}
    """.stripMargin)

)
class VanillaExperienceComponent {
  //TODO: add manual gains and manual spendings via buttons
  //TODO: show manual entries as collapsed list / log (LIFO)


}
