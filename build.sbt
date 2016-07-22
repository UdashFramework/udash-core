name := "udash"

version in ThisBuild := "0.4.0-SNAPSHOT"
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

val commonJSSettings = Seq(
  emitSourceMaps in Compile := true,
  persistLauncher in Test := false,
  scalaJSUseRhino in Test := false,
  scalaJSStage in Test := FastOptStage,
  jsDependencies in Test += RuntimeDOM % Test,
  jsEnv in Test := new org.scalajs.jsenv.selenium.SeleniumJSEnv(org.scalajs.jsenv.selenium.Firefox),
  scalacOptions += {
    val localDir = (baseDirectory in ThisBuild).value.toURI.toString
    val githubDir = "https://raw.githubusercontent.com/UdashFramework/udash-core"
    s"-P:scalajs:mapSourceURI:$localDir->$githubDir/v${version.value}/"
  }
)

lazy val udash = project.in(file("."))
  .aggregate(
    `core-macros`, `core-shared-JS`, `core-shared-JVM`, `core-frontend`,
    `rpc-macros`, `rpc-shared-JS`, `rpc-shared-JVM`, `rpc-frontend`, `rpc-backend`,
    `rest-macros`, `rest-shared-JS`, `rest-shared-JVM`,
    `i18n-shared-JS`, `i18n-shared-JVM`, `i18n-frontend`, `i18n-backend`,
    `bootstrap`
  )
  .settings(publishArtifact := false)

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
  .jsSettings(commonJSSettings:_*)

lazy val `core-shared-JVM` = `core-shared`.jvm
lazy val `core-shared-JS` = `core-shared`.js

lazy val `core-frontend` = project.in(file("core/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-shared-JS` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)
  .settings(
    emitSourceMaps in Compile := true,
    libraryDependencies ++= coreFrontendDeps.value,
    persistLauncher in Compile := true,
    publishedJS <<= Def.taskDyn {
      if (isSnapshot.value) Def.task((fastOptJS in Compile).value) else Def.task((fullOptJS in Compile).value)
    },
    publishedJSDependencies <<= Def.taskDyn {
      if (isSnapshot.value) Def.task((packageJSDependencies in Compile).value) else Def.task((packageMinifiedJSDependencies in Compile).value)
    }
  )

lazy val `rpc-macros` = project.in(file("rpc/macros"))
  .dependsOn(`core-macros`)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.avsystem.commons" %% "commons-macros" % avsCommonsVersion
    )
  )

lazy val `rpc-shared` = crossProject.crossType(CrossType.Full).in(file("rpc/shared"))
  .configure(_.dependsOn(`core-shared` % CompileAndTest))
  .jsConfigure(_.dependsOn(`rpc-macros`))
  .jvmConfigure(_.dependsOn(`rpc-macros`))
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= rpcCrossDeps.value,
    libraryDependencies ++= rpcCrossTestDeps.value
  )
  .jsSettings(commonJSSettings:_*)
  .jvmSettings(
    libraryDependencies ++= rpcSharedJVMDeps.value
  )

lazy val `rpc-shared-JVM` = `rpc-shared`.jvm
lazy val `rpc-shared-JS` = `rpc-shared`.js

lazy val `rpc-backend` = project.in(file("rpc/backend"))
  .dependsOn(`rpc-shared-JVM` % CompileAndTest)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= rpcBackendDeps.value
  )

lazy val `rpc-frontend` = project.in(file("rpc/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`rpc-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)
  .settings(
    jsDependencies ++= rpcFrontendJsDeps.value
  )

lazy val `rest-macros` = project.in(file("rest/macros"))
  .dependsOn(`rpc-macros`)
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.avsystem.commons" %% "commons-macros" % avsCommonsVersion
    )
  )

lazy val `rest-shared` = crossProject.crossType(CrossType.Pure).in(file("rest/shared"))
  .configure(_.dependsOn(`rpc-shared` % CompileAndTest))
  .jsConfigure(_.dependsOn(`rest-macros`))
  .jvmConfigure(_.dependsOn(`rest-macros`))
  .settings(commonSettings: _*).settings(
    libraryDependencies ++= restCrossDeps.value
  )
  .jsSettings(commonJSSettings:_*)

lazy val `rest-shared-JVM` = `rest-shared`.jvm
lazy val `rest-shared-JS` = `rest-shared`.js

lazy val `i18n-shared` = crossProject.crossType(CrossType.Pure).in(file("i18n/shared"))
  .configure(_.dependsOn(`core-shared`, `rpc-shared` % CompileAndTest))
  .settings(commonSettings: _*)
  .jsSettings(commonJSSettings:_*)

lazy val `i18n-shared-JVM` = `i18n-shared`.jvm
lazy val `i18n-shared-JS` = `i18n-shared`.js

lazy val `i18n-backend` = project.in(file("i18n/backend"))
  .dependsOn(`i18n-shared-JVM` % CompileAndTest, `rpc-backend` % CompileAndTest)
  .settings(commonSettings: _*)

lazy val `i18n-frontend` = project.in(file("i18n/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`i18n-shared-JS` % CompileAndTest, `core-frontend` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)
  .settings(
    jsDependencies += RuntimeDOM % Test
  )

lazy val `bootstrap` = project.in(file("bootstrap/frontend")).enablePlugins(ScalaJSPlugin)
  .dependsOn(`core-frontend` % CompileAndTest)
  .settings(commonSettings: _*)
  .settings(commonJSSettings: _*)
  .settings(
    libraryDependencies ++= bootstrapFrontendDeps.value,
    jsDependencies ++= bootstrapFrontendJsDeps.value
  )
