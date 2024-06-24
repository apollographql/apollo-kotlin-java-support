package com.apollographql.apollo.sample.server.graphql

import com.apollographql.apollo.sample.server.CurrentWebSocket
import com.apollographql.apollo3.api.ExecutionContext
import com.apollographql.execution.annotation.GraphQLSubscriptionRoot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.http4k.websocket.WsStatus

@GraphQLSubscriptionRoot
class SubscriptionRoot(private val tag: String) {
  fun count(to: Int, intervalMillis: Int): Flow<Int> = flow {
    repeat(to) {
      emit(it)
      if (intervalMillis > 0) {
        delay(intervalMillis.toLong())
      }
    }
  }

  fun operationError(): Flow<String> = flow {
    throw Exception("Woops")
  }

  fun graphqlAccessError(after: Int): Flow<Int?> = flow {
    repeat(after) {
      emit(it)
    }

    error("Woops")
  }

  fun closeWebSocket(executionContext: ExecutionContext): Flow<String> = flow {
    executionContext[CurrentWebSocket]!!.ws.close(WsStatus(1011, "closed"))

    emit("closed")
  }
}

class State(
  val tag: String,
  val subscriptionId: String,
)
