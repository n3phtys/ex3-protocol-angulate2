package nephtys.loom.frontend

import angulate2.forms.FormsModule
import angulate2.platformBrowser.BrowserModule
import angulate2.std.{@@, NgModule}
import nephtys.dualframe.cqrs.client.ClientFrameModule

/**
  * Created by Christopher on 25.01.2017.
  */
@NgModule(
  imports = @@[ClientFrameModule, BrowserModule, FormsModule],
  providers = @@[CharmService],
  declarations = @@[AttributeComponent, CharmComponent, StringPairComponent],
  exports = @@[AttributeComponent, CharmComponent, StringPairComponent]
)
class SharedComponentsModule {

}
