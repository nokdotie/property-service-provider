package ie.nok.psp

case class LicensedPropertyServiceProviderRaw(
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
