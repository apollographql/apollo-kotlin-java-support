import com.apollographql.execution.gradle.internal.CopySchema

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.google.devtools.ksp")
  id("com.apollographql.execution")
}

dependencies {
  implementation(libs.apollo.api)
  implementation(libs.kotlinx.coroutines)
  implementation(libs.atomicfu.library)
  implementation(libs.apollo.execution.runtime)

  implementation(platform(libs.http4k.bom.get()))
  implementation(libs.http4k.core)
  implementation(libs.http4k.server.jetty)
  implementation(libs.slf4j.nop.get().toString()) {
    because("jetty uses SL4F")
  }
}

apolloExecution {
  service("sampleserver") {
    packageName = "sample.server"
  }
}

tasks.withType(CopySchema::class.java).configureEach {
  doFirst {
    // https://github.com/apollographql/apollo-kotlin-execution/pull/6
    (this as CopySchema).to.get().asFile.delete()
  }
}
