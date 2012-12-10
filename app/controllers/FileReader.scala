package controllers

import models._
import java.util.zip.ZipFile
import java.io.File
import scala.io.Source
import java.util.zip.ZipEntry

class ErrorReportFile(filename: String) {
  val configText = FileReader.getConfigFile(filename)
  val reportText = FileReader.getReport(filename)
//  val logText = FileReader.getAllLogs(filename)

  val errorReport: ErrorReport =
    {
      val uuidRegex = """net\.java\.sip\.communicator\.UUID=(\S*)""".r
      val phoneNumberRegex = """sip\.acc\d+\.USER_ID=(\d*)""".r
      val clientVersionRegex = """Client Version Number: (.*)\n""".r
      val osVersionRegex = """User OS: (.*)\n""".r
      val usernameRegex = """Username: (.*)\n""".r
      val logRegex = """(?s)(.*?)\n\n(.*?)\nClient Version Number:""".r
      val logRegexUncaughtExceptionVersion = """(?s)(.*?)(Uncaught exception.*)\n\nClient Version Number:""".r

      val logRegexToUse = if (reportText.contains("Uncaught exception")) logRegexUncaughtExceptionVersion else logRegex

      val uuid = uuidRegex.findFirstMatchIn(configText) match { case Some(aMatch) => aMatch.group(1); case None => "Couldn't find UUID" }
      val phoneNumber = phoneNumberRegex.findFirstMatchIn(configText) match { case Some(aMatch) => aMatch.group(1); case None => "Couldn't find phoneNumber" }
      val clientVersion= clientVersionRegex.findFirstMatchIn(reportText) match { case Some(aMatch) => aMatch.group(1); case None => "Couldn't find clientVersion" }
      val osVersion= osVersionRegex.findFirstMatchIn(reportText) match { case Some(aMatch) => aMatch.group(1); case None => "Couldn't find osVersion" }
      val username = usernameRegex.findFirstMatchIn(reportText) match { case Some(aMatch) => aMatch.group(1); case None => "Couldn't find userName" }
      val logText = logRegexToUse.findFirstMatchIn(reportText) match { case Some(aMatch) => aMatch.group(1); case None => "Couldn't find logText" }
      val logType = logRegexToUse.findFirstMatchIn(reportText) match { case Some(aMatch) => aMatch.group(2); case None => "Couldn't find logType" }

      val filenameonly = new File(filename).getName
      new ErrorReport(filenameonly, phoneNumber, clientVersion, osVersion, username, logType, logText)
    }
}

object FileReader {
  val reportRegexString = """report-\d{6}-\d{6}\.zip"""

  def readFile(filename: String, filter: String): List[String] =
    {
      import scala.collection.JavaConversions._

      val zf: ZipFile = new ZipFile(new File(filename))

      val result = (for (e: ZipEntry <- zf.entries if e.getName.matches(filter)) yield {
        println(e.getName)
        Source.fromInputStream(zf.getInputStream(e), "latin1").mkString //THis latin1 thing is a HACK
      }).toList
      zf.close()
      result
    }

  def getConfigFile(filename: String): String =
    {
      readFile(filename, "sip-communicator\\.properties").mkString.replaceAll("\r\n", "\n").replaceAll("\r", "\n")
    }

  def getAllLogs(filename: String): String =
    {
      readFile(filename, ".*[0123]$").mkString("\n").replaceAll("\r\n", "\n").replaceAll("\r", "\n")
    }

    def getReport(filename: String): String =
    {
      readFile(filename, "report.*\\.txt").mkString("\n").replaceAll("\r\n", "\n").replaceAll("\r", "\n")
    }
}