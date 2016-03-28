package exercise

import java.util.TimerTask

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
    println(s"started reading the build file asynchronously")
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

  // Converting callback-based APIs
  "Text 11" should "do" in {

    import org.apache.commons.io.monitor._
    import java.io.File

    def fileCreated(directory: String): Future[String] = {
      val p = Promise[String]
      val fileMonitor = new FileAlterationMonitor(1000)
      val observer = new FileAlterationObserver(directory)
      val listener = new FileAlterationListenerAdaptor {
        override def onFileCreate(file: File): Unit =  {
          try p.trySuccess(file.getName) finally fileMonitor.stop()
        }
      }
      observer.addListener(listener)
      fileMonitor.addObserver(observer)
      fileMonitor.start()
      p.future
    }

    fileCreated(".") foreach {
      case filename => println(s"Detected new file '$filename'")
    }



  }

  // Converting callback-based APIs
  "Text 12" should "do" in {

    // timeout
    import java.util.Timer
    val timer = new Timer(true)

    def timeout(t: Long): Future[Unit] = {
      val p = Promise[Unit]
      timer.schedule( new TimerTask {
        def run() = {
          p success ()
          timer.cancel()
        }
      }, t)
      p.future
    }

    timeout(1000) foreach { case _ => println("Timed out")}
    Thread.sleep(2000)
  }

  // Extending the future API
  "Text 13" should "do" in {
    // with implicit class

    implicit class FutureOps[T](val self: Future[T]) {

      // The resulting future is completed with the value of one of the input futures depending on the execution schedule.
      def or(that: Future[T]): Future[T] = {
        val p = Promise[T]
        self onComplete { case x => p tryComplete x }
        that onComplete { case y => p tryComplete y }
        p.future
      }
    }

    Future {println("A")} or Future {println("B")}
    Thread.sleep(2000)
  }

  // Cancellation of asynchronous computation
  "Text 14" should "do" in {
    type Cancellable[T] = (Promise[Unit],Future[T])

    def cancellable[T](b: Future[Unit] => T): Cancellable[T] = {
      val cancel = Promise[Unit]
      val f = Future {
        val r: T = b(cancel.future)
        if(!cancel.tryFailure(new Exception))
          throw new CancellationException
        r
      }
      (cancel,f)
    }

    val (cancel, value) = cancellable { cancel =>
      var i = 0
      while(i < 5) {
        if(cancel.isCompleted) {
          println("cancel is ordered")
          throw new CancellationException("wook")
        }
        Thread.sleep(500)
        println(s"$i: working")
        i += 1
      }
      "resulting value"
    }

    Thread.sleep(1500)
    cancel trySuccess()
    println("computation cancelled!!")
    Thread.sleep(2000)
    println(value.value)



  }

  // Awaiting futures
  "Text 15" should "do" in {

    // create starvation...

    import scala.concurrent.duration._
    val startTime = System.nanoTime
    val futures = for (_ <- 0 until 16) yield Future {
      Thread.sleep(1000)
      val endTime = System.nanoTime
      println(s"Elpased time = ${(endTime - startTime) / 1000000} ms")
    }
    for (f <- futures) Await.ready(f, Duration.Inf)
    val endTime = System.nanoTime
    println(s"Total time = ${(endTime - startTime) / 1000000} ms")
    println(s"Total CPUs = ${Runtime.getRuntime.availableProcessors}")
  }

  // Scala async library
  "Text 16" should "do" in {

    import scala.async.Async.{async,await}

    def delay(n: Int): Future[Unit] = async {
      blocking { Thread.sleep(n*1000) }
    }

    def countdown(n: Int)(f: Int => Unit): Future[Unit] = async {
      var i = n
      while (i>0) {
        f(i)
        await { delay(1) }
        i -= 1
      }
    }

    countdown(3) { n => println(s"T-minus $n seconds") } foreach {
      case _ => println(s"This program is over!")
    }
    Thread.sleep(4000)

  }

  // scalaz-concurrent
  "Text 17" should "do" in {

    // pull semantics
    import scalaz.concurrent._
    val tombola = Future {
      scala.util.Random.shuffle((0 until 10000).toVector)
    }
    tombola.runAsync { numbers =>
      println(s"And the winner is ${numbers.head}")
    }
    tombola.runAsync { numbers =>
      println(s".....ahm, winner is ${numbers.head}")
    }

    // push semantics
    val tombola2 = Future {
      scala.util.Random.shuffle((0 until 10000).toVector)
    } start

    tombola2.runAsync { numbers =>
        println(s"And the winner is ${numbers.head}")
      }
    tombola2.runAsync { numbers =>
      println(s".....ahm, winner is ${numbers.head}")
    }

  }

  "Exercise 4.1" should "do" in {

    /*Implement a command-line program that asks the user to input a URL of
    some website, and displays the HTML of that website. Between the time that
      the user hits ENTER and the time that the HTML is retrieved, the program
      should repetitively print a . to the standard output every 50 milliseconds,
    with a two seconds timeout. Use only futures and promises, and avoid the
    synchronization primitives from the previous chapters. You may reuse the
      timeout method defined in this chapter*/

    
  }
}
