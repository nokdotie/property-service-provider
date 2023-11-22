package ie.nok.psp.service
import ie.nok.psp.ClassOfProvider
import ie.nok.psp.store.LicensedPropertyServiceProviderInMemoryStore
import munit.FunSuite

class LicensedPropertyServiceProviderServiceImplTest extends FunSuite {

  private val service = LicensedPropertyServiceProviderServiceImpl(LicensedPropertyServiceProviderInMemoryStore)

  test("get provider by licence number") {
    val licenceNumber = "001015-001006"
    val actual        = service.getProviderByLicenseNumber(licenceNumber)
    assertEquals(actual.map(_.licenseNumber), Some(licenceNumber))
    assertEquals(actual.map(_.classOfProvider), Some(ClassOfProvider.Director))
  }

  test("get agency by licence number") {
    val licenceNumber = "001128"
    val actual        = service.getProviderByLicenseNumber(licenceNumber)
    assertEquals(actual.map(_.licenseNumber), Some(licenceNumber))
    assertEquals(actual.map(_.classOfProvider), Some(ClassOfProvider.Company))
  }
}
