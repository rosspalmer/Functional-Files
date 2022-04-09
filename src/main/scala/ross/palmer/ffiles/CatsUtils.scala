package ross.palmer.ffiles

import cats.Monoid

object CatsUtils {
  def combineAll[A: Monoid](as: List[A]): A =
    as.foldLeft(Monoid[A].empty)(Monoid[A].combine)
}
