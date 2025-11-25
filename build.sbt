name := "my8086"
version := "0.1.0"
scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "edu.berkeley.cs" %% "chisel3" % "3.6.0",
  "edu.berkeley.cs" %% "chiseltest" % "0.6.2" % "test"
)

scalacOptions ++= Seq(
  "-language:reflectiveCalls",
  "-deprecation",
  "-feature",
  "-Xcheckinit"
)

addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.6.0" cross CrossVersion.full)
