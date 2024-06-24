package com.apollographql.apollo.sample.server.graphql

import com.apollographql.execution.annotation.GraphQLQueryRoot


@GraphQLQueryRoot
class QueryRoot {
  fun random(): Int = 42
  fun zero(): Int = 0
}


