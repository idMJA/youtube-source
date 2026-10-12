plugins {
    java
    alias(libs.plugins.maven.publish.base) apply false
}

val (gitVersion, release) = versionFromGit()
logger.lifecycle("Version: $gitVersion (release: $release)")

allprojects {
    group = "dev.lavalink.youtube"
    // The plugin project is the only one that should not have a snapshot version since lavalink expects the jar name to be specific
    version = if (project.name == "plugin") {
        gitVersion.removeSuffix("-SNAPSHOT")
    } else {
        gitVersion
    }


    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://maven.lavalink.dev/releases")
        maven(url = "https://jitpack.io")
    }
}

subprojects {
    apply<JavaPlugin>()
    apply<MavenPublishPlugin>()

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    configure<PublishingExtension> {
        val mavenUsername = findProperty("MAVEN_USERNAME") as String?
        val mavenPassword = findProperty("MAVEN_PASSWORD") as String?
        if (!mavenUsername.isNullOrEmpty() && !mavenPassword.isNullOrEmpty()) {
            repositories {
                val snapshots = "https://maven.lavalink.dev/snapshots"
                val releases = "https://maven.lavalink.dev/releases"

                maven(if (release) releases else snapshots) {
                    credentials {
                        username = mavenUsername
                        password = mavenPassword
                    }
                }
            }
        } else {
            logger.lifecycle("Not publishing to maven.lavalink.dev because credentials are not set")
        }
    }
}

fun versionFromGit(): Pair<String, Boolean> {
    try {
        val process = ProcessBuilder("git", "describe", "--tags", "--exact-match")
            .directory(project.rootDir)
            .redirectErrorStream(true)
            .start()
        val tag = process.inputStream.bufferedReader().readLine()?.trim()
        val exitCode = process.waitFor()

        if (exitCode == 0 && !tag.isNullOrEmpty()) {
            return tag to true
        }

        val revProcess = ProcessBuilder("git", "rev-parse", "HEAD")
            .directory(project.rootDir)
            .redirectErrorStream(true)
            .start()
        val commitHash = revProcess.inputStream.bufferedReader().readLine()?.trim()
        revProcess.waitFor()

        if (!commitHash.isNullOrEmpty() && commitHash.matches(Regex("[0-9a-fA-F]{7,40}"))) {
            return "${commitHash.take(7)}-SNAPSHOT" to false
        }
    } catch (_: Exception) {}

    return "HEAD-SNAPSHOT" to false
}