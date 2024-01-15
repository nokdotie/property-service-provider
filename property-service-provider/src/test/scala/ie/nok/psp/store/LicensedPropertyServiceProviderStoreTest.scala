package ie.nok.psp.store

import ie.nok.psp.ClassOfProvider
import munit.FunSuite

class LicensedPropertyServiceProviderStoreTest extends FunSuite {

  private val storeFromScraper     = LicensedPropertyServiceProviderStore.fromScraper
  private val storeFromGoogleStore = LicensedPropertyServiceProviderStore.fromGoogleStore

  test("storeFromScraper - get all providers".ignore) {
    val actual = storeFromScraper.getAll
    assert(actual.length > 5000)
  }

  test("storeFromScraper - get provider by licence number".ignore) {
    val licenceNumber = "001015-001006"
    val actual        = storeFromScraper.getByLicenseNumber(licenceNumber)
    assertEquals(actual.map(_.licenseNumber), Some(licenceNumber))
    assertEquals(actual.map(_.classOfProvider), Some(ClassOfProvider.Director))
  }

  test("storeFromScraper - get agency by licence number".ignore) {
    val licenceNumber = "001128"
    val actual        = storeFromScraper.getByLicenseNumber(licenceNumber)
    assertEquals(actual.map(_.licenseNumber), Some(licenceNumber))
    assertEquals(actual.map(_.classOfProvider), Some(ClassOfProvider.Company))
  }

  test("storeFromGoogleStore - get all providers".ignore) {
    val actual = storeFromGoogleStore.getAll
    assert(actual.length > 5000)
  }
}
