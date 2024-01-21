package ie.nok.psp

import org.jsoup.Jsoup
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Try

class LicensedPropertyServiceProviderParserTest extends AnyFunSuite with TestHelper {

  test("parser should parse html file") {
    Try {
      val doc = Jsoup.parse(html)
      LicensedPropertyServiceProviderParserImpl.parse(doc)
    }.fold(
      t => fail(t.getLocalizedMessage),
      providers =>
        assert(providers.length === 5927)
        assert(providers.count(_.licenseStatus == LicenceStatus.NotAuthorised) === 72)
        assert(providers.count(_.licenseStatus == LicenceStatus.PendingRenewal) === 2591)
        assert(providers.count(_.licenseStatus == LicenceStatus.Permitted) === 3264)
        assert(providers.count(_.licenseStatus == LicenceStatus.Suspended) === 0)
        assert(providers.count(_.classOfProvider == ClassOfProvider.Company) === 1155)
        assert(providers.count(_.classOfProvider == ClassOfProvider.PropertyServicesEmployer) === 234)
        assert(providers.count(!_.licenseNumber.contains("-")) === 1798) // independent agencies
        assert(providers.forall(_.address.nonEmpty))
    )
  }
}
