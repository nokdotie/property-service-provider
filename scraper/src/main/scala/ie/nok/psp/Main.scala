package ie.nok.psp

import ie.nok.psp.store.{LicensedPropertyServiceProviderGoogleStore, LicensedPropertyServiceProviderStore}
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object Main extends ZIOAppDefault {

  // scraper main app
  private val app: ZIO[LicensedPropertyServiceProviderGoogleStore, Throwable, Boolean] =
    ZIO
      .serviceWithZIO[LicensedPropertyServiceProviderGoogleStore] { licensedPropertyServiceProviderGoogleStore =>
        ZIO.attempt(licensedPropertyServiceProviderGoogleStore.saveAll)
      }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = app
    .provide(
      LicensedPropertyServiceProviderGoogleStore.layer,
      LicensedPropertyServiceProviderGoogleStore.googleStorageLayer,
      LicensedPropertyServiceProviderStore.layer,
      LicensedPropertyServiceProviderScraper.layer,
      LicensedPropertyServiceProviderParser.layer
    )
}
