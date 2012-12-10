package controllers

import play.api._
import play.api.data._
import play.api.data.Forms._
import scala.collection.JavaConversions._
import com.codahale.jerkson.Json._
import play.api.mvc._
import models.DatabaseAccess._
import models._
import java.text.SimpleDateFormat
import java.util.Date
import java.io.File
import play.api.libs.concurrent.Akka
import play.api.data._
import play.api.data.Forms._
import java.net.URLEncoder._
import play.api.Play.current
object Application extends Controller {
  val pending = "/mnt/jetfire"
  val moved = "/root/ErrorReports"
  val destination = "/mnt/test"

  def moveReport(filename: String, dirName: String, mailto: String) = Action {
    val report: ErrorReport = getReport(filename)
    val dirName2 = if (mailto.equalsIgnoreCase("yes")) "#%d %s".format(BugReader.maxBugID + 1, dirName) else dirName
    val destinationFile = new File(destination, dirName2)

    if (!destinationFile.exists) destinationFile.mkdir()

    org.apache.commons.io.FileUtils.moveFile(new File(moved, filename), new File(destinationFile, filename))

    if (mailto.equalsIgnoreCase("yes")) {
      val subject = encode("[Bug] " + dirName2).replaceAll("\\+", "%20");
      val body = encode(report.logText.substring(0, Math.min(report.logText.length, 100))).replaceAll("\\+", "%20");
      val complete = encode("mailto:x+1249978827234@mail.asana.com&subject=%s&body=%s".format(subject, body))
      Redirect("/?mailto=" + complete)
    } else {
      Redirect("/")
    }
  }

  def reports = Action {
    val stacks = DatabaseAccess.getAllErrorReports

    val files = (new File(moved)).list
    // Partition the stacks into ones that are backed by files and
    // those that aren't.
    // The ones that are backed by files can be retured and the
    // other should be "handled"

    val (currentfiles, missingfiles) = stacks.partition { stack => files.contains(stack.filename) }

    for (aFile <- missingfiles) yield {
      Akka.future {
        Logger.info("Removing file from database" + aFile.filename)
        DatabaseAccess.deleteReport(aFile.filename)
      }
    }

    val bugDirs = new File(destination).listFiles() filter { _.isDirectory() } map { _.getName }
    Ok(views.html.errorReports(currentfiles, bugDirs))
  }

  val archiveUniqueStackForm = Form(
    "hash" -> text)

  def addDummyData = Action {
    val aErrorReport = new ErrorReport("Filename", "+441234", "app version", "osversion", "username", "log type", "logtext")
    DatabaseAccess.persistErrorReport(aErrorReport)
    Ok("added")
  }

  def getZipFile = Action {
    val filename = "report-121107-161912.zip"
    //        val filename = "report.zip"
    val report = new ErrorReportFile(filename)
    DatabaseAccess.persistErrorReport(report.errorReport);
    Ok("OK")
  }

}