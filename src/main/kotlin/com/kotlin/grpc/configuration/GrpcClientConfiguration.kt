package com.kotlin.grpc.configuration

import com.grpc.PingServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class GrpcClientConfiguration {

    companion object {
        private val log = LoggerFactory.getLogger(GrpcServerConfiguration::class.java)
        private const val HOST = "127.0.0.1"
        private const val PORT = 50051
    }

    private lateinit var managedChannel: ManagedChannel
    lateinit var stub: PingServiceGrpc.PingServiceBlockingStub

    fun start() {
        managedChannel = ManagedChannelBuilder.forAddress(HOST, PORT)
            .usePlaintext()
            .build()
        stub = PingServiceGrpc.newBlockingStub(managedChannel)
        log.info("gRPC client started. Address: {}:{}", HOST, PORT)
    }

    fun shutdown() {
        managedChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS)
        log.info("gRPC client shut down successfully")
    }
}
