plugins {
  `embedded-kotlin`
}

dependencies {
  implementation(libs.kgp)
  implementation(libs.librarian)
  implementation(libs.apollo.gradle.plugin)
  implementation(libs.ksp)
  implementation(libs.apollo.execution.gradle.plugin)
}

group = "build-logic"
