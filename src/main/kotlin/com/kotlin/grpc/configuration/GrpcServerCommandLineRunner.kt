package com.kotlin.grpc.configuration

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class GrpcServerCommandLineRunner(
    private val grpcServerConfiguration: GrpcServerConfiguration
) :
    CommandLineRunner {

    override fun run(vararg args: String?) {
        grpcServerConfiguration.start()
        grpcServerConfiguration.block()
    }
}
