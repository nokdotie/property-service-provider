package ie.nok.psp

extension (s: String) {
  private def toStrOpt: Option[String] = s.trim match {
    case ""    => None
    case other => Some(other)
  }
}
