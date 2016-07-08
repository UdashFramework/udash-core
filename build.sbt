name := "udash-guide"

version in ThisBuild := "0.3.0-SNAPSHOT"
scalaVersion in ThisBuild := versionOfScala
organization in ThisBuild := "io.udash"
scalacOptions in ThisBuild ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions",
  "-language:existentials",
  "-language:dynamics",
  "-language:experimental.macros",
  "-Xfuture",
  "-Xfatal-warnings",
  "-Xlint:_,-missing-interpolator,-adapted-args"
)

val commonSettings = Seq(
  moduleName := "udash-guide-" + moduleName.value,
  (publishArtifact in packageDoc) := false
)

lazy val udashGuide = project.in(file("."))
  .aggregate(sharedJS, sharedJVM, guide, homepage, backend, `frontend-commons`)
  .dependsOn(backend)
  .settings(
    publishArtifact := false,
    mainClass in Compile := Some("io.udash.guide.Launcher")
  )

lazy val shared = crossProject.crossType(CrossType.Pure).in(file("shared"))
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= crossDeps.value
  )

lazy val sharedJVM = shared.jvm
lazy val sharedJS = shared.js

lazy val backend = project.in(file("backend"))
  .dependsOn(sharedJVM)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= backendDeps.value,

    (compile in Compile) <<= (compile in Compile).dependsOn(copyStatics),
    copyStatics := {
      copyStaticsToBackend(homepage).value
      copyStaticsToBackend(guide).value
    },
    copyStatics <<= copyStatics.dependsOn(compileStatics in guide, compileStatics in homepage),

    mappings in (Compile, packageBin) ++= {
      prepareMappings(homepage).value ++ prepareMappings(guide).value
    },

    watchSources ++= (sourceDirectory in guide).value.***.get,

    assemblyJarName in assembly := "udash-guide.jar",
    mainClass in assembly := Some("io.udash.guide.Launcher"),
    assemblyMergeStrategy in assembly := {
      case "JS_DEPENDENCIES" => MergeStrategy.concat
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )

lazy val `frontend-commons` = project.in(file("commons")).enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= frontendDeps.value,
    staticFilesDir := "UdashStatic/commons",
    compileStatics := {
      IO.copyDirectory(sourceDirectory.value / "main/assets/pdf", target.value / staticFilesDir.value / "WebContent/assets/pdf")
      IO.copyDirectory(sourceDirectory.value / "main/assets/svg", target.value / staticFilesDir.value / "WebContent/assets/svg")
      target.value / staticFilesDir.value
    }
  )

val commonFrontendSettings = Seq(
  jsDependencies ++= frontendJSDeps.value,
  persistLauncher in Compile := true,

  compile <<= (compile in Compile),

  artifactPath in(Compile, fastOptJS) :=
    (target in(Compile, fastOptJS)).value / staticFilesDir.value / "WebContent" / "scripts" / "frontend-impl-fast.js",
  artifactPath in(Compile, fullOptJS) :=
    (target in(Compile, fullOptJS)).value / staticFilesDir.value / "WebContent" / "scripts" / "frontend-impl.js",
  artifactPath in(Compile, packageJSDependencies) :=
    (target in(Compile, packageJSDependencies)).value / staticFilesDir.value / "WebContent" / "scripts" / "frontend-deps-fast.js",
  artifactPath in(Compile, packageMinifiedJSDependencies) :=
    (target in(Compile, packageMinifiedJSDependencies)).value / staticFilesDir.value / "WebContent" / "scripts" / "frontend-deps.js",
  artifactPath in(Compile, packageScalaJSLauncher) :=
    (target in(Compile, packageScalaJSLauncher)).value / staticFilesDir.value / "WebContent" / "scripts" / "frontend-init.js",

  requiresDOM in Test := true,
  persistLauncher in Test := false,
  scalaJSUseRhino in Test := false
)

lazy val guide = project.in(file("guide")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`frontend-commons`)
  .settings(commonSettings: _*)
  .settings(commonFrontendSettings: _*)
  .settings(
    staticFilesDir := "UdashStatic/guide",
    compileStatics := {
      IO.copyDirectory((compileStatics in `frontend-commons`).value, target.value / staticFilesDir.value)
      IO.copyDirectory(sourceDirectory.value / "main/assets/fonts", target.value / staticFilesDir.value / "WebContent/assets/fonts")
      IO.copyDirectory(sourceDirectory.value / "main/assets/images", target.value / staticFilesDir.value / "WebContent/assets/images")
      IO.copyDirectory(sourceDirectory.value / "main/assets/prism", target.value / staticFilesDir.value / "WebContent/assets/prism")
      compileStaticsForRelease.value
      target.value / staticFilesDir.value
    },
    compileStatics <<= compileStatics.dependsOn(compile in Compile)
  )

lazy val homepage = project.in(file("homepage")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`frontend-commons`)
  .settings(commonSettings: _*)
  .settings(commonFrontendSettings: _*)
  .settings(
    jsDependencies ++= homepageJSDeps.value,

    staticFilesDir := "UdashStatic/homepage",
    compileStatics := {
      IO.copyDirectory((compileStatics in `frontend-commons`).value, target.value / staticFilesDir.value)
      IO.copyDirectory(sourceDirectory.value / "main/assets/fonts", target.value / staticFilesDir.value / "WebContent/assets/fonts")
      IO.copyDirectory(sourceDirectory.value / "main/assets/images", target.value / staticFilesDir.value / "WebContent/assets/images")
      IO.copyDirectory(sourceDirectory.value / "main/assets/prism", target.value / staticFilesDir.value / "WebContent/assets/prism")
      IO.copyDirectory(sourceDirectory.value / "main/assets/scrollbar", target.value / staticFilesDir.value / "WebContent/assets/scrollbar")
      IO.copyDirectory(sourceDirectory.value / "main/assets/svg4everybody", target.value / staticFilesDir.value / "WebContent/assets/svg4everybody")
      compileStaticsForRelease.value
      target.value / staticFilesDir.value
    },
    compileStatics <<= compileStatics.dependsOn(compile in Compile)
  )

lazy val selenium = project.in(file("selenium"))
  .dependsOn(backend)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= seleniumDeps.value,
    libraryDependencies ++= testDeps.value,

    parallelExecution := false
  )