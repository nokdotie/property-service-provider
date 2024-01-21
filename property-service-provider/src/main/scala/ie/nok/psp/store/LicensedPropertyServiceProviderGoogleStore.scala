package ie.nok.psp.store

import com.google.cloud.storage.Storage.{BlobSourceOption, BlobWriteOption}
import com.google.cloud.storage.{BlobInfo, Storage}
import ie.nok.gcp.safe.{GcpCredentials, GcpStorage}
import ie.nok.psp.LicensedPropertyServiceProvider
import zio.{ZIO, ZLayer}

import java.io.FileWriter
import java.nio.file.{Files, Path}
import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneOffset}
import scala.util.chaining.scalaUtilChainingOps
import scala.util.{Failure, Success, Try}

class LicensedPropertyServiceProviderGoogleStore(googleCloudStorage: Storage, store: LicensedPropertyServiceProviderStore) {

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
      .map { CsvUtil.fromCsv }
      .flatMap { _.toOption }
      .toList
}

object LicensedPropertyServiceProviderGoogleStore {

//  private lazy val googleCredentials: Task[GoogleCredentials] =

  lazy val zGoogleStorage: ZIO[Any, Throwable, Storage] = ZIO
    .fromTry(GcpCredentials.googleCredentials)
    .flatMap(credentials => ZIO.fromTry(GcpStorage.googleCloudStorage(credentials)))

  lazy val googleStorageLayer: ZLayer[Any, Throwable, Storage] = ZLayer.scoped(zGoogleStorage)

  lazy val zLicensedPropertyServiceProviderGoogleStore
      : ZIO[Storage with LicensedPropertyServiceProviderStore, Throwable, LicensedPropertyServiceProviderGoogleStore] =
    for {
      googleCloudStorage <- ZIO.service[Storage]
      store              <- ZIO.service[LicensedPropertyServiceProviderStore]
    } yield {
      LicensedPropertyServiceProviderGoogleStore(googleCloudStorage, store)
    }

  lazy val layer: ZLayer[Storage with LicensedPropertyServiceProviderStore, Throwable, LicensedPropertyServiceProviderGoogleStore] =
    ZLayer.scoped(zLicensedPropertyServiceProviderGoogleStore)

//  lazy val v: ZLayer[Any, Throwable, LicensedPropertyServiceProviderGoogleStore] =

//  lazy val instance: LicensedPropertyServiceProviderGoogleStore = licensedPropertyServiceProviderStore.map(LicensedPropertyServiceProviderGoogleStore(_))
//
//  lazy val layer: ZLayer[LicensedPropertyServiceProviderStore, Throwable, LicensedPropertyServiceProviderGoogleStore] =
//    ZLayer.scoped {
//      ZIO
//        .service[LicensedPropertyServiceProviderStore]
//        .map(LicensedPropertyServiceProviderGoogleStore(_))
//    }
//
//  lazy val live: ZLayer[Any, Throwable, LicensedPropertyServiceProviderGoogleStore] =
//    LicensedPropertyServiceProviderStore.layer >>> layer
}
