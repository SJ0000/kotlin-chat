import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    java
    jacoco
    kotlin("jvm") version "1.9.20"
    kotlin("kapt") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
    kotlin("plugin.allopen") version "1.9.22"
}

group = "sj"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("io.jsonwebtoken:jjwt-api:0.12.3")

    // QueryDsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    implementation("com.querydsl:querydsl-apt:5.0.0")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

    // mysql
    implementation("com.mysql:mysql-connector-j:8.3.0")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    runtimeOnly("com.h2database:h2")
    // oracle
    runtimeOnly("com.oracle.database.jdbc:ojdbc8")
    // rabbitmq
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    // fcm
    implementation("com.google.firebase:firebase-admin:9.3.0")

    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:1.0.6")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter:1.19.6")
    testImplementation("org.testcontainers:mysql:1.19.3")
    testImplementation("org.testcontainers:mongodb:1.19.3")
    testImplementation("org.testcontainers:oracle-xe:1.19.7")
    testImplementation("org.testcontainers:rabbitmq:1.19.7")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

jacoco {
    toolVersion = "0.8.12"
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    withType<BootJar> {
        enabled = true
        archiveFileName = "simple-messenger-server.jar"
    }

    named<Jar>("jar") {
        enabled = false
    }
}

tasks.test {
    extensions.configure(JacocoTaskExtension::class) {
        setDestinationFile(
            file(
                layout.buildDirectory.dir(
                    "jacoco/jacoco.exec"
                ).get().asFile
            )
        )
    }
}

val queryDslQClassPatterns = ('A'..'Z').map { "Q${it}*" }

tasks.jacocoTestReport {
    reports {
        // html report 사용
        html.required = true
        xml.required = false
        csv.required = false
    }

    // querydsl GeneratedClass를 report에서 제외
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it){
                exclude(queryDslQClassPatterns.map { "**/${it}" })
            }
        })
    )

}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            // element가 없으면, 프로젝트 전체 파일을 합친 기준 값으로 한다.
            limit {
                // counter 기본값은 "INSTRUCTION"
                // value 기본값은 "COVEREDRATIO"
                minimum = "0.30".toBigDecimal()
            }
        }

        rule {
            // 클래스 단위로 체크
            element = "CLASS"

            // 브랜치 커버리지를 최소 90% 만족해야 한다
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }

            // 라인 커버리지 최소 80% 만족
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }

            // 빈 줄을 제외한 코드의 라인 수를 최대 200라인으로 제한.
            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "200".toBigDecimal()
            }

            excludes = listOf("*.test.*") + queryDslQClassPatterns.map { "*.${it}" }
        }
    }
}

val testCoverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage"

    dependsOn(
        ":test",
        ":jacocoTestReport",
        ":jacocoTestCoverageVerification"
    )

    tasks["jacocoTestReport"].mustRunAfter(tasks["test"])
    tasks["jacocoTestCoverageVerification"].mustRunAfter(tasks["jacocoTestReport"])
}