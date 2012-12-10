package models
import play.api.db.DB

import java.security.MessageDigest
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import org.h2.jdbc.JdbcSQLException
import java.util.Date
import java.text.SimpleDateFormat

case class ErrorReport(filename: String,
                       dn: String,
                       appVersion: String,
                       osVersion: String,
                       username: String,
                       logType: String,
                       logText: String
                       )


object DatabaseAccess {
   val format = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss.SSS")

   def getDate(in:String):Date =
   {
    format.parse(in)
   }

  val errorReport = {
    get[String]("filename") ~
      get[String]("dn") ~
      get[String]("appVersion") ~
      get[String]("osVersion") ~
      get[String]("username") ~
      get[String]("logType") ~
      get[String]("logText")map {
        case filename ~ dn ~ appVersion ~ osVersion ~ username ~ logType ~ logText=> new ErrorReport(filename, dn, appVersion, osVersion, username, logType, logText)
      }
  }

  def persistErrorReport(report: ErrorReport) =
    {
        DB.withConnection { implicit c =>
          SQL("""INSERT INTO error_report (filename, dn, appVersion, osVersion, username, logType, logText)
         VALUES ({filename}, {dn}, {appVersion}, {osVersion}, {username}, {logType}, {logText})""").on(
          "filename" -> report.filename, "dn" -> report.dn, "appVersion" -> report.appVersion, "osVersion" -> report.osVersion, "username" -> report.username, "logType" -> report.logType, "logText" -> report.logText).executeUpdate()
        }
    }

    def getAllErrorReports(): List[ErrorReport] =
    {
      DB.withConnection {
        implicit c =>
          SQL("""SELECT * FROM error_report""").as(errorReport *)
      }
    }

        def dropAllData() =
    {
      DB.withConnection {
        implicit c =>
          SQL("""DELETE FROM error_report""").execute()
      }
    }

          def deleteReport(filename:String) =
    {
      DB.withConnection {
        implicit c =>
          SQL("""DELETE FROM error_report WHERE filename={filename}""").on("filename" -> filename).execute()
      }
    }

                    def getReport(filename:String):ErrorReport =
    {
      DB.withConnection {
        implicit c =>
          SQL("""SELECT * FROM error_report WHERE filename={filename}""").on("filename" -> filename).as(errorReport.single)
      }
    }


}


