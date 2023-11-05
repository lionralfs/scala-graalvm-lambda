package example

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import java.time.Instant;

class Handler extends RequestHandler[Object, String] {
  println(Instant.now(), "setting up env")

  override def handleRequest(input: Object, context: Context): String = {
    val logger = context.getLogger
    logger.log("logging via logger")
    println(Instant.now(), "handle")
    println(Instant.now(), input)
    println(Instant.now(), context.getAwsRequestId)
    println(Instant.now(), context.getRemainingTimeInMillis)
    println(Instant.now(), context.getFunctionName)
    println(Instant.now(), context.getFunctionVersion)
    println(Instant.now(), context.getInvokedFunctionArn)
    println(Instant.now(), context.getMemoryLimitInMB)
    println(Instant.now(), context.getLogGroupName)
    println(Instant.now(), context.getLogStreamName)
    println(Instant.now(), System.getenv("_X_AMZN_TRACE_ID"))
    "It worked"
  }
}