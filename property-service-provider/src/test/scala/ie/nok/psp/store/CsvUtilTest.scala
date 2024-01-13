package ie.nok.psp.store

import ie.nok.psp.{ClassOfProvider, LicenceStatus, LicenseType, LicensedPropertyServiceProvider}
import munit.FunSuite
import zio.prelude.NonEmptyList

import java.time.LocalDate

class CsvUtilTest extends FunSuite {

  test("csv serialization for LicensedPropertyServiceProvider") {
    val input = LicensedPropertyServiceProvider(
      county = Some("Dublin"),
      licenseNumber = "123124",
      parentLicense = None,
      licenseeDetails = "test",
      address = "Dublin 2",
      tradingName = None, // Some("Sylwester"),
      classOfProvider = ClassOfProvider.Employee,
      licenseExpiry = LocalDate.of(2024, 10, 1),
      licenseTypes = NonEmptyList(LicenseType.A, LicenseType.B, LicenseType.C),
      licenseStatus = LicenceStatus.Permitted,
      additionalInfo = None
    )
    val csv    = CsvUtil.toCsv(input)
    val actual = CsvUtil.fromCsv(csv)
    assertEquals(actual, Some(input))
  }
}
