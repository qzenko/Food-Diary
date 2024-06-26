pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
} // Добавьте точку с запятой здесь

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Working"
include(":app")