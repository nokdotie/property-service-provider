package ie.nok.psp.store
import munit.FunSuite

import scala.util.Try

class LicensedPropertyServiceProviderGoogleStoreTest extends FunSuite {

  private lazy val store = LicensedPropertyServiceProviderGoogleStore.instance

  test("saveAll providers to google store".ignore) {
    Try {
      store.saveAll
    } fold (
      _ => fail("Error in saveAll"),
      _ => assert(true)
    )
  }
}
