import com.gradleup.librarian.gradle.librarianModule
import org.gradle.jvm.tasks.Jar

plugins {
  id("org.jetbrains.kotlin.jvm")
}

librarianModule()

dependencies {
  api(libs.apollo.api)
  api(libs.okhttp)
}

tasks.withType(Jar::class.java).configureEach {
  manifest {
    attributes(mapOf("Automatic-Module-Name" to "com.apollographql.java.client"))
  }
}
