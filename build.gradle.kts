import com.gradleup.librarian.gradle.Librarian

plugins {
  alias(libs.plugins.kotlin).apply(false)
  alias(libs.plugins.librarian).apply(false)
}


Librarian.root(project)
