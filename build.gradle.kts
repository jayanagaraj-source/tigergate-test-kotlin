plugins {
    kotlin("jvm") version "2.1.0"
    application
    id("org.cyclonedx.bom") version "1.10.0"
}

group = "com.tigergate"
version = "0.1.0"

repositories { mavenCentral() }

// --- Scanner-portability guards -------------------------------------------------
// This repo is scanned by external tools on runners we do not control. Both settings
// below exist so that `gradle dependencies` / SBOM generation can NEVER fail because
// of the local environment: a failed resolution yields an empty package list, which is
// indistinguishable from a project that genuinely has no dependencies.
//
// 1. NO hard toolchain pin. `kotlin { jvmToolchain(17) }` makes resolution of
//    runtimeClasspath fail outright on a runner without that exact JDK
//    ("Cannot find a Java installation ... matching {languageVersion=17}"), because
//    the classpath's target-JVM attribute forces compileJava's toolchain to resolve.
//    Instead we compile against whatever JDK is running, capped at 17.
//    Opt back into a reproducible toolchain with -PjdkToolchain=17.
// 2. Dependency locking is OPT-IN (-PstrictLocks=true). gradle.lockfile stays
//    committed for lockfile-based SCA, but lock drift cannot fail a scanner's run.
val pinnedToolchain = (findProperty("jdkToolchain") as String?)?.toIntOrNull()
val targetMajor = pinnedToolchain
    ?: minOf(JavaVersion.current().majorVersion.toInt(), 17).coerceAtLeast(8)

kotlin {
    if (pinnedToolchain != null) {
        jvmToolchain(pinnedToolchain)
    } else {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(targetMajor.toString()))
        }
    }
}

java {
    if (pinnedToolchain == null) {
        sourceCompatibility = JavaVersion.toVersion(targetMajor)
        targetCompatibility = JavaVersion.toVersion(targetMajor)
    }
}

tasks.withType<JavaCompile>().configureEach {
    if (pinnedToolchain == null) options.release.set(targetMajor)
}

if (findProperty("strictLocks") == "true") {
    dependencyLocking { lockAllConfigurations() }
}

dependencies {
    // Every version below is deliberately outdated and carries published CVEs (see SECURITY_FIXTURES.md).
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")        // CVE-2021-44228 Log4Shell, CVE-2021-45046
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")  // CVE-2019-12384, CVE-2019-14379, CVE-2020-36518 ...
    implementation("org.yaml:snakeyaml:1.26")                            // CVE-2022-1471, CVE-2022-25857
    implementation("org.apache.commons:commons-text:1.9")                // CVE-2022-42889 Text4Shell
    implementation("commons-io:commons-io:2.6")                           // CVE-2021-29425
    implementation("commons-collections:commons-collections:3.2.1")      // CVE-2015-6420 deserialisation gadget
    implementation("com.google.guava:guava:19.0")                         // CVE-2018-10237, CVE-2020-8908
    implementation("com.squareup.okhttp3:okhttp:3.12.0")                 // CVE-2021-0341
    implementation("org.apache.httpcomponents:httpclient:4.5.10")        // CVE-2020-13956
    implementation("org.postgresql:postgresql:42.2.5")                    // CVE-2020-13692, CVE-2022-21724
    implementation("com.h2database:h2:1.4.199")                           // CVE-2021-42392, CVE-2022-23221
    implementation("org.bouncycastle:bcprov-jdk15on:1.60")               // CVE-2018-1000613, CVE-2020-15522
    implementation("org.springframework:spring-web:5.3.15")               // CVE-2022-22965 Spring4Shell, CVE-2016-1000027
    compileOnly("javax.servlet:javax.servlet-api:3.1.0")

    testImplementation(kotlin("test-junit"))
    testImplementation("junit:junit") { version { strictly("4.12") } }    // CVE-2020-15250 (kotlin-test-junit would otherwise pull 4.13.2)
}

application { mainClass.set("com.tigergate.fixture.AppKt") }

tasks.test { useJUnit() }

tasks.cyclonedxBom {
    setIncludeConfigs(listOf("runtimeClasspath"))
    setProjectType("application")
    setSchemaVersion("1.5")
    setDestination(project.file("sbom"))
    setOutputName("bom")
    setOutputFormat("all")
    setIncludeLicenseText(false)
}
