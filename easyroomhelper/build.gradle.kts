plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.islamsaadi.easyroomhelper"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

group = "com.github.islamsaadi"
version = "1.1.3"

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // --- expose Room to consumers ---
    api(libs.room.runtime)
    api(libs.room.common.jvm)
    annotationProcessor(libs.room.compiler)

}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = project.group.toString()
                artifactId = "easyroomhelper"
                version = project.version.toString()

                artifact(tasks.getByName("bundleReleaseAar"))

                pom {
                    withXml {
                        val depsNode = asNode().appendNode("dependencies")
                        configurations["api"].dependencies.forEach { dep ->
                            val dependencyNode = depsNode.appendNode("dependency")
                            dependencyNode.appendNode("groupId", dep.group)
                            dependencyNode.appendNode("artifactId", dep.name)
                            dependencyNode.appendNode("version", dep.version)
                            dependencyNode.appendNode("scope", "compile")
                        }
                        configurations["implementation"].dependencies.forEach { dep ->
                            val dependencyNode = depsNode.appendNode("dependency")
                            dependencyNode.appendNode("groupId", dep.group)
                            dependencyNode.appendNode("artifactId", dep.name)
                            dependencyNode.appendNode("version", dep.version)
                            dependencyNode.appendNode("scope", "runtime")
                        }
                    }
                }
            }
        }

        repositories {
            mavenLocal()
        }
    }
}