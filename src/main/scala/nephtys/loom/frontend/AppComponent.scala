package nephtys.loom.frontend

import angulate2.std.Component

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "my-app",
  template =
    """<h1>I am Loom's AppComponent</h1>
      <login-area></login-area>
      <nav>
          <a routerLink="/dashboard/vanilla" routerLinkActive="active">Dashboard</a>
          <a routerLink="/new/vanilla" routerLinkActive="active">Create New Solar</a>
      </nav>
      <router-outlet></router-outlet>
    """

)
class AppComponent {

}
