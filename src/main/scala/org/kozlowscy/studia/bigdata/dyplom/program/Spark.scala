package org.kozlowscy.studia.bigdata.dyplom.program

import org.apache.spark.{SparkConf, SparkContext}

object Spark {
  // zapis plików wynikowych w systemie Windows (http://stackoverflow.com/questions/30993655/write-rdd-as-textfile-using-apache-spark)
  System.setProperty("hadoop.home.dir", "C:/hadoop")

  // utworzenie instancji Sparka
  val conf: SparkConf = new SparkConf()
    .setAppName("studia-bigdata-dyplom")
    .setMaster("local[*]")
    // nadpisywanie plików wynikowych
    .set("spark.hadoop.validateOutputSpecs", "false")
  val context: SparkContext = new SparkContext(conf)
  // ustawienie poziomu logowania Sparka
  context.setLogLevel("ERROR")
}
