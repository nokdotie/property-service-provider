package ie.nok.psp

import scala.io.{BufferedSource, Source}

trait TestHelper {

  protected val source: BufferedSource                                  = Source.fromResource("Register of Licensed Property Services Providers_20240118.html")
  protected val html: String                                            = source.getLines().mkString
  protected val htmlScraper: LicensedPropertyServiceProviderHtmlScraper = LicensedPropertyServiceProviderScraper.htmlScraper(html)
}
