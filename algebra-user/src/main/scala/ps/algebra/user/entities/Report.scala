package ps.algebra.user.entities

import ps.algebra.user.{ReportID, ReportMessage, UserID}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-12
  *
  */
final case class Report(
    reportId: ReportID,
    reportedId: UserID,
    reporterId: UserID,
    message: ReportMessage
) extends Serializable

final case class ReportDefinition(
    reportedId: UserID,
    reporterId: UserID,
    message: ReportMessage
) extends Serializable
