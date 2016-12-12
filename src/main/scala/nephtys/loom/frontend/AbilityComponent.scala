package nephtys.loom.frontend

import angulate2.std.{@@@, Component}

/**
  * Created by nephtys on 12/12/16.
  */
@Component(
  selector = "abilities-caste-vanilla",
  template =
    """ <div>
      |this is the abilities-caste-vanilla component
      |</div>
    """.stripMargin,
  styles = @@@(
    """
      |.form-inline > * {
      |   margin:15px 15px;
      |}
    """.stripMargin)

)
class AbilityComponent {


  /*
  can add a new ability to a given list
  for general cases, you can select and set a dot rating

  selection contains as option the already set one
  AND normal
  AND favored (only if number of favored selects < 5
  AND caste (only if number of caste selects < 4 and is in caste list)
  AND supernal (only if number of supernal selects == 0 and is in caste list)

  use *ngIf on the options

  store cached values of "allowed element" in this component

  create number of array N*4 Boolean values to store this data
  a handler function is called everytime a select changes (with index)
  this index takes the previous value and the new and calculates the whole values anew


  There is a button below to save the changes (disabled without changes) and a cancel button (resets all changes)

  problem: Caste has to be set in here too, because this is needed. This is a normal full width form select



   */
}
