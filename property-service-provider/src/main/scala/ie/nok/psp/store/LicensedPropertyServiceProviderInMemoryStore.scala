package ie.nok.psp.store
import ie.nok.psp.{LicensedPropertyServiceProvider, LicensedPropertyServiceProviders}

object LicensedPropertyServiceProviderInMemoryStore extends LicensedPropertyServiceProviderStore {

  private val cache = LicensedPropertyServiceProviders.getAll

  override def getAll: List[LicensedPropertyServiceProvider] = cache
  override def getByLicenseNumber(licenseNumber: String): Option[LicensedPropertyServiceProvider] =
    cache.find(_.licenseNumber == licenseNumber)
}
