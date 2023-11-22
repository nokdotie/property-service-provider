package ie.nok.psp.service

import ie.nok.psp.LicensedPropertyServiceProvider
import ie.nok.psp.store.LicensedPropertyServiceProviderStore

trait LicensedPropertyServiceProviderService {

  def getAllProviders: List[LicensedPropertyServiceProvider]
  def getProviderByLicenseNumber(licenseNumber: String): Option[LicensedPropertyServiceProvider]
}

class LicensedPropertyServiceProviderServiceImpl(store: LicensedPropertyServiceProviderStore) extends LicensedPropertyServiceProviderService {

  override def getProviderByLicenseNumber(licenseNumber: String): Option[LicensedPropertyServiceProvider] =
    store.getByLicenseNumber(licenseNumber)

  override def getAllProviders: List[LicensedPropertyServiceProvider] = store.getAll
}
