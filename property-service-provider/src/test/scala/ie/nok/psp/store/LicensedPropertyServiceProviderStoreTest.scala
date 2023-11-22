package ie.nok.psp.store

import ie.nok.psp.ClassOfProvider
import munit.FunSuite

class LicensedPropertyServiceProviderStoreTest extends FunSuite {

  private val store = LicensedPropertyServiceProviderStore.fromMemory

  test("get provider by licence number") {
    val licenceNumber = "001015-001006"
    val actual        = store.getByLicenseNumber(licenceNumber)
    assertEquals(actual.map(_.licenseNumber), Some(licenceNumber))
    assertEquals(actual.map(_.classOfProvider), Some(ClassOfProvider.Director))
  }

  test("get agency by licence number") {
    val licenceNumber = "001128"
    val actual        = store.getByLicenseNumber(licenceNumber)
    assertEquals(actual.map(_.licenseNumber), Some(licenceNumber))
    assertEquals(actual.map(_.classOfProvider), Some(ClassOfProvider.Company))
  }
}
