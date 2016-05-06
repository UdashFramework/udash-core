name := "udash-guide"

version in ThisBuild := "0.2.0-SNAPSHOT"
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
  .aggregate(sharedJS, sharedJVM, guide, backend)
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

    (compile in Compile) <<= (compile in Compile).dependsOn(copyStaticsGuide),
    copyStaticsGuide := IO.copyDirectory((crossTarget in guide).value / GuideStaticFilesDir, (target in Compile).value / GuideStaticFilesDir),
    copyStaticsGuide <<= copyStaticsGuide.dependsOn(compileStaticsGuide in guide),

    (compile in Compile) <<= (compile in Compile).dependsOn(copyStaticsHomepage),
    copyStaticsHomepage := IO.copyDirectory((crossTarget in homepage).value / HomepageStaticFilesDir, (target in Compile).value / HomepageStaticFilesDir),
    copyStaticsHomepage <<= copyStaticsHomepage.dependsOn(compileStaticsHomepage in homepage),

    mappings in (Compile, packageBin) ++= {
      copyStaticsGuide.value
      ((target in Compile).value / GuideStaticFilesDir).***.get map { file =>
        file -> file.getAbsolutePath.stripPrefix((target in Compile).value.getAbsolutePath)
      }
    },

    mappings in (Compile, packageBin) ++= {
      copyStaticsHomepage.value
      ((target in Compile).value / HomepageStaticFilesDir).***.get map { file =>
        file -> file.getAbsolutePath.stripPrefix((target in Compile).value.getAbsolutePath)
      }
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

lazy val guide = project.in(file("guide")).enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= frontendDeps.value,
    jsDependencies ++= frontendJSDeps.value,
    persistLauncher in Compile := true,

    compileStaticsGuide := {
      IO.copyDirectory(sourceDirectory.value / "main/assets/fonts", crossTarget.value / GuideStaticFilesDir / "WebContent/assets/fonts")
      IO.copyDirectory(sourceDirectory.value / "main/assets/pdf", crossTarget.value / GuideStaticFilesDir / "WebContent/assets/pdf")
      IO.copyDirectory(sourceDirectory.value / "main/assets/images", crossTarget.value / GuideStaticFilesDir / "WebContent/assets/images")
      IO.copyDirectory(sourceDirectory.value / "main/assets/svg", crossTarget.value / GuideStaticFilesDir / "WebContent/assets/svg")
      IO.copyDirectory(sourceDirectory.value / "main/assets/prism", crossTarget.value / GuideStaticFilesDir / "WebContent/assets/prism")
      compileStaticsGuideForRelease.value
      (crossTarget.value / GuideStaticFilesDir).***.get
    },

    artifactPath in(Compile, fastOptJS) :=
      (crossTarget in(Compile, fastOptJS)).value / GuideStaticFilesDir / "WebContent" / "scripts" / "frontend-impl-fast.js",
    artifactPath in(Compile, fullOptJS) :=
      (crossTarget in(Compile, fullOptJS)).value / GuideStaticFilesDir / "WebContent" / "scripts" / "frontend-impl.js",
    artifactPath in(Compile, packageJSDependencies) :=
      (crossTarget in(Compile, packageJSDependencies)).value / GuideStaticFilesDir / "WebContent" / "scripts" / "frontend-deps-fast.js",
    artifactPath in(Compile, packageMinifiedJSDependencies) :=
      (crossTarget in(Compile, packageMinifiedJSDependencies)).value / GuideStaticFilesDir / "WebContent" / "scripts" / "frontend-deps.js",
    artifactPath in(Compile, packageScalaJSLauncher) :=
      (crossTarget in(Compile, packageScalaJSLauncher)).value / GuideStaticFilesDir / "WebContent" / "scripts" / "frontend-init.js",

    requiresDOM in Test := true,
    persistLauncher in Test := false,
    scalaJSUseRhino in Test := false
  )

lazy val homepage = project.in(file("homepage")).enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= frontendDeps.value,
    jsDependencies ++= frontendJSDeps.value,
    jsDependencies ++= homepageJSDeps.value,
    persistLauncher in Compile := true,

    compile <<= (compile in Compile),
    compileStaticsHomepage := {
      IO.copyDirectory(sourceDirectory.value / "main/assets/fonts", crossTarget.value / HomepageStaticFilesDir / "WebContent/assets/fonts")
      IO.copyDirectory(sourceDirectory.value / "main/assets/pdf", crossTarget.value / HomepageStaticFilesDir / "WebContent/assets/pdf")
      IO.copyDirectory(sourceDirectory.value / "main/assets/images", crossTarget.value / HomepageStaticFilesDir / "WebContent/assets/images")
      IO.copyDirectory(sourceDirectory.value / "main/assets/svg", crossTarget.value / HomepageStaticFilesDir / "WebContent/assets/svg")
      IO.copyDirectory(sourceDirectory.value / "main/assets/prism", crossTarget.value / HomepageStaticFilesDir / "WebContent/assets/prism")
      IO.copyDirectory(sourceDirectory.value / "main/assets/scrollbar", crossTarget.value / HomepageStaticFilesDir / "WebContent/assets/scrollbar")
      IO.copyDirectory(sourceDirectory.value / "main/assets/svg4everybody", crossTarget.value / HomepageStaticFilesDir / "WebContent/assets/svg4everybody")
      compileStaticsHomepageForRelease.value
      (crossTarget.value / HomepageStaticFilesDir).***.get
    },
    compileStaticsHomepage <<= compileStaticsHomepage.dependsOn(compile in Compile),

    artifactPath in(Compile, fastOptJS) :=
      (crossTarget in(Compile, fastOptJS)).value / HomepageStaticFilesDir / "WebContent" / "scripts" / "frontend-impl-fast.js",
    artifactPath in(Compile, fullOptJS) :=
      (crossTarget in(Compile, fullOptJS)).value / HomepageStaticFilesDir / "WebContent" / "scripts" / "frontend-impl.js",
    artifactPath in(Compile, packageJSDependencies) :=
      (crossTarget in(Compile, packageJSDependencies)).value / HomepageStaticFilesDir / "WebContent" / "scripts" / "frontend-deps-fast.js",
    artifactPath in(Compile, packageMinifiedJSDependencies) :=
      (crossTarget in(Compile, packageMinifiedJSDependencies)).value / HomepageStaticFilesDir / "WebContent" / "scripts" / "frontend-deps.js",
    artifactPath in(Compile, packageScalaJSLauncher) :=
      (crossTarget in(Compile, packageScalaJSLauncher)).value / HomepageStaticFilesDir / "WebContent" / "scripts" / "frontend-init.js"
  )

lazy val selenium = project.in(file("selenium"))
  .dependsOn(backend)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= seleniumDeps.value,
    libraryDependencies ++= testDeps.value,

    parallelExecution := false
  )