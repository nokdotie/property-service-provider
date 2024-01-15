package ie.nok.psp.store
import ie.nok.psp.{LicensedPropertyServiceProvider, LicensedPropertyServiceProviderScraper}
import zio.ZLayer

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

  lazy val layer: ZLayer[Any, Throwable, LicensedPropertyServiceProviderStore] =
    ZLayer.succeed(LicensedPropertyServiceProviderStore.fromScraper)

  lazy val fromScraper: LicensedPropertyServiceProviderStore = LicensedPropertyServiceProviderStoreImpl(LicensedPropertyServiceProviderScraper.getAll)
  lazy val fromGoogleStore: LicensedPropertyServiceProviderStore = LicensedPropertyServiceProviderStoreImpl(
    LicensedPropertyServiceProviderGoogleStore(fromScraper).getAll
  )
}
