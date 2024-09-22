# Fetch Interview App

Android app developed for an interview for Fetch .

## Description

This is an Android written in Kotlin that fetches json data from [the provided url](https://fetch-hiring.s3.amazonaws.com/hiring.json) and presents it in an easy to read list that has sorted the data by both listId and name fields, filtered out any null or blank names, and grouped data with matching listId fields. 

## Getting Started

### Dependencies

This app was developed in Kotlin version 2.0.20, targeting the Android 34 SDK.

A dependency that has been added is the Kotlinx Serialization library, which can be added in plugins and dependencies in the app level build.gradle.kts:
```
plugins{
    kotlin("plugin.serialization") version "2.0.20"
}
```
```
dependencies {
    implementation(libs.kotlinx.serialization.json)
}
```
And a kotlin version target can be added in the project level build.gradle.kts:
```
plugins{
    kotlin("jvm") version "2.0.20" apply false}
}
```

### Installing

Feel free to clone this repository and open the project in Android Studio.

### Executing program

The program can either be installed and ran on Android Studio's device emulator, or a connected physical phone. 

## Help

For any questions or issues please feel free to contact me via email. Thank you for checking this repository out!

## Authors

Joshua Baesler
