package com.eigengo.lift.spark

import com.eigengo.lift.spark.jobs.Batch
import com.typesafe.config.Config
import org.apache.log4j.Logger
import org.apache.spark.{SparkConf, SparkContext}

import scala.reflect.ClassTag

trait Driver {

  def config: Config

  def master: String

  private val logger = Logger.getLogger(classOf[Driver])

  private lazy val sc = sparkContext("Spark Driver",  (c, conf) => {
    conf.set("spark.cassandra.connection.host", c.getString("cassandra.host"))
      .set("spark.cassandra.journal.keyspace", "akka")
      .set("spark.cassandra.journal.table", "messages")
  })

  private def sparkContext(name: String, additionalConfig: (Config, SparkConf) => SparkConf) = {
    val conf = new SparkConf()
      .setAppName(name)
      .setMaster(master)

    val sc = new SparkContext(additionalConfig(config, conf))
    sc.addJar("/app/spark-assembly-1.0.0-SNAPSHOT.jar")
    sc
  }

  def submit[P, R](job: Batch[P, R], jobParam: P): Either[String, R] = {
    //val sc = sparkContext(job.name, job.additionalConfig)

    logger.info(s"Executing job ${job.name} on master $master")
    val result = job.execute(sc, config, jobParam)
    logger.info(s"Job ${job.name} finished with result $result")

    //sc.stop()
    result
  }

  def submit[R](name: String, job: SparkContext => Either[String, R], additionalConfig: (Config, SparkConf) => SparkConf = (x, y) => y) = {
    //val sc = sparkContext(name, additionalConfig)

    logger.info(s"Executing job ${name} on master $master")
    val result = job(sc)
    logger.info(s"Job ${name} finished with result $result")

    //sc.stop()
    result
  }

  def submit[T](job: Stream[T]): Either[String, T] = ???
}