$version: "2"
namespace com.tetris

use aws.protocols#restJson1

@title("A Sample Hello World API")

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
structure ApiError {
    @required
    errorMessage: String
}