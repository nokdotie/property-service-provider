package ie.nok.psp.store
import ie.nok.psp.{LicensedPropertyServiceProvider, LicensedPropertyServiceProviders}

sealed trait LicensedPropertyServiceProviderStore {

  def getAll: List[LicensedPropertyServiceProvider]

  def getByLicenseNumber(licenseNumber: String): Option[LicensedPropertyServiceProvider]
}

class LicensedPropertyServiceProviderStoreImpl(cache: List[LicensedPropertyServiceProvider]) extends LicensedPropertyServiceProviderStore {

  override def getAll: List[LicensedPropertyServiceProvider] = cache

  override def getByLicenseNumber(licenseNumber: String): Option[LicensedPropertyServiceProvider] =
    cache.find(_.licenseNumber == licenseNumber)

}

object LicensedPropertyServiceProviderStore {

  val fromMemory: LicensedPropertyServiceProviderStore = LicensedPropertyServiceProviderStoreImpl(LicensedPropertyServiceProviders.getAll)
}
