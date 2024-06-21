import com.gradleup.librarian.core.librarianModule
import org.gradle.jvm.tasks.Jar

plugins {
  id("org.jetbrains.kotlin.jvm")
}

librarianModule()

dependencies {
  api(libs.rx.java2)
  api(project(":client"))
}

tasks.withType(Jar::class.java).configureEach {
  manifest {
    attributes(mapOf("Automatic-Module-Name" to "com.apollographql.java.rx2"))
  }
}
