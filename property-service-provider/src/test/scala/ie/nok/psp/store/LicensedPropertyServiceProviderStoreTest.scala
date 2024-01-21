package ie.nok.psp.store

import ie.nok.psp.{ClassOfProvider, TestHelper}
import org.scalatest.funsuite.AnyFunSuite

class LicensedPropertyServiceProviderStoreTest extends AnyFunSuite with TestHelper {

  private lazy val triedStoreFromHtmlScraper = htmlScraper.getAll.map(LicensedPropertyServiceProviderStoreImpl(_))

  test("storeFromHtmlScraper - get all providers") {
    triedStoreFromHtmlScraper.fold(
      t => fail(t.getLocalizedMessage),
      store => assert(store.getAll.length === 5927)
    )
  }

  test("storeFromHtmlScraper - get provider by licence number") {
    val licenceNumber = "001015-001006"
    triedStoreFromHtmlScraper.fold(
      t => fail(t.getLocalizedMessage),
      store =>
        val actual = store.getByLicenseNumber(licenceNumber)
        assert(actual.map(_.licenseNumber) === Some(licenceNumber))
        assert(actual.map(_.classOfProvider) === Some(ClassOfProvider.Director))
    )
  }

  test("storeFromHtmlScraper - get agency by licence number") {
    val licenceNumber = "001128"
    triedStoreFromHtmlScraper.fold(
      t => fail(t.getLocalizedMessage),
      store =>
        val actual = store.getByLicenseNumber(licenceNumber)
        assert(actual.map(_.licenseNumber) === Some(licenceNumber))
        assert(actual.map(_.classOfProvider) === Some(ClassOfProvider.Company))
    )
  }

//  test("storeFromGoogleStore - get all providers".ignore) {
//    val actual = storeFromGoogleStore.getAll
//    assert(actual.length > 5000)
//  }
}
