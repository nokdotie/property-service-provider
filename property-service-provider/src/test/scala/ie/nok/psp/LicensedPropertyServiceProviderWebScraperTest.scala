package ie.nok.psp

import org.scalatest.funsuite.AnyFunSuite

class LicensedPropertyServiceProviderWebScraperTest extends AnyFunSuite {

  ignore("getAll should scrape all data from web") {
    val actual = LicensedPropertyServiceProviderScraper.webScraper.getAll
    actual.fold(
      throwable => fail(throwable.getLocalizedMessage),
      providers =>
        assert(providers.size > 5000)
        assert(providers.exists(_.licenseStatus == LicenceStatus.NotAuthorised))
        assert(providers.exists(_.licenseStatus == LicenceStatus.PendingRenewal))
        assert(providers.forall(_.address.nonEmpty))
    )
  }
}
