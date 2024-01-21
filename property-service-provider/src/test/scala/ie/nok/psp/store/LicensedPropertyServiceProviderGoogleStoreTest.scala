package ie.nok.psp.store

import com.google.cloud.storage.*
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper
import ie.nok.psp.TestHelper
import org.scalatest.funsuite.AnyFunSuite

class LicensedPropertyServiceProviderGoogleStoreTest extends AnyFunSuite with TestHelper {

  private lazy val googleCloudStorageLocal: Storage = LocalStorageHelper.getOptions.getService
  private lazy val triedStoreFromHtmlScraper        = htmlScraper.getAll.map(LicensedPropertyServiceProviderStoreImpl(_))

  private lazy val triedPropertyServiceProviderGoogleStore =
    triedStoreFromHtmlScraper.map(LicensedPropertyServiceProviderGoogleStore(googleCloudStorageLocal, _))

  test("saveAll providers to google store") {
    triedPropertyServiceProviderGoogleStore
      .map(_.saveAll)
      .fold(
        t => fail(t.getLocalizedMessage),
        _ => assert(true)
      )
  }

  test("getAll providers from google store") {
    triedPropertyServiceProviderGoogleStore.map(_.saveAll)
    triedPropertyServiceProviderGoogleStore
      .map(_.getAll)
      .fold(
        t => fail(t.getLocalizedMessage),
        providers => assert(providers.length === 5927)
      )
  }
}
