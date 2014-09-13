package org.scalatrain.basic

object Collections {


  def main(args: Array[String]) {
    traversable()
    mutableCollections()
    immutableCollections()
    parallelCollections()
  }

  def traversable() = {
    // Addition, ++
    // map, flatMap, and collect
    // Conversions toArray, toList, toIterable, toSeq, toIndexedSeq, toStream, toSet, toMap
    // Size info operations isEmpty, nonEmpty, size, and hasDefiniteSize
    // Element retrieval operations head, last, headOption, lastOption, and find
    // Sub-collection retrieval operations tail, init, slice, take, drop, takeWhile, dropWhile, filter, filterNot, withFilter
    // Element tests exists, forall, count
    // Folds foldLeft, foldRight, /:, :\, reduceLeft, reduceRight
    // Specific folds sum, product, min, max
    // mkString
  }

  def mutableCollections(): Unit = {
    import collection.mutable
    // Seq: IndexedSeq, LinearSeq, Array, ArrayBuffer, ListBuffer

    // Set: HashSet

    // Map: HashMap, TrieMap
  }

  def immutableCollections(): Unit = {
    import collection.immutable
    // Seq: List, Vector, Range

    // Set: HashSet

    // Map: HashMap, TreeMap
  }

  def parallelCollections() = {

  }
}
