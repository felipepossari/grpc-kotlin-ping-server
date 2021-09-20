package com.kotlin.grpc.configuration

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class GrpcClientCommandLineRunner(
    private val grpcClientConfiguration: GrpcClientConfiguration
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        grpcClientConfiguration.start()

        Runtime.getRuntime().addShutdownHook(
            Thread {
                grpcClientConfiguration.shutdown()
            }
        )
    }
}
