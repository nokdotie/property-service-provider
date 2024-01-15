package ie.nok.psp

import ie.nok.psp.store.LicensedPropertyServiceProviderGoogleStore
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {

  // scraper main app
  private val app: ZIO[LicensedPropertyServiceProviderGoogleStore, Throwable, Boolean] =
    ZIO
      .serviceWithZIO[LicensedPropertyServiceProviderGoogleStore] { licensedPropertyServiceProviderGoogleStore =>
        ZIO.attempt(licensedPropertyServiceProviderGoogleStore.saveAll)
      }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = app
    .provide(LicensedPropertyServiceProviderGoogleStore.live)
}
