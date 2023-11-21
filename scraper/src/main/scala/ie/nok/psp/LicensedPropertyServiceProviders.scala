package ie.nok.psp

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters.*
import scala.util.Try

object LicensedPropertyServiceProviders extends App {

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

    val providersRaw = rowElements
      .map(e => e.map(_.text()))
      .flatMap(tryParse)

    providersRaw.map(tryParse)
  }
  
  private def tryParse(raw: LicensedPropertyServiceProviderRaw): LicensedPropertyServiceProvider = {
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
      licenseTypes = licenseTypes,
      licenseStatus = licenseStatus,
      additionalInfo = raw.additionalInfo
    )
  }

  extension (s: String) {
    private def toStrOpt: Option[String] = s.trim match {
      case ""    => None
      case other => Some(other)
    }
  }

  private def tryParse(str: List[String]): Option[LicensedPropertyServiceProviderRaw] = {
    Try {
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
    }.toOption
  }
}
