import controllers.Application

object test {
  val myFile = new java.io.File(Application.destination)
                                                  //> myFile  : java.io.File = \mnt\test
                                                  
  val allFileNames = myFile.list()                //> allFileNames  : Array[java.lang.String] = Array(##2, #1 A bug name, #5 a bug
                                                  //|  name with a 123)
               	
               	
  val justBugs = allFileNames filter {_.matches("^#\\d+ .*$")}
                                                  //> justBugs  : Array[java.lang.String] = Array(#1 A bug name, #5 a bug name wit
                                                  //| h a 123)
                                                  //
                                                  //
  //val uuid = uuidRegex.findFirstMatchIn(configText) match { case Some(aMatch) => aMatch.group(1); case None => "Couldn't find UUID" }
  
  val bugIDs = for (aBug <- justBugs if aBug.matches("^#\\d+ .*$")) yield
  {
  val res = """^#(\d+)""".r.findFirstMatchIn(aBug)
  res match  { case Some(aMatch) => Integer.parseInt(aMatch.group(1));
  case None => 0}
  }                                               //> bugIDs  : Array[Int] = Array(1, 5)
  
  bugIDs.max                                      //> res0: Int = 5
}