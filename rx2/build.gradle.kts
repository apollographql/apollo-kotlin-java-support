import com.gradleup.librarian.core.librarianModule

plugins {
  id("org.jetbrains.kotlin.jvm")
}

librarianModule()

dependencies {
  api(libs.rx.java2)
  api(project(":runtime"))
}
