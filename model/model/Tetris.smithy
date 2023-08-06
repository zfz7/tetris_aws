$version: "2.0"
namespace com.tetris

use aws.api#service
use aws.protocols#restJson1

@title("A Sample Hello World API")
@service(
    sdkId: "Tetris"
    arnNamespace: "execute-api"
)
// Define a service-level API Gateway integration -- this can be overridden on individual methods
@aws.apigateway#integration(
    type: "aws_proxy",
    httpMethod: "POST",
    uri: "arn:${AWS::Partition}:apigateway:${AWS::Region}:lambda:path/2015-03-31/functions/${LambdaFunction.Arn}/invocations",
    credentials: "${APIGatewayExecutionRole.Arn}"
)
@restJson1
service Tetris {
    version: "1.0"
    operations: [SayHello]
}

@readonly
@http(method: "GET", uri: "/hello")
operation SayHello {
    input := {
        @httpQuery("name")
        @required
        name: String
    }
    output := {
        @required
        message: String
    }
    errors: [ApiError]
}

@error("client")
@httpError(400)
structure ApiError {
    @required
    errorMessage: String
}