package ie.nok.psp

import org.scalatest.funsuite.AnyFunSuite

class LicensedPropertyServiceProviderHtmlScraperTest extends AnyFunSuite with TestHelper {

  test("getAll should scrape all data from file _20240118") {
    val actual = htmlScraper.getAll
    actual.fold(
      t => fail(t.getLocalizedMessage),
      providers => assert(providers.length === 5927)
    )
  }
}
