package nephtys.loom.frontend

import angulate2.forms.FormsModule
import angulate2.platformBrowser.BrowserModule
import angulate2.router.RouterModule
import angulate2.std.{@@, NgModule}
import nephtys.dualframe.cqrs.client.{ClientFrameModule, LoginModule}

import scala.scalajs.js

/**
  * Created by Christopher on 25.01.2017.
  */
@NgModule(
  imports = @@[ClientFrameModule, BrowserModule, FormsModule, SharedComponentsModule],
  providers = @@[ChroniclesControlService, ChroniclesInMemoryService, ChroniclesCommandQueueService],
  declarations = @@[ChroniclesExperienceComponent, ChroniclesPointLeftComponent, EditChroniclesComponent, MainTableChroniclesComponent, NewChroniclesComponent, ChroniclesAbilityComponent],
  exports = @@[ EditChroniclesComponent, MainTableChroniclesComponent, NewChroniclesComponent]
)
class ChroniclesSolarModule {

}
