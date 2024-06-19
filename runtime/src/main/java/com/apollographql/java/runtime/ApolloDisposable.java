package com.apollographql.java.runtime;

public interface ApolloDisposable {
  void dispose();

  boolean isDisposed();

  void addListener(Listener listener);

  void removeListener(Listener listener);

  interface Listener {
    void onDisposed();
  }
}
