package agiga
//package org.clulab.learning

import org.clulab.struct.Counter

/**
  * Trait for ML datums. L indicates the type of the label; F indicates the type of the feature
  * User: mihais
  * Date: 4/23/13
  */
trait Datum[L, F] {
  val label:L

  def features:Iterable[F]

  def featuresCounter:Counter[F]

  override def toString:String = {
    val os = new StringBuilder
    os.append("LABEL:" + label)
    os.append(" FEATURES:")
    val c = featuresCounter
    val keys = c.keySet
    var first = true
    for(key <- keys) {
      if(! first) os.append(", ")
      os.append(key)
      os.append(":")
      os.append(c.getCount(key))
      first = false
    }
    os.toString()
  }
}

/**
  * Datum that contains real-valued features
  * @param label
  * @param featuresCounter
  * @tparam L
  * @tparam F
  */
class mRVFDatum[String, L, F](
                        val name:String,
                        val label:L,
                        val featuresCounter:Counter[F]) extends Datum[L, F] {

  def features = featuresCounter.keySet

  def getFeatureCount(f:F) = featuresCounter.getCount(f)

  override def equals(other:Any):Boolean = {
    other match {
      case that:mRVFDatum[String, L, F] => label == that.label && featuresCounter == that.featuresCounter
      case _ => false
    }
  }
}