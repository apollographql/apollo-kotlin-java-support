import com.gradleup.librarian.core.librarianModule

plugins {
  id("org.jetbrains.kotlin.jvm")
}

librarianModule()

dependencies {
  api(libs.rx.java3)
  api(project(":runtime"))
}
