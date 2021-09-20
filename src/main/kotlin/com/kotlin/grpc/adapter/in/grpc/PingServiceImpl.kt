package com.kotlin.grpc.adapter.`in`.grpc

import com.grpc.PingRequest
import com.grpc.PingResponse
import com.grpc.PingServiceGrpcKt
import io.grpc.StatusException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class PingServiceImpl() : PingServiceGrpcKt.PingServiceCoroutineImplBase() {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(PingServiceImpl::class.java)
        private val random: Random = Random(12346L)
    }

    override suspend fun ping(request: PingRequest): PingResponse = kotlin.runCatching {
        log.info("Unary - Ping request received. Address: {}", request.address)

        return buildResponse(request)
    }.onFailure { e -> throw e.toStatusException() }.getOrThrow()

    private fun buildResponse(request: PingRequest) = PingResponse.newBuilder().apply {
        address = request.address
        time = random.nextInt(1000)
    }.build()

    override fun pingServerStream(request: PingRequest): Flow<PingResponse> = kotlin.runCatching {
        flow {
            log.info("ServerStream - Ping request received")
            for (i in 1..5) {
                emit(buildResponse(request))
                kotlinx.coroutines.delay(500)
            }
        }
    }.onFailure { e -> e.toStatusException() }.getOrThrow()
}

private fun Throwable.toStatusException(): StatusException =
    when (this) {
        is IllegalArgumentException ->
            io.grpc.Status.INVALID_ARGUMENT
                .withCause(this.cause)
                .withDescription(this.message)
                .asException()
        else ->
            io.grpc.Status.INTERNAL
                .withCause(this.cause)
                .withDescription(this.message)
                .asException()
    }
