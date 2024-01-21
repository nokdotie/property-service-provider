package ie.nok.psp.store
import ie.nok.psp.{LicensedPropertyServiceProvider, LicensedPropertyServiceProviderScraper}
import zio.{ZIO, ZLayer}

trait LicensedPropertyServiceProviderStore {

  def getAll: List[LicensedPropertyServiceProvider]

  def getByLicenseNumber(licenseNumber: String): Option[LicensedPropertyServiceProvider]
}

class LicensedPropertyServiceProviderStoreImpl(cache: List[LicensedPropertyServiceProvider]) extends LicensedPropertyServiceProviderStore {

  override def getAll: List[LicensedPropertyServiceProvider] = cache

  override def getByLicenseNumber(licenseNumber: String): Option[LicensedPropertyServiceProvider] =
    cache.find(_.licenseNumber == licenseNumber)

}

object LicensedPropertyServiceProviderStore {

  lazy val zLicensedPropertyServiceProviderStore: ZIO[LicensedPropertyServiceProviderScraper, Throwable, LicensedPropertyServiceProviderStore] =
    ZIO
      .service[LicensedPropertyServiceProviderScraper]
      .flatMap { scraper =>
        ZIO
          .fromTry(scraper.getAll)
          .map(LicensedPropertyServiceProviderStoreImpl(_))
      }

  lazy val layer: ZLayer[LicensedPropertyServiceProviderScraper, Throwable, LicensedPropertyServiceProviderStore] =
    ZLayer.scoped(zLicensedPropertyServiceProviderStore)
}
