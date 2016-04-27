name := "udash"

version in ThisBuild := "0.2.0"
scalaVersion in ThisBuild := versionOfScala
organization in ThisBuild := "io.udash"
cancelable in Global := true
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
  moduleName := "udash-" + moduleName.value,
  libraryDependencies ++= compilerPlugins.value,
  libraryDependencies ++= commonDeps.value,
  libraryDependencies ++= commonTestDeps.value
)

lazy val udash = project.in(file("."))
  .aggregate(
    `core-macros`, `core-shared-JS`, `core-shared-JVM`, `core-frontend`,
    `rpc-macros`, `rpc-shared-JS`, `rpc-shared-JVM`, `rpc-frontend`, `rpc-backend`
  )
  .settings(publishArtifact := false)

/** Project containing implementations of macros. Macros can be used in both JS and JVM code. */
lazy val `core-macros` = project.in(file("core/macros"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
  )

lazy val `core-shared` = crossProject.crossType(CrossType.Pure).in(file("core/shared"))
  .jsConfigure(_.dependsOn(`core-macros`))
  .jvmConfigure(_.dependsOn(`core-macros`))
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= coreCrossDeps.value
  )
  .jsSettings(
    emitSourceMaps in Compile := true,
    persistLauncher in Test := false,
    scalaJSStage in Test := FastOptStage,
    jsDependencies in Test += RuntimeDOM % Test
  )

lazy val `core-shared-JVM` = `core-shared`.jvm
lazy val `core-shared-JS` = `core-shared`.js

lazy val `core-frontend` = project.in(file("core/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-shared-JS` % "test->test;compile->compile")
  .settings(commonSettings: _*).settings(
    emitSourceMaps in Compile := true,
    libraryDependencies ++= coreFrontendDeps.value,
    jsDependencies += RuntimeDOM % Test,
    persistLauncher in Compile := true,
    publishedJS <<= Def.taskDyn {
      if (isSnapshot.value) Def.task((fastOptJS in Compile).value) else Def.task((fullOptJS in Compile).value)
    },
    publishedJSDependencies <<= Def.taskDyn {
      if (isSnapshot.value) Def.task((packageJSDependencies in Compile).value) else Def.task((packageMinifiedJSDependencies in Compile).value)
    },

    requiresDOM in Test := true,
    persistLauncher in Test := false,
    scalaJSUseRhino in Test := false
  )

/** Project containing implementations of macros. Macros can be used in both JS and JVM code. */
lazy val `rpc-macros` = project.in(file("rpc/macros"))
  .dependsOn(`core-macros`)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.avsystem.commons" %% "commons-macros" % avsCommonsVersion
    )
  )

/** Cross project containing code compiled to both JS and JVM. */
lazy val `rpc-shared` = crossProject.crossType(CrossType.Full).in(file("rpc/shared"))
  .jsConfigure(_.dependsOn(`rpc-macros`))
  .jvmConfigure(_.dependsOn(`rpc-macros`))
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= rpcCrossDeps.value,
    libraryDependencies ++= rpcCrossTestDeps.value
  )
  .jsSettings(
    persistLauncher in Test := false,
    emitSourceMaps in Test := true,
    scalaJSStage in Test := FastOptStage,
    jsDependencies in Test += RuntimeDOM % Test
  )
  .jvmSettings(
    libraryDependencies ++= rpcSharedJVMDeps.value
  )

lazy val `rpc-shared-JVM` = `rpc-shared`.jvm
lazy val `rpc-shared-JS` = `rpc-shared`.js

lazy val `rpc-backend` = project.in(file("rpc/backend"))
  .dependsOn(`rpc-shared-JVM` % "test->test;compile->compile")
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= rpcBackendDeps.value,
    libraryDependencies ++= rpcBackendTestDeps.value
  )

lazy val `rpc-frontend` = project.in(file("rpc/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`rpc-shared-JS` % "test->test;compile->compile", `core-frontend`)
  .settings(commonSettings: _*).settings(
    jsDependencies ++= rpcFrontendJsDeps.value,
  jsDependencies += RuntimeDOM % Test,

  requiresDOM in Test := true,
  persistLauncher in Test := false,
  scalaJSUseRhino in Test := false,
  emitSourceMaps in Test := true
)

lazy val udash = project.in(file("."))
  .aggregate(`i18n-shared-JS`, `i18n-shared-JVM`, `i18n-frontend`, `i18n-backend`)
  .settings(publishArtifact := false)

lazy val `i18n-shared` = crossProject.crossType(CrossType.Pure).in(file("i18n/shared"))
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= crossDeps.value,
    libraryDependencies ++= crossTestDeps.value
  )
  .jsSettings(
    jsDependencies in Test += RuntimeDOM % Test,
    persistLauncher in Test := false,
    scalaJSStage in Test := FastOptStage,
    emitSourceMaps in Test := true
  )

lazy val `i18n-shared-JVM` = `i18n-shared`.jvm
lazy val `i18n-shared-JS` = `i18n-shared`.js

lazy val `i18n-backend` = project.in(file("i18n/backend"))
  .dependsOn(`i18n-shared-JVM` % "test->test;compile->compile")
  .settings(commonSettings: _*)

lazy val `i18n-frontend` = project.in(file("i18n/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`i18n-shared-JS` % "test->test;compile->compile")
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= frontendDeps.value,
    jsDependencies += RuntimeDOM % Test,

    requiresDOM in Test := true,
    persistLauncher in Test := false,
    scalaJSUseRhino in Test := false,
    emitSourceMaps in Test := true
  )
