package example

import com.amazonaws.services.lambda.runtime.{ClientContext, CognitoIdentity, Context, LambdaLogger}

private[example] class LambdaContext(
                                      private val requestId: String,
                                      private val functionName: String,
                                      private val functionArn: String,
                                      private val deadlineMs: Long,
                                      private val logGroupName: String,
                                      private val logStreamName: String,
                                      private val memorySize: Int,
                                      private val functionVersion: String,
                                      private val logger: LambdaLogger,
                                    ) extends Context {
  override def getAwsRequestId: String = requestId

  override def getLogGroupName: String = logGroupName

  override def getLogStreamName: String = logStreamName

  override def getFunctionName: String = functionName

  override def getFunctionVersion: String = functionVersion

  override def getInvokedFunctionArn: String = functionArn

  override def getIdentity: CognitoIdentity = ???

  override def getClientContext: ClientContext = ???

  override def getRemainingTimeInMillis: Int = (deadlineMs - System.currentTimeMillis()).toInt

  override def getMemoryLimitInMB: Int = memorySize

  override def getLogger: LambdaLogger = logger
}
