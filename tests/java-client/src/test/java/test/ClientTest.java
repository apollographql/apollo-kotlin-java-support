package test;

import com.apollographql.apollo.api.ApolloRequest;
import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.exception.ApolloHttpException;
import com.apollographql.java.client.ApolloCallback;
import com.apollographql.java.client.ApolloClient;
import com.apollographql.java.client.ApolloDisposable;
import com.apollographql.java.client.interceptor.ApolloInterceptor;
import com.apollographql.java.client.interceptor.ApolloInterceptorChain;
import com.apollographql.java.client.network.http.HttpInterceptor;
import com.apollographql.mockserver.MockRequestBase;
import com.apollographql.mockserver.MockResponse;
import com.apollographql.mockserver.MockServer;
import com.apollographql.mockserver.MockServerKt;
import com.google.common.truth.Truth;
import io.reactivex.rxjava3.annotations.NonNull;
import javatest.GetRandomQuery;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import scalar.GeoPointAdapter;
import scalars.CreateCatMutation;
import scalars.LocationQuery;

import java.util.Arrays;

import static test.Utils.*;

public class ClientTest {
  MockServer mockServer;
  ApolloClient apolloClient;
  private String mockServerUrl;

  @Before
  public void before() {
    mockServer = MockServerKt.MockServer();

    /*
      Because url doesn't suspend on the JVM, we can just use the return value
     */
    mockServerUrl = (String) mockServer.url(new Continuation<String>() {
      @NotNull
      @Override
      public CoroutineContext getContext() {
        return EmptyCoroutineContext.INSTANCE;
      }

      @Override
      public void resumeWith(@NotNull Object o) {
      }
    });

    apolloClient = new ApolloClient.Builder().serverUrl(mockServerUrl).build();
  }

  @Test
  public void success() {
    mockServer.enqueue(new MockResponse.Builder().body("{\"data\": {\"random\": 42}}").build());
    @NonNull ApolloResponse<GetRandomQuery.Data> queryResponse = blockingQuery(apolloClient, GetRandomQuery.builder().build());
    Truth.assertThat(queryResponse.dataOrThrow().random).isEqualTo(42);

    mockServer.enqueue(new MockResponse.Builder().body("{\"data\": {\"createAnimal\": {\"__typename\": \"Cat\", \"species\": \"cat\", \"habitat\": {\"temperature\": 10.5}}}}").build());
    @NonNull ApolloResponse<CreateCatMutation.Data> mutationResponse = blockingMutation(apolloClient, CreateCatMutation.builder().build());
    Truth.assertThat(mutationResponse.dataOrThrow().createAnimal.catFragment.species).isEqualTo("cat");
  }

  @Test
  public void graphqlError() {
    mockServer.enqueue(new MockResponse.Builder().body("{\n" +
        "  \"errors\": [\n" +
        "    {\n" +
        "      \"message\": \"The requested resource could not be found.\",\n" +
        "      \"path\": [\"user\"]\n" +
        "    }\n" +
        "  ]\n" +
        "}").build());
    @NonNull ApolloResponse<GetRandomQuery.Data> queryResponse = blockingQuery(apolloClient, GetRandomQuery.builder().build());
    Truth.assertThat(queryResponse.data).isNull();
    Truth.assertThat(queryResponse.exception).isNull();
    Truth.assertThat(queryResponse.errors.get(0).getMessage()).isEqualTo("The requested resource could not be found.");
  }

  @Test
  public void httpError() {
    mockServer.enqueue(new MockResponse.Builder().statusCode(500).build());
    @NonNull ApolloResponse<GetRandomQuery.Data> queryResponse = blockingQuery(apolloClient, GetRandomQuery.builder().build());
    Truth.assertThat(queryResponse.data).isNull();
    Truth.assertThat(queryResponse.errors).isNull();
    Truth.assertThat(queryResponse.exception).isInstanceOf(ApolloHttpException.class);
  }

  @Test
  public void apolloInterceptors() {
    ApolloInterceptor interceptor1 = new ApolloInterceptor() {
      @Override
      public <D extends Operation.Data> void intercept(@NotNull ApolloRequest<D> request, @NotNull ApolloInterceptorChain chain, @NotNull ApolloCallback<D> callback) {
        request = request.newBuilder().addHttpHeader("interceptor1", "true").build();
        chain.proceed(request, callback);
      }
    };
    ApolloInterceptor interceptor2 = new ApolloInterceptor() {
      @Override
      public <D extends Operation.Data> void intercept(@NotNull ApolloRequest<D> request, @NotNull ApolloInterceptorChain chain, @NotNull ApolloCallback<D> callback) {
        request = request.newBuilder().addHttpHeader("interceptor2", "true").build();
        chain.proceed(request, callback);
      }
    };
    ApolloInterceptor interceptor3 = new ApolloInterceptor() {
      @Override
      public <D extends Operation.Data> void intercept(@NotNull ApolloRequest<D> request, @NotNull ApolloInterceptorChain chain, @NotNull ApolloCallback<D> callback) {
        request = request.newBuilder().addHttpHeader("interceptor3", "true").build();
        chain.proceed(request, callback);
      }
    };

    apolloClient = new ApolloClient.Builder()
        .serverUrl(mockServerUrl)
        .addInterceptor(interceptor1)
        .addInterceptors(Arrays.asList(interceptor2, interceptor3))
        .build();

    mockServer.enqueue(new MockResponse.Builder().body("{\"data\": {\"random\": 42}}").build());
    blockingQuery(apolloClient, GetRandomQuery.builder().build());
    MockRequestBase mockRequest = mockServer.takeRequest();
    Truth.assertThat(mockRequest.getHeaders().get("interceptor1")).isEqualTo("true");
    Truth.assertThat(mockRequest.getHeaders().get("interceptor2")).isEqualTo("true");
    Truth.assertThat(mockRequest.getHeaders().get("interceptor3")).isEqualTo("true");
  }

  @Test
  public void httpInterceptors() {
    HttpInterceptor interceptor1 = (request, chain, callback) -> {
      request = request.newBuilder().addHeader("interceptor1", "true").build();
      chain.proceed(request, callback);
    };

    HttpInterceptor interceptor2 = (request, chain, callback) -> {
      request = request.newBuilder().addHeader("interceptor2", "true").build();
      chain.proceed(request, callback);
    };

    HttpInterceptor interceptor3 = (request, chain, callback) -> {
      request = request.newBuilder().addHeader("interceptor3", "true").build();
      chain.proceed(request, callback);
    };

    apolloClient = new ApolloClient.Builder()
        .serverUrl(mockServerUrl)
        .addHttpInterceptor(interceptor1)
        .addHttpInterceptors(Arrays.asList(interceptor2, interceptor3))
        .build();

    mockServer.enqueue(new MockResponse.Builder().body("{\"data\": {\"random\": 42}}").build());
    blockingQuery(apolloClient, GetRandomQuery.builder().build());
    MockRequestBase mockRequest = mockServer.takeRequest();
    Truth.assertThat(mockRequest.getHeaders().get("interceptor1")).isEqualTo("true");
    Truth.assertThat(mockRequest.getHeaders().get("interceptor2")).isEqualTo("true");
    Truth.assertThat(mockRequest.getHeaders().get("interceptor3")).isEqualTo("true");
  }

  @Test
  public void scalarAdapters() {
    apolloClient = new ApolloClient.Builder()
        .serverUrl(mockServerUrl)
        .addCustomScalarAdapter(scalars.type.GeoPoint.type, new GeoPointAdapter())
        .build();

    mockServer.enqueue(new MockResponse.Builder().body("{\"data\": {\"location\": {\"latitude\": 10.5, \"longitude\": 20.5}}}").build());
    ApolloResponse<LocationQuery.Data> queryResponse = blockingQuery(apolloClient, LocationQuery.builder().build());
    Truth.assertThat(queryResponse.dataOrThrow().location.latitude).isEqualTo(10.5);
    Truth.assertThat(queryResponse.dataOrThrow().location.longitude).isEqualTo(20.5);
  }

  @Test
  public void cancellation() {
    mockServer.enqueue(
        new MockResponse.Builder()
            .body("{\"data\": {\"random\": 42}}")
            .delayMillis(500)
            .build()
    );

    final Object[] received = new Object[1];

    ApolloDisposable disposable = apolloClient.query(GetRandomQuery.builder().build()).enqueue(new ApolloCallback<GetRandomQuery.Data>() {
      @Override
      public void onResponse(@NotNull ApolloResponse<GetRandomQuery.Data> response) {
        received[0] = response;
      }
    });

    sleep(100);

    disposable.dispose();

    sleep(1000);

    /*
     * Cancellation do not emit anything
     */
    Truth.assertThat(received[0]).isEqualTo(null);
  }
}
