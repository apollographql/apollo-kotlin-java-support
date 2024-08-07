package com.apollographql.java.client.network.http.internal;

import com.apollographql.apollo.api.http.HttpRequest;
import com.apollographql.java.client.network.http.HttpCallback;
import com.apollographql.java.client.network.http.HttpInterceptor;
import com.apollographql.java.client.network.http.HttpInterceptorChain;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultHttpInterceptorChain implements HttpInterceptorChain {
  private final List<HttpInterceptor> interceptors;
  private final int index;

  public DefaultHttpInterceptorChain(@NotNull List<HttpInterceptor> interceptors, int index) {
    this.interceptors = interceptors;
    this.index = index;
  }

  @Override
  public void proceed(@NotNull HttpRequest request, @NotNull HttpCallback callback) {
    interceptors.get(index).intercept(request, new DefaultHttpInterceptorChain(interceptors, index + 1), callback);
  }
}
