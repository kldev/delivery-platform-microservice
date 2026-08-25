plugins {
   	kotlin("jvm") version "2.4.0"
	kotlin("plugin.spring") version "2.4.0"
	kotlin("plugin.allopen") version "2.4.0" apply false
    id("io.quarkus") version "3.38.3" apply false
	kotlin("plugin.jpa") version "2.4.10" apply false

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