package com.apollographql.java.client.network.http;

import com.apollographql.apollo.api.http.HttpRequest;
import org.jetbrains.annotations.NotNull;

public interface HttpInterceptor {
  void intercept(@NotNull HttpRequest request, @NotNull HttpInterceptorChain chain, @NotNull HttpCallback callback);
}
