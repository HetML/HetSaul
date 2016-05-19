val cogcompNLPVersion = "3.0.20"
val cogcompPipelineVersion = "0.1.16"

lazy val root = (project in file(".")).
  aggregate(saulCore, saulExamples)

lazy val commonSettings = Seq(
  organization := "edu.illinois.cs.cogcomp",
  name := "saul-project",
  version := "0.1",
  scalaVersion := "2.11.7",
  resolvers ++= Seq(
    Resolver.mavenLocal,
    "CogcompSoftware" at "http://cogcomp.cs.illinois.edu/m2repo/"
  ),
  javaOptions ++= List("-Xmx6g"),
  libraryDependencies ++= Seq(
    "edu.illinois.cs.cogcomp" % "LBJava" % "1.2.16" withSources,
    "edu.illinois.cs.cogcomp" % "illinois-core-utilities" % cogcompNLPVersion withSources,
    "com.gurobi" % "gurobi" % "6.0",
    "org.apache.commons" % "commons-math3" % "3.0",
    "org.scalatest" % "scalatest_2.11" % "2.2.4"
  ),
  fork := true,
  publishTo := Some(Resolver.sftp("CogcompSoftwareRepo", "bilbo.cs.illinois.edu", "/mounts/bilbo/disks/0/www/cogcomp/html/m2repo/")),
  isSnapshot := true
)

lazy val saulCore = (project in file("saul-core")).
  settings(commonSettings: _*).
  settings(
    name := "saul",
    libraryDependencies ++= Seq(
      "com.typesafe.play" % "play_2.11" % "2.4.3" exclude("ch.qos.logback", "logback-classic")
    )
  )

lazy val saulExamples = (project in file("saul-examples")).
  settings(commonSettings: _*).
  settings(
    name := "saul-examples",
    libraryDependencies ++= Seq(
      "edu.illinois.cs.cogcomp" % "illinois-nlp-pipeline" % cogcompPipelineVersion withSources,
      "edu.illinois.cs.cogcomp" % "illinois-curator" % cogcompNLPVersion,
      "edu.illinois.cs.cogcomp" % "illinois-edison" % cogcompNLPVersion,
      "edu.illinois.cs.cogcomp" % "illinois-nlp-readers" % "0.0.2-SNAPSHOT",
      "edu.illinois.cs.cogcomp" % "saul-pos-tagger-models" % "1.0",
      "edu.illinois.cs.cogcomp" % "saul-er-models" % "1.3"
    )
  ).dependsOn(saulCore).aggregate(saulCore)

lazy val saulWebapp = (project in file("saul-webapp")).
  enablePlugins(PlayScala).
  settings(commonSettings: _*).
  settings(
    name := "saul-webapp",
    libraryDependencies ++= Seq(
      "org.webjars" %% "webjars-play" % "2.4.0-1",
      "org.webjars" % "bootstrap" % "3.1.1-2",
      jdbc,
      cache,
      ws,
      specs2 % Test
    ),
    resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
    routesGenerator := InjectedRoutesGenerator
  ).dependsOn(saulCore).aggregate(saulCore)

lazy val srlModels = project
  .in(file("models/"))
  .settings(
    organization              := "edu.illinois.cs.cogcomp",
    name                      := "saul-illinois-srl",
    version                   := "0.1",
    crossPaths                := false,  //don't add scala version to this artifacts in repo
    publishMavenStyle         := true,
    autoScalaLibrary          := false,  //don't attach scala libs as dependencies
    description               := "project for publishing dependency to maven repo",
    packageBin in Compile     := baseDirectory.value / s"${name.value}.jar"
    //packageDoc in Compile     := baseDirectory.value / s"${name.value}-javadoc.jar"
  )
 publishTo := Some(Resolver.sftp("CogcompSoftwareRepo", "bilbo.cs.illinois.edu", "/mounts/bilbo/disks/0/www/cogcomp/html/m2repo/"))
