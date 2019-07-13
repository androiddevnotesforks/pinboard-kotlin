object Versions {
    const val minSdkVersion = 21
    const val targetSdkVersion = 28
    const val compileSdkVersion = 28
    const val buildToolsVersion = "28.0.3"

    internal const val kotlinVersion = "1.3.40"
    internal const val coroutinesVersion = "1.2.1"
    internal const val lifecycleVersion = "2.1.0-beta01"
    internal const val roomVersion = "2.1.0"
}

object Classpaths {
    const val gradlePlugin = "com.android.tools.build:gradle:3.4.0"
    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    const val jacocoPlugin = "org.jacoco:org.jacoco.core:0.8.1"
    const val dexCountPlugin = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.8.2"
}

object Dependencies {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}"

    const val supportLibrary = "androidx.appcompat:appcompat:1.1.0-beta01"
    const val materialDesign = "com.google.android.material:material:1.1.0-alpha07"
    const val supportAnnotations = "androidx.annotation:annotation:1.0.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta1"

    const val coreLib = "com.fibelatti.core:core:1.1.0-alpha3"
    const val coreLibArch = "com.fibelatti.core:arch-components:1.1.0-alpha3"

    const val archComponents = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycleVersion}"
    const val archComponentsCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycleVersion}"

    const val room = "androidx.room:room-runtime:${Versions.roomVersion}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.roomVersion}"

    const val customTabs = "androidx.browser:browser:1.0.0"

    private const val daggerVersion = "2.22.1"

    const val dagger = "com.google.dagger:dagger:$daggerVersion"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:$daggerVersion"

    const val gson = "com.google.code.gson:gson:2.8.5"

    private const val retrofitVersion = "2.6.0"

    const val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
    const val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:$retrofitVersion"
    const val httpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:3.10.0"

    const val jsoup = "org.jsoup:jsoup:1.11.3"
}

object TestDependencies {
    private const val junit5Version = "5.4.2"

    const val coreLibTest = "com.fibelatti.core:core-test:1.1.0-alpha3"
    const val coreLibArchTest = "com.fibelatti.core:arch-components-test:1.1.0-alpha3"

    const val junit = "junit:junit:4.12"
    const val junit5 = "org.junit.jupiter:junit-jupiter-api:$junit5Version"
    const val junit5Engine = "org.junit.jupiter:junit-jupiter-engine:$junit5Version"
    const val junit5Params = "org.junit.jupiter:junit-jupiter-params:$junit5Version"
    const val junitVintage = "org.junit.vintage:junit-vintage-engine:$junit5Version"
    const val testRunner = "com.android.support.test:runner:1.1.0"
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlinVersion}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesVersion}"
    const val mockitoCore = "org.mockito:mockito-core:2.23.0"
    const val mockitoAndroid = "org.mockito:mockito-android:2.18.3"
    const val archComponentsTest = "android.arch.core:core-testing:${Versions.lifecycleVersion}"
}
