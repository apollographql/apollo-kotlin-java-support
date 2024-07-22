buildscript {
  repositories {
    mavenCentral()
  }
}

plugins {
  alias(libs.plugins.kotlin).apply(false)
  alias(libs.plugins.apollo).apply(false)
  alias(libs.plugins.ksp).apply(false)
  alias(libs.plugins.apollo.execution).apply(false)
}
