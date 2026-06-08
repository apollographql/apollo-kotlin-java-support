pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
    it.maven("https://storage.googleapis.com/gradleup/m2") {
      content {
        // Those libraries are used at build time but using separate configurations that are not part of pluginManagement.
        // This is why we need to include them here explicitely
        includeGroup("com.gradleup.librarian")
        includeModule("com.gradleup.gratatouille", "gratatouille-processor")
        includeModule("com.gradleup.nmcp", "nmcp-tasks")
      }
    }
  }
}

include("client", "rx2", "rx3")
