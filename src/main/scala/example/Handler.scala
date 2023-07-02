package example

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import io.opentelemetry.instrumentation.annotations.WithSpan

import java.time.Instant
import scala.jdk.CollectionConverters.CollectionHasAsScala

class Handler extends RequestHandler[SQSEvent, String] {
  println(Instant.now(), "setting up env")

  @WithSpan // hm this doesn't work?
  override def handleRequest(input: SQSEvent, context: Context): String = {
    val records = input.getRecords
    val logger = context.getLogger
    records.asScala.foreach(record => logger.log(record.getBody))
    logger.log("logging via logger")
    println(Instant.now(), "handle")
    println(Instant.now(), records.size())
    println(Instant.now(), context.getAwsRequestId)
    println(Instant.now(), context.getRemainingTimeInMillis)
    println(Instant.now(), context.getFunctionName)
    println(Instant.now(), context.getFunctionVersion)
    println(Instant.now(), context.getInvokedFunctionArn)
    println(Instant.now(), context.getMemoryLimitInMB)
    println(Instant.now(), context.getLogGroupName)
    println(Instant.now(), context.getLogStreamName)
    println(Instant.now(), System.getenv("_X_AMZN_TRACE_ID"))

    doWork()
    "It worked"
  }

  @WithSpan
  private def doWork() = {
    println("doing some work")
  }
}