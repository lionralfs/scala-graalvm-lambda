package example

import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}

import java.net.URI
import java.net.http.HttpClient.Version
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import java.time.{Duration, Instant}

object Bootstrap {
  private val LAMBDA_VERSION_DATE = "2018-06-01"
  private lazy val runtimeApi = System.getenv("AWS_LAMBDA_RUNTIME_API")
  private lazy val logGroupName = System.getenv("AWS_LAMBDA_LOG_GROUP_NAME")
  private lazy val logStreamName = System.getenv("AWS_LAMBDA_LOG_STREAM_NAME")
  private lazy val functionName = System.getenv("AWS_LAMBDA_FUNCTION_NAME")
  private lazy val memorySize = System.getenv("AWS_LAMBDA_FUNCTION_MEMORY_SIZE")
  private lazy val functionVersion = System.getenv("AWS_LAMBDA_FUNCTION_VERSION")
  private lazy val handlerName = System.getenv("_HANDLER")
  private lazy val httpClient = HttpClient.newBuilder
    .version(Version.HTTP_2)
    .connectTimeout(Duration.ofSeconds(10))
    .build

  def main(args: Array[String]): Unit = {
    println(s"[${Instant.now()}] bootstrapping (code)")
    println(s"[${Instant.now()}] args: ${args.mkString("\n")}")

    // TODO: handle `package.Class` format (https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html)
    val List(packageAndClass, handlerMethodName) = handlerName.split("::").toList

    val handlerClass = Class.forName(packageAndClass)
    val handlerMethod = handlerClass.getMethod(handlerMethodName, classOf[String], classOf[Context])
    val handler = handlerClass.getConstructor().newInstance()

    while (true) {
      val invocationResponse = getNextInvocation
      val headers = invocationResponse.headers
      val requestId = headers.firstValue("Lambda-Runtime-Aws-Request-Id").orElseThrow
      val deadlineMs = headers.firstValueAsLong("Lambda-Runtime-Deadline-Ms").orElseThrow
      val functionArn = headers.firstValue("Lambda-Runtime-Invoked-Function-Arn").orElseThrow
      headers.firstValue("Lambda-Runtime-Trace-Id").ifPresent(traceId => System.setProperty("_X_AMZN_TRACE_ID", traceId))
      //      val clientContext = headers.firstValue("Lambda-Runtime-Client-Context").orElseThrow
      //      val cognitoIdentity = headers.firstValue("Lambda-Runtime-Cognito-Identity").orElseThrow
      //      println("clientContext", clientContext)
      //      println("cognitoIdentity", cognitoIdentity)
      val logger = new Logger(requestId)
      val ctx = new LambdaContext(
        requestId = requestId,
        functionName = functionName,
        functionArn = functionArn,
        deadlineMs = deadlineMs,
        logGroupName = logGroupName,
        logStreamName = logStreamName,
        memorySize = memorySize.toInt,
        functionVersion = functionVersion,
        logger = logger
      )
      val response = handlerMethod.invoke(handler, invocationResponse.body, ctx).asInstanceOf[String]
      sendResponse(requestId, response)
    }
  }

  private def getNextInvocation = {
    val request = HttpRequest.newBuilder
      .GET()
      .uri(URI.create(s"http://$runtimeApi/$LAMBDA_VERSION_DATE/runtime/invocation/next"))
      .build
    val response = httpClient.send(request, BodyHandlers.ofString)
    response
  }

  private def sendResponse(requestId: String, body: String) = {
    val request = HttpRequest.newBuilder
      .POST(BodyPublishers.ofString(body))
      .uri(URI.create(s"http://$runtimeApi/$LAMBDA_VERSION_DATE/runtime/invocation/$requestId/response"))
      .build

    httpClient.send(request, BodyHandlers.ofString)
  }
}
