// Root project: personal-maven-repository-collection
// Each subproject (checkstyle-rule, bootstrap) has its own build.gradle.kts

allprojects {
    group = "com.weehong.maven"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/weehong-1/personal-maven-repository-collection")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
