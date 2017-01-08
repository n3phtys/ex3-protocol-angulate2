import java.nio.file.{FileAlreadyExistsException, Files, Path, Paths}
import java.util.function.Consumer

import scala.util.{Failure, Try}

enablePlugins(ScalaJSPlugin)
enablePlugins(Angulate2Plugin)

name := "ex3-protocol-angulate"

val scalaV = "2.11.8"

version := "0.0.2"

scalaVersion := scalaV


def shortVersion : String = scalaV.substring(0, 4)


val actualProtocolurl =  "https://github.com/n3phtys/protocol-vanilla-solar.git"
val genericFrontendUrl = "https://github.com/n3phtys/cqrs-client-frame.git"

lazy val actualProtocolProject = ProjectRef(uri(actualProtocolurl), "solarprotocolJS")

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
      "com.lihaoyi" %%% "upickle" % "0.4.4"
    ),
    ngBootstrap := Some("nephtys.loom.frontend.AppModule"),
    scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-target:jvm-1.8", "-unchecked",
      "-Ywarn-adapted-args", "-Ywarn-value-discard", "-Xlint"),
    jsDependencies += "org.webjars.npm" % "rxjs" % "5.0.0-rc.4" / "bundles/Rx.min.js" commonJSName "Rx",
      jsDependencies += "org.webjars" % "jquery" % "2.1.3" / "2.1.3/jquery.js"

  )


val createDirectories : Seq[String] = Seq("web", "web/target", s"web/target/scala-$shortVersion")

val copyDirectories : Map[String, String] = Map("node_modules" -> "web/node_modules")

val copyFiles : Map[String, String] = Map("index.html" -> "web/index.html", "package.json" -> "web/package.json", "systemjs.config.js" -> "web/systemjs.config.js",
  s"target/scala-$shortVersion/ex3-protocol-angulate-fastopt.js" -> s"web/target/scala-$shortVersion/ex3-protocol-angulate-fastopt.js",
  s"target/scala-$shortVersion/ex3-protocol-angulate-jsdeps.js" -> s"web/target/scala-$shortVersion/ex3-protocol-angulate-jsdeps.js",
  s"target/scala-$shortVersion/ex3-protocol-angulate-sjsx.js" -> s"web/target/scala-$shortVersion/ex3-protocol-angulate-sjsx.js"
)

lazy val collect = TaskKey[Unit]("collect", "Copy javascript related files to web directory")
//TODO: three steps: first create directories, afterwards copy directories, afterwards copyFiles
collect := {
  //create all directories
  createDirectories.foreach(s => Try(Files.createDirectory(Paths.get(s))))

  //copy directories
  copyDirectories.foreach( a => {
      def source = a._1
      def target = a._2
      Try {
        val stream = Files.walk(Paths.get(source))

    stream.forEach(
      new Consumer[Path] {
        def accept(path : Path) : Unit = Try({
          val targetpath : Path = Paths.get(path.toString.replace(source, target))
          val parent : File = targetpath.toFile.getParentFile
          if (parent.exists() || parent.mkdirs()) {
            Files.copy(path, targetpath)
          } else {
            println("could not create file")
          }
        }) match {
          case Failure(e) => if (!e.isInstanceOf[FileAlreadyExistsException]) println(s"Files.copy failed with $e")
          case _ => //println(s"Copied Path $path")Alread
        }
      })
      } match {
        case Failure(e) => println(s"Files.walk failed with $e")
        case _ =>
      }
  })

  //copy single files
  copyFiles.map(a => {
    def source = Paths.get(a._1)
    def target = Paths.get(a._2)
    Try(Files.copy(source, target)) match {
      case Failure(e) => if (!e.isInstanceOf[FileAlreadyExistsException]) println(s"Files.copy failed with $e")
      case _ => //println(s"Copied Path $path")Alread
    }
  })

}


addCommandAlias("complete", ";clean;fastOptJS;collect")