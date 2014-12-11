name := "Project 4"

version := "1.0"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"   % "2.3.7",
  "com.typesafe.akka" %% "akka-slf4j"   % "2.3.7",
  "com.typesafe.akka" %% "akka-remote"  % "2.3.7",
  "com.typesafe.akka" %% "akka-agent"   % "2.3.7",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.7" % "test"
)

Revolver.settings

ideaExcludeFolders += ".idea"

ideaExcludeFolders += ".idea_modules"