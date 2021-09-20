package com.kotlin.grpc.configuration

import com.grpc.PingServiceGrpcKt
import io.grpc.Server
import io.grpc.ServerBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GrpcServerConfiguration(
    private val pingServiceCoroutineImplBase: PingServiceGrpcKt.PingServiceCoroutineImplBase
) {
    private lateinit var server: Server

    companion object {
        private val log = LoggerFactory.getLogger(GrpcServerConfiguration::class.java)
        private const val serverPort = 50051
    }

    fun start() {
        log.info("Starting gRPC server on port {}", serverPort)
        server = ServerBuilder
            .forPort(serverPort)
            .addService(pingServiceCoroutineImplBase)
            .build()
            .start()
        log.info("gRPC server started.")

        Runtime.getRuntime().addShutdownHook(
            Thread {
                log.info("Shutting down gRPC server.")
                stop()
                log.info("gRPC shut down")
            }
        )
    }

    fun block() {
        server?.awaitTermination()
    }

    private fun stop() {
        server?.shutdown()
    }
}
