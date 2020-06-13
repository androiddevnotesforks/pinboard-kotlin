import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply(from = "detekt.gradle")

buildscript {
    val jacocoEnabled: String? by project
    extra["jacocoEnabled"] = jacocoEnabled?.toBoolean() ?: false

    repositories {
        google()
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath(Classpaths.gradlePlugin)
        classpath(Classpaths.kotlinPlugin)
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.28-alpha")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

subprojects {
    afterEvaluate {
        tasks.withType<KotlinCompile>().all {
            kotlinOptions.jvmTarget = "1.8"
        }

        tasks.findByName("preBuild")?.dependsOn(":detekt")
    }
}
