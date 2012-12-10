package controllers

import java.io.File

object BugReader {
  def maxBugID: Int =
    {
      val bugIDs = for (aBug <- new File(Application.destination).list if aBug.matches("^#\\d+ .*$")) yield {
        val res = """^#(\d+)""".r.findFirstMatchIn(aBug)
        res match {
          case Some(aMatch) => Integer.parseInt(aMatch.group(1));
          case None => 0
        }
      }

      bugIDs.max
    }
}