package nephtys.loom.frontend

import nephtys.loom.protocol.vanilla.solar.Experiences.ExperienceType

import scala.scalajs.js.annotation.{JSExport, JSExportAll}

/**
  * Created by Christopher on 07.01.2017.
  */
@JSExport
@JSExportAll
case class VanillaExperienceManualEntry(amountAdded : Int, typ : ExperienceType, note : String) {

}
