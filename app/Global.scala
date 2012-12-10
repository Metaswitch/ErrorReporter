    import play.api._
    import play.api.Play.current
    import play.api.libs.concurrent.Akka
    import akka.actor.Props
    import akka.actor.Actor
    import java.io.File
    import controllers.ErrorReportFile
    import models.DatabaseAccess
    import controllers.Application
    import controllers.FileReader

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("TED say@ Application has started")
    blah.doSomething
//    DatabaseAccess.dropAllData
  }

  override def onStop(app: Application) {
    Logger.info("TED say@ Application shutdown...")
  }
}

    object blah
    {
      def doSomething() =
      {
        import akka.util.duration._
        val myActor = Akka.system.actorOf(Props[FileProcessor], name = "myActor")
        Akka.system.scheduler.schedule(0 seconds, 15 seconds, myActor, DirectoryToList(Application.pending))
         myActor ! DirectoryToList(Application.pending)
      }
    }

case class FileMove(from: File, to: File)
//    case class FileList(files: List[String])
case class DirectoryToList(directory: String)

class FileProcessor extends Actor {
  def receive = {
    case FileMove(from, to) =>
      {
        Logger.info("Move file from " + from + " to " + to)

        if (to.exists())
        {
          //Assume that to and from are identical.
          // Delete the database record so we recreate it and delete the from
          // file so we don't process it again.
          DatabaseAccess.deleteReport(to.getName)
          from.delete()
        }
        else
        {
//          from.renameTo(to)
                              org.apache.commons.io.FileUtils.moveFile(from,to)
        }

        val report = new ErrorReportFile(to.getPath)
        DatabaseAccess.persistErrorReport(report.errorReport)
      }

    case DirectoryToList(directory) =>
      {
        val allFiles = new File(directory).listFiles
        for (aFile <- allFiles if aFile.getName().matches(FileReader.reportRegexString)) {
          Logger.info("Found report" + aFile.getName)
          self ! FileMove(aFile, new File(Application.moved, aFile.getName))
        }
      }
    }
  }