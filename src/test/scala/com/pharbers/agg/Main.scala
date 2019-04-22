package com.pharbers.agg

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import com.pharbers.data.util.ParquetLocation._

/**
  * @description:
  * @author: clock
  * @date: 2019-04-19 13:07
  */
object Main extends App {
    val aggFactory = new Agg(PFIZER_COMPANY_ID)
//    aggFactory.setSource("/test/CPA&GYCX/Pfizer_201804_CPA_20181227.csv", "/test/CPA&GYCX/Pfizer_201804_Gycx_20181127.csv")
    println("market count = " + aggFactory.getMarket.length)
    println("mole count = " + aggFactory.getMole.length)
    println("product name count = " + aggFactory.getProductName.length)
    println("provinces count = " + aggFactory.getProvinces.length)
    println("city count = " + aggFactory.getCity.length)
    println("hospital count = " + aggFactory.getHosp.length)
    aggFactory.getCountByTime.foreach(println)
    aggFactory.getCountByProvince.foreach(println)
    aggFactory.getHospType.foreach(println)
//    aggFactory.getSampleHosp
}

class Agg(company_id: String) {

    import com.pharbers.data.util._
    import com.pharbers.data.conversion._
    import org.apache.spark.sql.functions._
    import com.pharbers.data.util.ParquetLocation._
    import com.pharbers.data.util.sparkDriver.ss.implicits._

    val productDevERDArgs = Parquet2DF(PROD_DEV_LOCATION) // 17765
    val productEtcERDArgs = Parquet2DF(PROD_ETC_LOCATION + "/" + company_id)

    lazy val prodDIS: DataFrame = {
        ProductDevConversion()(ProductImsConversion(), ProductEtcConversion())
                .toDIS(Map(
                    "productDevERD" -> productDevERDArgs
                    , "productEtcERD" -> productEtcERDArgs
                    , "marketERD" -> Parquet2DF(PROD_MARKET_LOCATION + "/" + company_id)
                    , "atcERD" -> Parquet2DF(PROD_ATCTABLE_LOCATION)
                ))("productDIS")
    }

    lazy val hospDIS: DataFrame = {
        HospConversion().toDIS(
            Map(
                "hospBaseERD" -> Parquet2DF(HOSP_BASE_LOCATION),
                "hospAddressERD" -> Parquet2DF(HOSP_ADDRESS_BASE_LOCATION),
                "hospPrefectureERD" -> Parquet2DF(HOSP_ADDRESS_PREFECTURE_LOCATION),
                "hospCityERD" -> Parquet2DF(HOSP_ADDRESS_CITY_LOCATION),
                "hospProvinceERD" -> Parquet2DF(HOSP_ADDRESS_PROVINCE_LOCATION)
            )
        )("hospDIS")
    }

    lazy val marketDF = Parquet2DF(PROD_MARKET_LOCATION + "/" + company_id)

    lazy val phaDF = Parquet2DF(HOSP_PHA_LOCATION)

    var sourceDF: DataFrame = Parquet2DF("/test/qi/sourceDF")//Seq.empty[String].toDF("_id")

    def getMarket: Array[String] = marketDF.select("MARKET").distinct().collect().map(_.getAs[String](0))

    def getMole: Array[String] = prodDIS.select("ETC_MOLE_NAME").distinct().collect().map(_.getAs[String](0))

    def getProductName: Array[String] = prodDIS.select("ETC_PRODUCT_NAME").distinct().collect().map(_.getAs[String](0))

    def setSource(cpa_file: String = "", gycx_file: String = ""): Agg = {
        if (cpa_file.nonEmpty) {
            val cpaCvs = CPAConversion()(ProductEtcConversion())
            val cpaDF = CSV2DF(cpa_file).withColumn("COMPANY_ID", lit(company_id))
            val cpaERD = cpaCvs.toERD(Map(
                "cpaDF" -> cpaDF
                , "hospDF" -> hospDIS
                , "prodDF" -> prodDIS
                , "phaDF" -> phaDF
            ))("cpaERD")
            sourceDF = sourceDF.alignAt(cpaERD).unionByName(cpaERD.alignAt(sourceDF))
        }

        if (gycx_file.nonEmpty) {
            val gycCvs = GYCConversion(company_id)(ProductEtcConversion())
            val gycDF = CSV2DF(gycx_file)
            val gycxERD = gycCvs.toERD(Map(
                "gycDF" -> gycDF
                , "hospDF" -> hospDIS
                , "prodDF" -> prodDIS
                , "phaDF" -> phaDF
            ))("gycERD")
            sourceDF = sourceDF.alignAt(gycxERD).unionByName(gycxERD.alignAt(sourceDF))
        }

        this
    }

    lazy val sourceHosp: DataFrame = {
        sourceDF
                .join(
                    hospDIS
                    , sourceDF("HOSP_ID") === hospDIS("_id")
                    , "left"
                )
                .withColumn("province-name",
                    when(col("province-name").isNull, lit("other"))
                            .otherwise(col("province-name"))
                )
                .withColumn("city-name",
                    when(col("city-name").isNull, lit("other"))
                            .otherwise(col("city-name"))
                )
                .withColumn("hosp-name",
                    when(col("title").isNull, lit("other"))
                            .otherwise(col("title"))
                )
    }

    def getProvinces: Array[String] = sourceHosp.select("province-name").distinct().collect().map(_.getAs[String](0))

    def getCity: Array[String] = sourceHosp.select("city-name").distinct().collect().map(_.getAs[String](0))

    def getHosp: Array[String] = sourceHosp.select("hosp-name").distinct().collect().map(_.getAs[String](0))

    def getCountByTime: List[(String, String)] = sourceDF.groupBy("YM").count().sort(col("YM").asc).collect()
            .map(x => x.getAs[String](0) -> x.getAs[String](1))
            .toList

    def getCountByProvince: List[(String, Long)] = sourceHosp.groupBy("province-name").count().sort(col("count").desc).collect()
            .map(x => x.getAs[String](0) -> x.getAs[Long](1))
            .toList

    def getHospType: List[(String, Long, Double)] = {
        val hospDF = sourceHosp.select("hosp-name", "type")
        val hospCount = hospDF.count()
        val hospTypeCount = hospDF.groupBy("type").count().sort(col("count").desc).collect()
                .map(x => (
                        x.getAs[String](0)
                        , x.getAs[Long](1)
                        , (x.getAs[Long](1) / hospCount.toDouble).formatted("%1.5f").toDouble
                        )
                )
                .toList
        ("全部", hospCount, 1.0) :: hospTypeCount
    }

    def getSampleHosp = {
        val a = CSV2DF("/test/2019年Universe更新维护1.0.csv")
        a.show(false)
        sourceDF.show(false)
    }

}

