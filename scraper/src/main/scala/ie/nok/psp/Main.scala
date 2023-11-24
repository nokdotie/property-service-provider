package ie.nok.psp
import ie.nok.env.Environment
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {

  // scraper main app
  private val bucket: ZIO[Any, Throwable, String] =
    Environment.get
      .map {
        case Environment.Production => "nok-ie"
        case Environment.Other      => "nok-ie-dev"
      }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = ???
}
