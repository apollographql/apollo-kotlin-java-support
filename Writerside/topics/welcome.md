# Welcome


This article describes how to use [Apollo Kotlin](https://github.com/apollographql/apollo-kotlin) in Java projects.

## Using the Java codegen

Apollo Kotlin generates Kotlin code by default, but you can configure it to generate Java code instead:

```kotlin title="build.gradle[.kts]"
apollo {
  service("service") {
    generateKotlinModels.set(false)
  }
}
```

## The Java client

The default runtime for Apollo Kotlin, `com.apollographql.apollo:apollo-runtime`, exposes a client with a coroutines / Flow-based API that isn't well suited to be consumed from Java.
That is why a specific client, `com.apollographql.java:client` is available to use Apollo Kotlin from Java. To use it, add a dependency on this client instead of the default runtime:

```kotlin title="build.gradle[.kts]"
dependencies {
  // ...

  // Use the Java client instead of apollo-runtime
  implementation("com.apollographql.java:client:0.0.2")
}
```
Note that the Java runtime currently doesn't support the HTTP or normalized caches.

The Java runtime has a callbacks based API. This snippet demonstrates initializing an `ApolloClient` and executing a query in Java:

```java
import com.apollographql.java.client.ApolloClient;
// (...)

// Create and configure an ApolloClient
ApolloClient client = ApolloClient.Builder builder = new ApolloClient.Builder()
  .serverUrl("https://example.com/graphql")
  .build();

// Call enqueue() to execute a query asynchronously
apolloClient.query(new MyQuery()).enqueue(response -> {
  if (response.data != null) {
    // Handle (potentially partial) data
    System.out.println(response.data);
  } else {
    // Something wrong happened
    if (response.exception != null) {
      // Handle non-GraphQL errors, e.g. network issues
      response.exception.printStackTrace();
    } else {
      // Handle GraphQL errors in response.errors
      System.out.println(response.getErrors().get(0));
    }
  }
});
```

### Cancelling requests

`euqueue` returns an `ApolloDisposable` that can be used to cancel the request:

```java
ApolloDisposable disposable = apolloClient.subscription(new MySubscription()).enqueue(response -> ...)
// ...
disposable.dispose();

```

### Subscriptions

Please refer to the [subscriptions documentation](https://www.apollographql.com/docs/kotlin/v4/essentials/subscriptions) for more information about subscriptions in general, and the available protocols.

When executing subscriptions with the Java runtime, the callback passed to `enqueue()` can be called several times:

```java
ApolloClient apolloClient = new ApolloClient.Builder()
  .serverUrl("https://example.com/graphql")
  // Configure a protocol factory
  .wsProtocolFactory(new ApolloWsProtocol.Factory())
  .build();

// Execute the subscription
ApolloDisposable disposable = apolloClient.subscription(new MySubscription()).enqueue(response -> {
  System.out.println(response.dataOrThrow());
});

// Observe the disposable to know when the subscription is terminated
disposable.addListener(() -> {
  // Will be called when an operation terminates (either successfully or due to an error)
});

```
### Interceptors

[Like the Kotlin runtime](https://www.apollographql.com/docs/kotlin/v4/advanced/interceptors-http), the Java runtime supports interceptors.

- HTTP interceptors (`HttpInterceptor`) can be used to add headers to requests (e.g. for authentication), log requests and responses, etc.
- GraphQL interceptors (`ApolloInterceptor`) can be used to modify GraphQL requests and responses, implement retry logic, etc.

```java
// An HTTP interceptor that adds a custom header to each request
HttpInterceptor httpInterceptor = (request, chain, callback) -> {
  request = request.newBuilder().addHeader("my-header", "true").build();
  chain.proceed(request, callback);
};

// A GraphQL interceptor that logs the name of each operation before executing it
ApolloInterceptor apolloInterceptor = new ApolloInterceptor() {
  @Override
  public <D extends Operation.Data> void intercept(@NotNull ApolloRequest<D> request, @NotNull ApolloInterceptorChain chain, @NotNull ApolloCallback<D> callback) {
    System.out.println("Executing operation: " + request.getOperation().name());
    chain.proceed(request, callback);
  }
};

// Configure the interceptors when building the ApolloClient
apolloClient = new ApolloClient.Builder()
  .serverUrl(...)
  .addHttpInterceptor(httpInterceptor)
  .addInterceptor(apolloInterceptor)
  .build();
```

If you already have implemented OkHttp interceptors, you can also use them by passing your `OkHttpClient` instance to the `ApolloClient.Builder`:

```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
  .okHttpClient(myOkHttpClient)
  .build();
```

## RxJava extensions

If your project uses RxJava, you can use the RxJava extensions with the Java runtime.

To do so, add the `com.apollographql.java:rx2` or `com.apollographql.java:rx3` dependency to your project:

```kotlin title="build.gradle[.kts]"
dependencies {
  // ...

  // For RxJava 2
  implementation("com.apollographql.java:rx2:0.0.2")

  // For RxJava 3
  implementation("com.apollographql.java:rx3:0.0.2")
}
```
Then use the `Rx2Apollo` or `Rx3Apollo` classes to execute GraphQL operations and get RxJava observables:

```java
import com.apollographql.java.rx3.Rx3Apollo;

// (...)

// Query
ApolloCall<MyQuery.Data> queryCall = client.query(new MyQuery());
Single<ApolloResponse<MyQuery.Data>> queryResponse = Rx3Apollo.single(queryCall);
queryResponse.subscribe( /* ... */ );

// Mutation
ApolloCall<MyMutation.Data> mutationCall = client.mutation(new MyMutation("my-parameter"));
Single<ApolloResponse<MyMutation.Data>> mutationResponse = Rx3Apollo.single(mutationCall);
mutationResponse.subscribe( /* ... */ );

// Subscription
ApolloCall<MySubscription.Data> subscriptionCall = client.subscription(new MySubscription());
Flowable<ApolloResponse<MySubscription.Data>> subscriptionResponse = Rx3Apollo.flowable(subscriptionCall);
subscriptionResponse.subscribe( /* ... */ );
```
