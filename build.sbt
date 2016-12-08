
enablePlugins(ScalaJSPlugin)
enablePlugins(Angulate2Plugin)

name := "ex3-protocol-angulate"

version := "0.0.1"

scalaVersion := "2.11.8"


val actualProtocolurl =  "https://github.com/n3phtys/protocol-vanilla-solar.git"
val genericFrontendUrl = "https://github.com/n3phtys/cqrs-client-frame.git"

lazy val actualProtocolProject = RootProject(uri(actualProtocolurl))
lazy val genericFrontendProject = RootProject(uri(genericFrontendUrl))


lazy val root = Project("ex3-protocol-angulate", file("."))
  .settings(
    publish := {},
    publishLocal := {}
  )
  .dependsOn(actualProtocolProject, genericFrontendProject)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.lukajcb" %%% "rxscala-js" % "0.9.2",
      "com.lihaoyi" %%% "upickle" % "0.4.3"
    ),
    ngBootstrap := Some("nephtys.loom.frontend.AppModule"),
    scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-target:jvm-1.8", "-unchecked",
      "-Ywarn-adapted-args", "-Ywarn-value-discard", "-Xlint"),
    jsDependencies += "org.webjars.npm" % "rxjs" % "5.0.0-rc.4" / "bundles/Rx.min.js" commonJSName "Rx",
      jsDependencies += "org.webjars" % "jquery" % "2.1.3" / "2.1.3/jquery.js"

  )
