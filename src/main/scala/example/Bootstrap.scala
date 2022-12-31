package example

import java.net.URI
import java.net.http.{HttpClient, HttpRequest}
import java.net.http.HttpClient.Version
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.time.{Duration, Instant}
import scala.jdk.CollectionConverters.MapHasAsScala

object Bootstrap {
  private val LAMBDA_VERSION_DATE = "2018-06-01"
  private val httpClient = HttpClient.newBuilder
    .version(Version.HTTP_1_1)
    .connectTimeout(Duration.ofSeconds(10))
    .build

  def main(args: Array[String]): Unit = {
    println(Instant.now(), "bootstrapping (code)")
    println(Instant.now(), args.mkString("\n"))

    val runtimeApi = System.getenv("AWS_LAMBDA_RUNTIME_API")
    //    val taskRoot = System.getenv("LAMBDA_TASK_ROOT")
    //    val handlerName = System.getenv("_HANDLER")

    val handler = new Handler

    while (true) {
      val (requestId, requestBody) = getNextInvocation(runtimeApi)
      val response = handler.handle(requestBody)
      sendResponse(runtimeApi, requestId, response)
    }
  }

  private def getNextInvocation(runtimeApi: String) = {
    val request = HttpRequest.newBuilder
      .GET()
      .uri(URI.create(s"http://$runtimeApi/$LAMBDA_VERSION_DATE/runtime/invocation/next"))
      .build
    val response = httpClient.send(request, BodyHandlers.ofString)

    println(Instant.now(), response.headers.map.asScala)
    val requestId = response.headers.firstValue("Lambda-Runtime-Aws-Request-Id").orElseThrow
    val body = response.body
    (requestId, body)
  }

  private def sendResponse(runtimeApi: String, requestId: String, body: String) = {
    val request = HttpRequest.newBuilder
      .POST(BodyPublishers.ofString(body))
      .uri(URI.create(s"http://$runtimeApi/$LAMBDA_VERSION_DATE/runtime/invocation/$requestId/response"))
      .build

    httpClient.send(request, BodyHandlers.ofString)
  }
}
