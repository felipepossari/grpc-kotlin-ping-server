package com.kotlin.grpc.adapter.`in`.web

import com.grpc.PingRequest
import com.kotlin.grpc.adapter.`in`.web.request.PingWebRequest
import com.kotlin.grpc.adapter.`in`.web.response.PingWebResponse
import com.kotlin.grpc.configuration.GrpcClientConfiguration
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController(
    private val grpcClientConfiguration: GrpcClientConfiguration
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(PingController::class.java)
    }

    @PostMapping("/ping-unary")
    fun pingUnary(@RequestBody request: PingWebRequest): ResponseEntity<PingWebResponse> {
        LOG.info("Pinging address {}", request.address)
        val grpcRequest = PingRequest.newBuilder().apply {
            address = request.address
        }.build()
        val response = grpcClientConfiguration.stub.ping(grpcRequest)
        return ResponseEntity.ok(PingWebResponse(response.address, response.time))
    }

    @PostMapping("/ping-server-stream")
    fun pingServerStream(@RequestBody request: PingWebRequest): ResponseEntity<PingWebResponse> {
        val grpcRequest = PingRequest.newBuilder().apply {
            address = request.address
        }.build()

        grpcClientConfiguration.stub.pingServerStream(grpcRequest).forEachRemaining {
            LOG.info("Ping response: {}:{}", it.address, it.time)
        }

        return ResponseEntity.ok().build()
    }
}