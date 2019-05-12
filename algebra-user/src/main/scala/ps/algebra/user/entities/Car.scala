package ps.algebra.user.entities

import ps.algebra.user.{CarID, CarNumber}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-12
  *
  */
final case class Car(
    carId: CarID,
    carNumber: CarNumber
) extends Serializable
