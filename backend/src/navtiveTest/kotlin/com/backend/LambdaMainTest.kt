package com.backend

import com.backend.apigateway.APIGatewayProxy
import com.backend.apigateway.APIGatewayProxy.ProxyRequestContext
import io.github.trueangle.knative.lambda.runtime.api.Context
import kotlinx.coroutines.test.runTest
import platform.posix.setenv
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LambdaMainTest {
    private lateinit var subject: LambdaMain
    private val context = Context(
        awsRequestId = "1",
        xrayTracingId = null,
        deadlineTimeInMs = 1L,
        invokedFunctionArn = "invokedFunctionArn",
        invokedFunctionName = "invokedFunctionName",
        invokedFunctionVersion = "invokedFunctionVersion",
        memoryLimitMb = 1,
        clientContext = null,
        cognitoIdentity = null
    )

    @BeforeTest
    fun setup() {
        subject = LambdaMain()
    }

    @Test
    fun handleSayHelloRequest() = runTest {
        assertEquals(
            "{\"message\":\"hi\"}",
            subject.handleRequest(
                input = createAPIGatewayProxyEvent("{\"name\":\"hi\"}", "SayHello"),
                context = context
            ).body
        )
    }

    @Test
    fun handleSayHelloErrors() = runTest {
        assertEquals(
            "{\"errorMessage\":\"Throwing 400 error\"}",
            subject.handleRequest(
                input = createAPIGatewayProxyEvent("{\"name\":\"400\"}", "SayHello"),
                context = context
            ).body
        )
    }

    @Test
    fun handleInfoRequest() = runTest {
        setenv("REGION", "value1", 1)
        setenv("USER_POOL_ID", "value2", 1)
        setenv("USER_POOL_WEB_CLIENT_ID", "value3", 1)
        assertEquals(
            "{\"region\":\"value1\",\"userPoolId\":\"value2\",\"userPoolWebClientId\":\"value3\",\"authenticationFlowType\":\"USER_PASSWORD_AUTH\"}",
            subject.handleRequest(
                input = createAPIGatewayProxyEvent("", "Info"),
                context = context
            ).body
        )
    }

    private fun createAPIGatewayProxyEvent(
        body: String,
        operationName: String
    ): APIGatewayProxy = APIGatewayProxy(
        version = null,
        resource = null,
        path = null,
        httpMethod = "GET",
        headers = null,
        multiValueHeaders = null,
        queryStringParameters = null,
        multiValueQueryStringParameters = null,
        pathParameters = null,
        stageVariables = null,
        requestContext = ProxyRequestContext(
            operationName = operationName,
            accountId = null,
            stage = null,
            resourceId = null,
            requestId = null,
            identity = null,
            resourcePath = null,
            httpMethod = "GET",
            apiId = null,
            path = null,
            authorizer = null,
            extendedRequestId = null,
            requestTime = null,
            requestTimeEpoch = 1L,
            domainName = null,
            domainPrefix = null,
            protocol = null,
            deploymentId = null
        ),
        body = body,
        isBase64Encoded = false
    )
}
