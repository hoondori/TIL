package exercise

import org.scalatest.{Matchers, FlatSpec}
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source
import scala.util.control.NonFatal
import scala.util.{Try,Success,Failure}

/**
  * Created by hoondori on 2016. 3. 21..
  */
class Ch4_Future extends FlatSpec with Matchers {

  // Starting future computations
  "Text 1" should "do" in {

    Future {
      println("the future is here")
    }
    println("the future is coming")
    Thread.sleep(1000)
  }

  // Starting future computations
  "Text 2" should "do" in {

    val buildFile: Future[Unit] = Future {
      val f = Source.fromFile("build.sbt")
      try f.getLines().mkString("\n") finally f.close()
    }
    println(s"started reading the build file asynchronosly")
    println(s"status ${buildFile.isCompleted}")
    Thread.sleep(250)
    println(s"status ${buildFile.isCompleted}")
    println(s"value ${buildFile.value}")
  }

  // Future callbacks
  "Text 3" should "do" in {

    def getUrlSpec(): Future[List[String]] = Future {
      val url = "http://www.w3.org/Addressing/URL/url-spec.txt"
      val f = Source.fromURL(url)
      try f.getLines.toList finally f.close()
    }
    val urlSpec: Future[List[String]] = getUrlSpec()

    def find(lines: List[String], keyword: String): String =
      lines.zipWithIndex collect {
        case (line, n) if line.contains(keyword) => (n, line)
      } mkString("\n")

    urlSpec foreach {
      case lines => println(find(lines, "telnet"))
    }
    println("callback registered, continuing with other work")
    Thread.sleep(2000)

    urlSpec foreach {
      case lines => println(find(lines, "password"))
    }
    println("callback registered, continuing with other work")
    Thread.sleep(2000)

  }

  // Futures and exceptions
  "Text 4" should "do" in {

    // register callback for failure in future
    val urlSpec: Future[String] = Future {
      val invalidUrl = "http://www.w3.org/non-existent-url-spec.txt"
      Source.fromURL(invalidUrl).mkString
    }
    urlSpec.failed foreach {
      case t => println(s"exception occurred - $t")
    }
    Thread.sleep(3000)


  }

  // Using the try type
  "Text 5" should "do" in {

    // synchronously, akin to collections

    def handleMessage(t: Try[String]) = t match {
      case Success(msg) => println(msg)
      case Failure(error) => println(s"unexpected failure - $error")
    }

    val threadName: Try[String] = Try( Thread.currentThread().getName)
    val someText: Try[String] = Try {"Try objects are synchronous"}
    val message: Try[String] = for {
      tn <- threadName
      st <- someText
    } yield s"Message $st was created on t = $tn"
    handleMessage(message)

  }

  // Fatal exceptions
  "Text 6" should "do" in {

    val f = Future { throw new InterruptedException } // fatal-error => cannot capture
    val g = Future { throw new IllegalArgumentException }
    f.failed foreach { case t => println(s"error - $t")}
    g.failed foreach { case t => println(s"error - $t")}

  }

  // Future combination
  "Text 7" should "do" in {

    val netiquetteUrl = "http://www.ietf.org/rfc/rfc1855.txt"
    val netiquette = Future { Source.fromURL(netiquetteUrl).mkString }
    val urlSpecUrl = "http://www.w3.org/Addressing/URL/url-spec.txt"
    val urlSpec = Future { Source.fromURL(urlSpecUrl).mkString }
    val answer = netiquette.flatMap { nettext =>
      urlSpec.map { urltext =>
        "Check this out: " + nettext + ". And check out: " + urltext
      }
    }
    answer foreach { case contents => println(contents) }

    Thread.sleep(2000)
  }

  // Future combination
  "Text 8" should "do" in {
    // recover

    val netiquetteUrl = "http://www.ietf.org/rfc/rfc1855.doc"
    val netiquette = Future { Source.fromURL(netiquetteUrl).mkString }
    val answer = netiquette recover {
      case e: java.io.FileNotFoundException =>
        "Dear secretary, thank you for your e-mail." +
          "You might be interested to know that ftp links " +
          "can also point to regular files we keep on our servers."
    }
    answer foreach { case contents => println(contents) }
    Thread.sleep(2000)

  }

  // Promises
  "Text 9" should "do" in {

    val p = Promise[String]
    val q = Promise[String]
    p.future foreach { case x => println(s"p succeeded with '$x'")}
    Thread.sleep(1000)
    p success "assigned"
    q failure new Exception("not kept")
    q.future.failed foreach { case t => println(s"q failed with $t") }
    Thread.sleep(1000)
  }

  // Promises
  "Text 10" should "do" in {

    // my own future implementation by promise
    def myFuture[T](b: => T): Future[T] = {
      val p = Promise[T]
      global.execute(new Runnable {
        def run() = try {
          println("this is my future impl")
          p.success(b)
        } catch {
          case NonFatal(e) => p.failure(e)
        }
      })
      p.future
    }
    val f = myFuture { "naa" + "na"*8 + " Katamari Damacy!" }
    f foreach { case text => println(text)}

  }

  // Future combination
  "Text 11" should "do" in {

  }

  // Future combination
  "Text 12" should "do" in {

  }
}
