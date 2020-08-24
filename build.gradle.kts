val quarkusVersion: String = "1.5.0.Final"

plugins {
    kotlin("jvm") version "1.3.61"
    id ("io.quarkus") version "1.5.0.Final"
    id ("org.jetbrains.kotlin.plugin.allopen") version "1.3.72"
    id ("org.sonarqube") version "2.7"
    id ("jacoco")
}


group = "fr.convergence.proddoc"
version = "1.0-SNAPSHOT"

// je mets ces 2 variables ici car je n'arrive pas à les mettre ailleurs
// (dans settings.gradle.kts par exemple)
val myMavenRepoUser = "myMavenRepo"
val myMavenRepoPassword ="mask"

repositories {
    maven {
        url = uri("https://mymavenrepo.com/repo/OYRB63ZK3HSrWJfc2RIB/")
        credentials {
            username = myMavenRepoUser
            password = myMavenRepoPassword
        }
    }
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(enforcedPlatform("io.quarkus:quarkus-bom:$quarkusVersion"))
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("io.quarkus:quarkus-resteasy")
    implementation("io.quarkus:quarkus-rest-client")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-kafka-client:$quarkusVersion")
    implementation("io.quarkus:quarkus-smallrye-reactive-messaging-kafka:$quarkusVersion")
    implementation("io.quarkus:quarkus-smallrye-reactive-messaging:$quarkusVersion")
    implementation("io.quarkus:quarkus-kafka-streams:$quarkusVersion")
    implementation("io.debezium:debezium-core:1.1.2.Final")
    implementation("org.reflections:reflections:0.9.12")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    implementation("fr.convergence.proddoc.libs:MaskCache:1.0.2-SNAPSHOT")
    implementation("fr.convergence.proddoc.libs:MaskModel:1.0.0-SNAPSHOT")
    implementation("fr.convergence.proddoc.util:MaskSerdes:1.0-SNAPSHOT")

    testImplementation("io.quarkus:quarkus-junit5")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

allOpen {
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("javax.ws.rs.Path")
}
