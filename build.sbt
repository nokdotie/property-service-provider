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
    `property-service-provider`
  )

lazy val `property-service-provider` = project
  .settings(
    githubOwner := "nokdotie",
    githubRepository := "property-service-provider",
    resolvers += Resolver.githubPackages("nokdotie")
  )
