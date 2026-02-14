// reservation-application: domain에만 의존, Port 인터페이스 정의
dependencies {
    implementation(project(":shared:shared-kernel"))
    implementation(project(":modules:reservation-context:reservation-domain"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.mockito:mockito-core:5.15.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.15.2")
}
