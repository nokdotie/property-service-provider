import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneOffset}

ThisBuild / scalaVersion := "3.3.1"
ThisBuild / organization := "ie.nok"
ThisBuild / version := DateTimeFormatter
  .ofPattern("yyyyMMdd.HHmmss.n")
  .withZone(ZoneOffset.UTC)
  .format(Instant.now())

lazy val root = project
  .in(file("."))
  .aggregate(
    `property-service-provider`,
    scraper
  )

lazy val `property-service-provider` = project
  .settings(
    githubOwner      := "nokdotie",
    githubRepository := "property-service-provider",
    resolvers += Resolver.githubPackages("nokdotie"),
    libraryDependencies ++= List(
      "dev.zio"       %% "zio"              % "2.0.21",
      "dev.zio"       %% "zio-streams"      % "2.0.21",
      "dev.zio"       %% "zio-http"         % "0.0.5",
      "org.jsoup"      % "jsoup"            % "1.16.2",
      "ie.nok"        %% "scala-libraries"  % "20231012.185102.393149873",
      "org.scalameta" %% "munit"            % "0.7.29" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test
    )
  )

lazy val scraper = project
  .dependsOn(`property-service-provider` % "compile->compile;test->test")
