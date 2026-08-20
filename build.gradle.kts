plugins {
   	kotlin("jvm") version "2.3.21"
	kotlin("plugin.spring") version "2.3.21"

 	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7" 
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}


allprojects {
    group = "com.pl.platform"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}