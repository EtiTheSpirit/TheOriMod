@ECHO OFF
ECHO Did you remember to set the version in both build.gradle AND mods.toml?
ECHO If you didn't, go do that now.
PAUSE
.\gradlew build
PAUSE