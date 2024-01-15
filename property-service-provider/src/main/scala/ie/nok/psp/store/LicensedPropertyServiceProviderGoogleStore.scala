package ie.nok.psp.store

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Storage.{BlobSourceOption, BlobWriteOption}
import com.google.cloud.storage.{BlobInfo, Storage, StorageOptions}
import ie.nok.psp.LicensedPropertyServiceProvider
import zio.{ZIO, ZLayer}

import java.io.FileWriter
import java.nio.file.{Files, Path}
import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneOffset}
import scala.util.chaining.scalaUtilChainingOps
import scala.util.{Failure, Success, Try}

class LicensedPropertyServiceProviderGoogleStore(store: LicensedPropertyServiceProviderStore) {

  private val blobNameLatest: String = "providers/latest.csv"

  private val bucket: String = Option(System.getenv("ENV")) match {
    case Some("production") => "nok-ie"
    case _                  => "nok-ie-dev"
  }

  private def blobNameVersioned: String =
    DateTimeFormatter
      .ofPattern("yyyyMMddHHmmss")
      .withZone(ZoneOffset.UTC)
      .format(Instant.now)
      .pipe { timestamp => s"providers/$timestamp.csv" }

  private lazy val googleCloudStorage: Storage =
    StorageOptions.getDefaultInstance
      .toBuilder()
      .setCredentials(GoogleCredentials.getApplicationDefault())
      .build()
      .getService

  def saveAll: Boolean = {
    val data: List[LicensedPropertyServiceProvider] = store.getAll
    val tmpFilePath: Path                           = Files.createTempFile("tmp", ".csv")
    Try {
      val fileWriter = new FileWriter(tmpFilePath.toFile)
      data.zipWithIndex.foreach((d, i) =>
        if (i == 0) {
          fileWriter.write(CsvUtil.header(d))
        }
        fileWriter.write(CsvUtil.toCsv(d))
      )
      fileWriter.close()
      val writeOptions: List[BlobWriteOption] = List.empty
      val blobInfoVersioned: BlobInfo         = BlobInfo.newBuilder(bucket, blobNameVersioned).build()
      val blobInfoLatest: BlobInfo            = BlobInfo.newBuilder(bucket, blobNameLatest).build()
      googleCloudStorage.createFrom(blobInfoVersioned, tmpFilePath, writeOptions: _*)
      googleCloudStorage.createFrom(blobInfoLatest, tmpFilePath, writeOptions: _*)
    } match
      case Success(_) => true
      case Failure(exception) =>
        println(exception.getMessage)
        false
  }

  def getAll: List[LicensedPropertyServiceProvider] =
    val sourceOptions: List[BlobSourceOption] = List.empty
    val byteArray: Array[Byte]                = googleCloudStorage.readAllBytes(bucket, blobNameLatest, sourceOptions: _*)
    byteArray
      .pipe { new String(_) }
      .linesIterator
      .flatMap { CsvUtil.fromCsv }
      .toList
}

object LicensedPropertyServiceProviderGoogleStore {

  lazy val instance: LicensedPropertyServiceProviderGoogleStore = LicensedPropertyServiceProviderGoogleStore(LicensedPropertyServiceProviderStore.fromScraper)

  lazy val layer: ZLayer[LicensedPropertyServiceProviderStore, Throwable, LicensedPropertyServiceProviderGoogleStore] =
    ZLayer.scoped {
      ZIO
        .service[LicensedPropertyServiceProviderStore]
        .map(LicensedPropertyServiceProviderGoogleStore(_))
    }

  lazy val live: ZLayer[Any, Throwable, LicensedPropertyServiceProviderGoogleStore] =
    LicensedPropertyServiceProviderStore.layer >>> layer
}
