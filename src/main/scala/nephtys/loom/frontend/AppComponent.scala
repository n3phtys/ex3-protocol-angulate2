package nephtys.loom.frontend

import angulate2.std.Component

/**
  * Created by nephtys on 12/8/16.
  */
@Component(
  selector = "my-app",
  template =
    """
       |<nav class="navbar navbar-inverse">
 |  <div class="container-fluid">
 |    <div class="navbar-header">
 |      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
 |        <span class="icon-bar"></span>
 |        <span class="icon-bar"></span>
 |        <span class="icon-bar"></span>
 |      </button>
 |      <a class="navbar-brand" href="#">{{title}}</a>
 |    </div>
 |    <div class="collapse navbar-collapse" id="myNavbar">
 |      <ul class="nav navbar-nav">
 |        <li><a href="#">Home</a></li>
 |        <li class="dropdown">
 |          <a class="dropdown-toggle" data-toggle="dropdown" href="#">Exalted Third Edition <span class="caret"></span></a>
 |          <ul class="dropdown-menu">
 |          <li><a routerLink="/dashboard/vanilla" routerLinkActive="active">All my Characters</a></li>
 |          <li><a routerLink="/new/vanilla" routerLinkActive="active">Create New Solar</a></li>
 |            <!--li><a href="#">Page 1-1</a></li>
 |            <li><a href="#">Page 1-2</a></li-->
 |          </ul>
 |        </li>
 |        <!--li><a href="#">Page 2</a></li>
 |        <li><a href="#">Page 3</a></li-->
 |      </ul>
 |      <div class="nav navbar-nav navbar-right">
 |        <login-area></login-area>
 |      </div>
 |    </div>
 |  </div>
 |</nav>
 |<div class="container">
 |<router-outlet></router-outlet>
 |</div>
    """.stripMargin

)
class AppComponent {
  val title : String = "Loom of Characters"

}
