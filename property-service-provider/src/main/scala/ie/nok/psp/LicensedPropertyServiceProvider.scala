package ie.nok.psp

import zio.prelude.NonEmptyList

import java.time.LocalDate

case class LicensedPropertyServiceProvider(
    county: Option[String],
    licenseNumber: String,
    parentLicense: Option[String],
    licenseeDetails: String,
    address: String,
    tradingName: Option[String],
    classOfProvider: ClassOfProvider,
    licenseExpiry: LocalDate,
    licenseTypes: List[LicenseType],
    licenseStatus: LicenceStatus,
    additionalInfo: Option[String]
)

enum ClassOfProvider:
  case Company
  case CompanySecretary
  case Director
  case Employee
  case IndependentContractor
  case Manager
  case Partner
  case Partnership
  case PropertyServicesEmployer
  case Secretary

enum LicenceStatus:
  case NotAuthorised
  case PendingRenewal
  case Permitted
  case Suspended

enum LicenseType(info: String):
  case A extends LicenseType("The Auction of Property other than Land")
  case B extends LicenseType("The Purchase or Sale, by whatever means, of Land")
  case C extends LicenseType("The Letting of Land")
  case D extends LicenseType("Property Management Services")
