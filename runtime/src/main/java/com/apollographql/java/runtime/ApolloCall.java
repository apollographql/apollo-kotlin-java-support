package com.apollographql.java.runtime;

import com.apollographql.apollo3.api.MutableExecutionOptions;
import com.apollographql.apollo3.api.Operation;
import org.jetbrains.annotations.NotNull;

public interface ApolloCall<D extends Operation.Data> extends MutableExecutionOptions<ApolloCall<D>> {
  /**
   * Schedules the request to be executed at some point in the future.
   *
   * @param callback Callback which will handle the response or a failure exception.
   */
  ApolloDisposable enqueue(@NotNull ApolloCallback<D> callback);
}