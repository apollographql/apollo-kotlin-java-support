pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
  }
}

includeBuild("build-logic")

include("runtime", "rx2", "rx3")
