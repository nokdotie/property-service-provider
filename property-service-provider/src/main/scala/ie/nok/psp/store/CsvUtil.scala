package ie.nok.psp.store

import ie.nok.psp.*
import zio.prelude.NonEmptyList

import java.time.LocalDate
import scala.util.Try

object CsvUtil {

  private val separator       = ','
  private val valuesSeparator = '|'

  def header(l: LicensedPropertyServiceProvider): String =
    l.productElementNames.mkString("", separator.toString, "\n")
  def toCsv(l: LicensedPropertyServiceProvider): String = {
    List(
      l.county.getOrElse(""),
      l.licenseNumber,
      l.parentLicense.getOrElse(""),
      l.licenseeDetails,
      l.address,
      l.tradingName.getOrElse(""),
      l.classOfProvider,
      l.licenseExpiry.toString,
      l.licenseTypes.mkString(valuesSeparator.toString),
      l.licenseStatus,
      l.additionalInfo.getOrElse("")
    ).mkString("", separator.toString, "\n")
  }

  def fromCsv(s: String): Option[LicensedPropertyServiceProvider] = {
    val attributes: List[String] = s.split(separator.toString, -2).toList
    val licenseTypes             = attributes(8).split(valuesSeparator).toList.map(LicenseType.valueOf)
    Try {
      LicensedPropertyServiceProvider(
        county = attributes.head.toStrOpt,
        licenseNumber = attributes(1),
        parentLicense = attributes(2).toStrOpt,
        licenseeDetails = attributes(3),
        address = attributes(4),
        tradingName = attributes(5).toStrOpt,
        classOfProvider = ClassOfProvider.valueOf(attributes(6)),
        licenseExpiry = LocalDate.parse(attributes(7)),
        licenseTypes = NonEmptyList.fromIterable(licenseTypes.head, licenseTypes.tail),
        licenseStatus = LicenceStatus.valueOf(attributes(9)),
        additionalInfo = attributes(10).toStrOpt
      )
    }.toOption
  }
}
