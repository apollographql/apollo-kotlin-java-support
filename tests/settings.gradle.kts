pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.apply {
      mavenCentral()
      maven("https://storage.googleapis.com/apollo-previews/m2/") {
        content {
          includeGroup("com.apollographql.apollo")
        }
      }
    }
  }
}

rootProject.name = "apollo-kotlin-java-support-tests"

// Include all tests
rootProject.projectDir
  .listFiles()!!
  .filter { it.isDirectory && File(it, "build.gradle.kts").exists() }
  .forEach {
    include(it.name)
  }

includeBuild("../")
