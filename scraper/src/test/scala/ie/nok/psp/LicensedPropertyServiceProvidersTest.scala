package ie.nok.psp
import munit.FunSuite

class LicensedPropertyServiceProvidersTest extends FunSuite {

  test("LicensedPropertyServiceProviders getAll should ") {
    val actual = LicensedPropertyServiceProviders.getAll
    assert(actual.size > 5000)
    assert(actual.exists(_.licenseStatus == LicenceStatus.NotAuthorised))
    assert(actual.exists(_.licenseStatus == LicenceStatus.PendingRenewal))
    assert(actual.forall(_.address.nonEmpty))
  }
}
