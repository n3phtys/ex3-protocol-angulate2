package nephtys.loom.frontend

import angulate2.std._

/**
  * Created by nephtys on 12/8/16.
  */
@NgModule(
  imports = @@[nephtys.dualframe.cqrs.client.ClientFrameModule],
  //providers = @@[],
  declarations = @@[AppComponent],
  bootstrap = @@[AppComponent]
)
class AppModule {

}
