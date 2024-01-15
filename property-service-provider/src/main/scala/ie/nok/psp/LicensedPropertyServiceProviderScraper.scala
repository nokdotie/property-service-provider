package ie.nok.psp

import ie.nok.psp.toStrOpt
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import zio.prelude.NonEmptyList

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters.*
import scala.util.Try

object LicensedPropertyServiceProviderScraper {

  def getAll: List[LicensedPropertyServiceProvider] = {
    val url = "https://www.psr.ie/psra-registers/register-of-licensed-property-services-providers/"

    val doc: Document = Jsoup
      .connect(url)
      .timeout(5000)  // increased timeout just in case
      .maxBodySize(0) // infinite body size, default 2MB is too small
      .get()

    val tableRows: List[Element] = doc
      .select("tbody")
      .select("tr")
      .iterator()
      .asScala
      .toList

    val rowElements = tableRows.map { tr =>
      tr.select("td").iterator().asScala.toList
    }

    val providersRaw: List[LicensedPropertyServiceProviderRaw] = rowElements
      .map(e => e.map(_.text()))
      .flatMap(tryParse(_).toOption)

    providersRaw.flatMap(tryParse(_).toOption)
  }

  private case class LicensedPropertyServiceProviderRaw(
      county: Option[String],
      licenseNumber: String,
      parentLicense: Option[String],
      licenseeDetails: String, // name
      address: String,
      tradingName: Option[String],
      classOfProvider: String,
      licenseExpiry: String,
      licenseType: String,
      additionalInfo: Option[String]
  )

  private def tryParse(raw: LicensedPropertyServiceProviderRaw): Try[LicensedPropertyServiceProvider] = {
    Try {
      val dateStr       = raw.licenseExpiry.trim.replace("**", "")
      val licenseExpiry = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
      val licenseStatus = raw.additionalInfo match {
        case Some("Cannot Provide Services at this Time") => LicenceStatus.NotAuthorised
        case other =>
          if (raw.licenseExpiry.contains("**")) LicenceStatus.PendingRenewal
          else LicenceStatus.Permitted

      }
      val licenseTypes = raw.licenseType
        .split(" ")
        .map(_.trim.replace("[", "").replace("]", ""))
        .map(LicenseType.valueOf)
        .toList

      LicensedPropertyServiceProvider(
        county = raw.county,
        licenseNumber = raw.licenseNumber,
        parentLicense = raw.parentLicense,
        licenseeDetails = raw.licenseeDetails,
        address = raw.address,
        tradingName = raw.tradingName,
        classOfProvider = ClassOfProvider.valueOf(raw.classOfProvider.filterNot(_.isWhitespace)),
        licenseExpiry = licenseExpiry,
        licenseTypes = NonEmptyList.fromIterable(licenseTypes.head, licenseTypes.tail),
        licenseStatus = licenseStatus,
        additionalInfo = raw.additionalInfo
      )
    }
  }

  private def tryParse(str: List[String]): Try[LicensedPropertyServiceProviderRaw] = Try {
    val licenseNumber = str(1)
    val parentLicense = licenseNumber.split("-").headOption match {
      case Some("999999")                          => None // assigned to the default parent
      case Some(parent) if parent == licenseNumber => None
      case other                                   => other
    }
    LicensedPropertyServiceProviderRaw(
      county = str.head.toStrOpt,
      licenseNumber = licenseNumber,
      parentLicense = parentLicense,
      licenseeDetails = str(2),
      address = str(3),
      tradingName = str(4).toStrOpt,
      classOfProvider = str(5),
      licenseExpiry = str(6),
      licenseType = str(7),
      additionalInfo = str(8).toStrOpt
    )
  }
}
