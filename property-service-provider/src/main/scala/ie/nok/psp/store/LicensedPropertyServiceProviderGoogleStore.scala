package ie.nok.psp.store

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.{Blob, BlobInfo, Storage, StorageOptions}
import ie.nok.psp.LicensedPropertyServiceProvider

import java.io.FileWriter
import java.nio.file.{Files, Path}
import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneOffset}
import scala.util.Try
import scala.util.chaining.scalaUtilChainingOps
object LicensedPropertyServiceProviderGoogleStore {

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

  private val googleCloudStorage: Storage =
    StorageOptions.getDefaultInstance
      .toBuilder()
      .setCredentials(GoogleCredentials.getApplicationDefault())
      .build()
      .getService

  def saveAll(data: List[LicensedPropertyServiceProvider]): Boolean = {
    val tmpFile: Path = Files.createTempFile("tmp", ".csv")
    Try {
      val fileWriter = new FileWriter(tmpFile.toFile)
      data.zipWithIndex.foreach((d, i) =>
        if (i == 0) {
          fileWriter.write(CsvUtil.header(d))
        }
        fileWriter.write(CsvUtil.toCsv(d))
      )
      fileWriter.close()
    }
    val blobInfo: BlobInfo = BlobInfo.newBuilder(bucket, blobNameVersioned).build()
    val blob: Blob         = googleCloudStorage.createFrom(blobInfo, tmpFile, List.empty: _*)
    // TODO how can I know that the file was saved from the response ?
    true
  }

}
