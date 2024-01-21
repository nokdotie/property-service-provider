package ie.nok.psp

import ie.nok.psp.utils.StringUtils.toStrOpt
import org.jsoup.nodes.{Document, Element}
import zio.prelude.NonEmptyList
import zio.{ZIO, ZLayer}

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters.*
import scala.util.Try
import scala.util.chaining.*

trait LicensedPropertyServiceProviderParser {
  def parse(doc: Document): List[LicensedPropertyServiceProvider]
}

object LicensedPropertyServiceProviderParserImpl extends LicensedPropertyServiceProviderParser {

  def parse(doc: Document): List[LicensedPropertyServiceProvider] = {

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
      county = str.head.pipe(toStrOpt),
      licenseNumber = licenseNumber,
      parentLicense = parentLicense,
      licenseeDetails = str(2),
      address = str(3),
      tradingName = str(4).pipe(toStrOpt),
      classOfProvider = str(5),
      licenseExpiry = str(6),
      licenseType = str(7),
      additionalInfo = str(8).pipe(toStrOpt)
    )
  }
}

object LicensedPropertyServiceProviderParser {

  lazy val zLicensedPropertyServiceProviderParser: ZIO[Any, Throwable, LicensedPropertyServiceProviderParser] =
    ZIO.succeed(LicensedPropertyServiceProviderParserImpl)

  lazy val layer: ZLayer[Any, Throwable, LicensedPropertyServiceProviderParser] =
    ZLayer.scoped(zLicensedPropertyServiceProviderParser)
}
