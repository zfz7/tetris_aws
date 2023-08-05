package com.backend

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

// Lambda handler:
// com.backend.LambdaMain
class LambdaMain : RequestHandler<Map<String, Any>, String> {
    override fun handleRequest(input: Map<String, Any>, context: Context?): String {
        context?.logger?.log("input$input $context")
        return "hiII";
    }
}

