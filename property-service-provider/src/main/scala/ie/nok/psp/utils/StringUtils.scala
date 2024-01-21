package ie.nok.psp.utils

object StringUtils {
  def toStrOpt(s: String): Option[String] = s.trim match {
    case ""    => None
    case other => Some(other)
  }
}
