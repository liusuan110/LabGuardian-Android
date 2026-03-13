pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LabGuardian"

include(":app")
include(":core:model")
include(":core:network")
include(":core:data")
include(":core:ui")
include(":core:common")
include(":feature:dashboard")
include(":feature:camera")
include(":feature:guidance")
