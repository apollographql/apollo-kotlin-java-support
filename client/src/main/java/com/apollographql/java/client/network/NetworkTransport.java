package com.apollographql.java.client.network;

import com.apollographql.apollo3.api.ApolloRequest;
import com.apollographql.apollo3.api.Operation;
import com.apollographql.java.client.ApolloCallback;
import com.apollographql.java.client.ApolloDisposable;
import org.jetbrains.annotations.NotNull;

public interface NetworkTransport {
  <D extends Operation.Data> void execute(@NotNull ApolloRequest<D> request, @NotNull ApolloCallback<D> callback, ApolloDisposable disposable);

  void dispose();
}
