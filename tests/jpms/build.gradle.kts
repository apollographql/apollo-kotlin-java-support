plugins {
  id("java")
  id("application")
  id("com.apollographql.apollo3")
}

dependencies {
  implementation("com.apollographql.java:client")
  implementation("com.apollographql.java:rx2")
  testImplementation(libs.junit)
}

application {
  mainModule.set("com.example.app") // name defined in module-info.java
  mainClass.set("com.example.app.Main")
}

afterEvaluate {
  project.tasks.withType(JavaCompile::class.java).configureEach {
    // Override the default. JPMS is only available with Java9+
    options.release.set(9)
  }
}
apollo {
  service("service") {
    packageName.set("com.example")
  }
}
