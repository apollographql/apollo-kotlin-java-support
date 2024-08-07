import com.apollographql.apollo.annotations.ApolloExperimental

plugins {
  id("java")
  id("com.apollographql.apollo")
}

dependencies {
  implementation("com.apollographql.java:client")
  implementation("com.apollographql.java:rx3")
  testImplementation(libs.junit)
  testImplementation(libs.truth)
  testImplementation(project(":sample-server"))
  testImplementation(libs.apollo.mockserver)
}

apollo {
  service("main") {
    schemaFiles.from(file("../sample-server/graphql/schema.graphqls"))
    srcDir("src/main/graphql/main")
    packageName.set("javatest")
    @OptIn(ApolloExperimental::class)
    generateModelBuilders.set(true)
  }

  service("scalars") {
    srcDir("src/main/graphql/scalars")
    mapScalarToJavaString("LanguageCode")
    mapScalarToJavaObject("Json")
    mapScalarToJavaLong("Long")
    mapScalar("GeoPoint", "scalar.GeoPoint")
    packageName.set("scalars")
    @OptIn(ApolloExperimental::class)
    generateModelBuilders.set(true)
  }

  service("graphql-ws") {
    srcDir("src/main/graphql/graphql-ws")
    packageName.set("graphqlws")
    @OptIn(ApolloExperimental::class)
    generateModelBuilders.set(true)
  }

  service("appsync") {
    srcDir("src/main/graphql/appsync")
    packageName.set("appsync")
    @OptIn(ApolloExperimental::class)
    generateModelBuilders.set(true)
  }

  service("batching") {
    srcDir("src/main/graphql/batching")
    packageName.set("batching")
    @OptIn(ApolloExperimental::class)
    generateModelBuilders.set(true)
  }
}
