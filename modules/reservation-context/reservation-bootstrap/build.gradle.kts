plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":shared:shared-kernel"))
    implementation(project(":modules:reservation-context:reservation-domain"))
    implementation(project(":modules:reservation-context:reservation-application"))
    implementation(project(":modules:reservation-context:reservation-adapter"))

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // DB
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
}
