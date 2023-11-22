package ie.nok.psp.store
import ie.nok.psp.LicensedPropertyServiceProvider

trait LicensedPropertyServiceProviderStore {

  def getAll: List[LicensedPropertyServiceProvider]

  def getByLicenseNumber(licenseNumber: String): Option[LicensedPropertyServiceProvider]
}
