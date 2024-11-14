plugins {
  application
  pmd
  jacoco
  checkstyle
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.management)
  alias(libs.plugins.javaagent)
  id("org.openapi.generator") version "7.9.0"
  id("com.unclezs.gradle.sass") version "1.0.10"
}

group = "code"
java.toolchain.languageVersion = JavaLanguageVersion.of(21)


application {
  mainClass = "code.ConversationApp"
}

try {
  apply(from = rootProject.file("gradle/util/misc.gradle.kts"))
  apply(from = rootProject.file("gradle/util/git.gradle.kts"))
  apply(from = rootProject.file("gradle/util/docker.gradle.kts"))
} catch (e: Exception) {
  println("Error while loading utils ${e.message}")
}

repositories {
  mavenCentral()
  maven { url = uri("https://repo.spring.io/release") }
  maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
  implementation(libs.spring.modulith)

  implementation(libs.bundles.spring.data)
  runtimeOnly(libs.postgresql)

  implementation(libs.bundles.observability)
  runtimeOnly(libs.modulith.observability)

  implementation(libs.bundles.events)

  implementation(libs.bundles.spring.web)
  implementation(libs.bundles.thymeleaf)

//  implementation(libs.bundles.jmolecules)
  compileOnly(libs.lombok)
  annotationProcessor(libs.lombok)
  testImplementation(libs.lombok)
  testAnnotationProcessor(libs.lombok)
  implementation(libs.mapstruct)
  annotationProcessor(libs.bundles.mapstruct.annotation)

  testImplementation(libs.bundles.testcontainers)
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly(libs.junit.platform)
  testImplementation(libs.bundles.spring.test)

  developmentOnly(libs.spring.dev.tools)
  testJavaagent(libs.javaagent.impl)

  implementation("com.google.code.findbugs:jsr305:3.0.2")
//  implementation("org.openapitools:openapi-java-client:v1beta")
  implementation("io.swagger.core.v3:swagger-annotations:2.2.25")
  implementation("org.openapitools:jackson-databind-nullable:0.2.6")
  implementation("javax.annotation:javax.annotation-api:1.3.2")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.slf4j:slf4j-api:2.0.5")
  implementation("com.github.tomakehurst:wiremock-standalone:2.27.2")
//  implementation("org.wiremock:wiremock-standalone:3.9.2") // use once cause of 500 errors is found
//  testImplementation ("io.rest-assured:rest-assured:${restAssured}")
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.modulith:spring-modulith-bom:1.2.1")
//    mavenBom("org.jmolecules:jmolecules-bom:2023.1.4")
    mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.6.0")
  }
}

sourceSets {
  named("main") {
    java.srcDir(layout.buildDirectory.file("/generated/sources/openapi/src/main/java").get().asFile.path)
  }
}

openApiGenerate {
  generatorName.set("java")
  library.set("webclient")
  inputSpec.set("$projectDir/src/main/resources/openapi/generativelanguage_googleapis_com.json")
  outputDir.set(layout.buildDirectory.file("/generated/sources/openapi").get().asFile.path)
  apiPackage.set("code.openApi.infrastructure")
  modelPackage.set("code.openApi.model")
  configOptions.set(
    mapOf(
      "serializableModel" to "true",
      "dateLibrary" to "java8",
      "serializationLibrary" to "jackson"
    )
  )
}


tasks {

  sass {
    cssPath = "static/css"
    sassPath = "static/scss"
    // --rerun-tasks
    compileSass {
      outputs.upToDateWhen { false }
    }
  }
  compileJava {
    dependsOn(compileSass)
    options.encoding = "UTF-8"
  }
  compileTestJava {
    options.encoding = "UTF-8"
  }
  bootJar {
    archiveFileName = "${project.name}-${version}.${archiveExtension.get()}"
  }
  jar {
    enabled = false
  }

  test {
    useJUnitPlatform()
    testLogging {
      events("passed", "skipped", "failed")
    }
  }

  check {
    dependsOn(pmdMain)
    dependsOn(pmdTest)
    dependsOn(jacocoTestReport)
    dependsOn(checkstyleMain)
    dependsOn(checkstyleTest)
  }

  pmd {
    toolVersion = "7.4.0"
    isConsoleOutput = false
    isIgnoreFailures = true
    rulesMinimumPriority = 5
    ruleSets = listOf("category/java/errorprone.xml", "category/java/bestpractices.xml")
    pmdMain {
      exclude(
        "code/openApi/**"
      )
      doLast {
        val reportPath = layout.buildDirectory.file("reports/pmd/main.html").get().asFile
        println("PmdMain report: file://${reportPath.toURI().path}")
      }
    }
    pmdTest {
      exclude(
      )
      doLast {
        val reportPath = layout.buildDirectory.file("reports/pmd/test.html").get().asFile
        println("PmdTest report: file://${reportPath.toURI().path}")
      }
    }
  }

  checkstyle {
    isIgnoreFailures = true
    isShowViolations = false
    configFile = file("config/checkstyle.xml")
    checkstyleMain {
      source("src/main/java")
      classpath = project.files()
    }
    checkstyleTest {
      source("src/test/java")
      classpath = project.files()
    }
  }

  jacoco {
    jacocoTestReport {
      reports {
        xml.required = false
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("reports/jacoco")
      }
      doLast {
        val reportPath = layout.buildDirectory.file("reports/jacoco/index.html").get().asFile
        println("Jacoco report: file://${reportPath.toURI().path}")
      }
      classDirectories.setFrom(
        files(classDirectories.files.map {
          fileTree(it) {
            exclude(
              "code/openApi/**"
            )
          }
        })
      )
      dependsOn(test)
    }
  }

  register("report") {
    doLast {
      var reportPath = layout.buildDirectory.file("reports/jacoco/index.html").get().asFile
      println("Jacoco report: file://${reportPath.toURI().path}")
      reportPath = layout.buildDirectory.file("reports/checkstyle/main.html").get().asFile
      println("CheckstyleMain report: file://${reportPath.toURI().path}")
      reportPath = layout.buildDirectory.file("reports/checkstyle/test.html").get().asFile
      println("CheckstyleTest report: file://${reportPath.toURI().path}")
      reportPath = layout.buildDirectory.file("reports/pmd/main.html").get().asFile
      println("PmdMain report: file://${reportPath.toURI().path}")
      reportPath = layout.buildDirectory.file("reports/pmd/test.html").get().asFile
      println("PmdTest report: file://${reportPath.toURI().path}")

    }
  }

  javadoc {
    setDestinationDir(file(layout.buildDirectory.dir("reports/javadoc")))
    options.encoding = "UTF-8"
  }
}