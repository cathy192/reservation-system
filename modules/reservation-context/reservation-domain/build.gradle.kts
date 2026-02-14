// reservation-domain: 순수 Java, 외부 프레임워크 의존성 0
dependencies {
    implementation(project(":shared:shared-kernel"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.3")
}
