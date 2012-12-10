import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "StackAnalyser"
    val appVersion      = "1.0-SNAPSHOT"

val appDependencies = Seq(
   // Add your project dependencies here,
  "commons-io" % "commons-io" % "2.3"
)

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
