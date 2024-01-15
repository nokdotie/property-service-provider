package ie.nok.psp
import munit.FunSuite

class LicensedPropertyServiceProviderScraperTest extends FunSuite {

  test("getAll should scrape all data".ignore) {
    val actual = LicensedPropertyServiceProviderScraper.getAll
    actual.foreach(println)
    assert(actual.size > 5000)
    assert(actual.exists(_.licenseStatus == LicenceStatus.NotAuthorised))
    assert(actual.exists(_.licenseStatus == LicenceStatus.PendingRenewal))
    assert(actual.forall(_.address.nonEmpty))
  }
}
