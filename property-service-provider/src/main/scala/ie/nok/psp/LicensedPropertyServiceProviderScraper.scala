package ie.nok.psp

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import zio.*

import scala.util.Try

sealed trait LicensedPropertyServiceProviderScraper {

  val parser: LicensedPropertyServiceProviderParser
  def getAll: Try[List[LicensedPropertyServiceProvider]]
}

class LicensedPropertyServiceProviderWebScraper(override val parser: LicensedPropertyServiceProviderParser) extends LicensedPropertyServiceProviderScraper {

  private val url = "https://www.psr.ie/psra-registers/register-of-licensed-property-services-providers/"

  def getAll: Try[List[LicensedPropertyServiceProvider]] = Try {
    val doc: Document = Jsoup
      .connect(url)
      .timeout(5000)  // increased timeout just in case
      .maxBodySize(0) // infinite body size, default 2MB is too small
      .get()

    parser.parse(doc)
  }
}

class LicensedPropertyServiceProviderHtmlScraper(override val parser: LicensedPropertyServiceProviderParser, html: String)
    extends LicensedPropertyServiceProviderScraper {

  def getAll: Try[List[LicensedPropertyServiceProvider]] = Try {
    val doc: Document = Jsoup.parse(html)
    parser.parse(doc)
  }
}

object LicensedPropertyServiceProviderScraper {

  lazy val webScraper: LicensedPropertyServiceProviderWebScraper = LicensedPropertyServiceProviderWebScraper(LicensedPropertyServiceProviderParserImpl)

  def htmlScraper(html: String): LicensedPropertyServiceProviderHtmlScraper =
    LicensedPropertyServiceProviderHtmlScraper(LicensedPropertyServiceProviderParserImpl, html)

  lazy val zWebScraper: ZIO[LicensedPropertyServiceProviderParser, Throwable, LicensedPropertyServiceProviderWebScraper] =
    ZIO.service[LicensedPropertyServiceProviderParser].map(LicensedPropertyServiceProviderWebScraper(_))

  lazy val layer: ZLayer[LicensedPropertyServiceProviderParser, Throwable, LicensedPropertyServiceProviderScraper] = ZLayer.scoped(zWebScraper)
}
