/**
 * Created by oinbar on 5/6/15.
 * Modified by fchang on 5/13/15.
 * 
 */

import scala.io.Source
import scalaj.http._
import java.io.File

object DataInserter extends App
{

  val urls = List(
    "https://www.dropbox.com/s/lud9yacy6ax1bj0/EBAY.TXT?dl=1",
    "https://www.dropbox.com/s/o6hfth0foqeupox/EMIF.TXT?dl=1",
    "https://www.dropbox.com/s/onxb29ofx0jrsxj/INTC.TXT?dl=1",
    "https://www.dropbox.com/s/w9ahvl1s6zkpeyr/MSFT.TXT?dl=1",
    "https://www.dropbox.com/s/6a9q0mna9u8afg8/NVDA.TXT?dl=1",
    "https://www.dropbox.com/s/v6k7wusqjsk53z2/YHOO.TXT?dl=1"
  )
  // modified from Stack Overflow article
  //http://stackoverflow.com/questions/3976616/how-to-find-nth-          //occurrence-of-character-in-a-string
  def nthOccurence(str: String, c: Char, n: Int):Int = { 
     var cnt = n
     var pos:Int = str.indexOf(c) 
     while( cnt > 0 && pos != -1 ){
        pos = str.indexOf(c, pos+1) 
        cnt = cnt - 1
     }   
     pos 
  }

  def downloadFile(url: String, destination: String)
  {
    try
    {
      val src = scala.io.Source.fromURL(url)
      val out = new java.io.FileWriter(destination)
      out.write(src.mkString)
      out.close
    }
    catch
    {
      case e: java.io.IOException => throw e
    }
  }


  val homedirectory = new File(".").getAbsolutePath()
  println(homedirectory)
  val nitem =  nthOccurence(homedirectory,'/', 3)
  println(nitem)
  urls.toParArray.foreach{ url=>
    val file = url.split('/')(5).split('?')(0)
    val dirPath = new File("src/main/resources/stockdata/").getAbsolutePath
    val dest = homedirectory.substring(0,nthOccurence(homedirectory,File.separator(0), 3))+  "/team-two/src/main/resources/stockdata" + "/" + file
    println(dest)
    //println(homedirectory.toVector.zipWithIndex.filter(_._1 == '\\').lastOption map {_._2} getOrElse(-1))
    downloadFile(url,dest)
    val header :: lines = Source.fromFile(dest).getLines().toList
    lines.foreach
    { line =>
      val Array(date, numeric_delivery_month, open, high, low, close, volume) = line.split(",")
      val endpoint = "http://localhost:8000/insert/" + file.split('.')(0) + "/" + close + "/" +  date
      val response: HttpResponse[String] = Http(endpoint)
        .timeout(connTimeoutMs = 10000, readTimeoutMs = 50000)
        .asString
    }
  }

}