package nephtys.loom.frontend

import angulate2.ext.inMemoryWebApi.{InMemoryBackendConfigArgs, InMemoryWebApiModule}
import angulate2.forms.FormsModule
import angulate2.http.HttpModule
import angulate2.platformBrowser.BrowserModule
import angulate2.router.{Route, RouterModule}
import angulate2.std._
import nephtys.dualframe.cqrs.client.{ClientFrameModule, LoginComponent, LoginModule}

import scala.scalajs.js
import scala.scalajs.js.Object

/**
  * Created by nephtys on 12/8/16.
  */
@NgModule(
  imports = @@[ClientFrameModule, BrowserModule, FormsModule, LoginModule] :+
    RouterModule.forRoot(Routes.routes, js.Dynamic.literal(useHash = true)),
  providers = @@[CharmService, VanillaControlService, VanillaInMemoryService, VanillaCommandQueueService],
  declarations = @@[AppComponent, CharmComponent, AbilityComponent, PointsLeftComponent, AttributeComponent, VanillaExperienceComponent, StringPairComponent, NewVanillaComponent,DashboardVanillaComponent, MainTableVanillaComponent, EditVanillaComponent],
  bootstrap = @@[AppComponent]
)
class AppModule {

}
