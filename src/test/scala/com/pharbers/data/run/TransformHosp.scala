package com.pharbers.data.run

import com.pharbers.pactions.actionbase.{DFArgs, MapArgs}
import com.pharbers.data.conversion.hosp.{phDataHandFunc, phFactory, phHospData, phRegionData}

/**
  * @description:
  * @author: clock
  * @date: 2019-04-16 19:24
  */
object TransformHosp extends App {

    import com.pharbers.data.util._
    import com.pharbers.data.conversion._
    import org.apache.spark.sql.functions._
    import com.pharbers.data.util.ParquetLocation._
    import com.pharbers.data.util.sparkDriver.ss.implicits._

    def transformHosp(): Unit = {
        val driver = phFactory.getSparkInstance()

        driver.sc.addJar("target/pharbers-data-repository-1.0-SNAPSHOT.jar")
        var df = driver.ss.read.format("com.databricks.spark.csv")
                .option("header", "true")
                .option("delimiter", ",")
                .load("/test/2019年Universe更新维护1.0.csv")
                .withColumn("addressId", phDataHandFunc.setIdCol())
                .cache()

        df.columns.foreach(x => {
            df = df.withColumnRenamed(x, x.trim)
        })

        new phHospData().getHospDataFromCsv(df)
        new phRegionData().getRegionDataFromCsv(df)
    }
//    transformHosp()


    val hospCvs = HospConversion()

    val hospBaseERD = Parquet2DF(HOSP_BASE_LOCATION)
    val hospBaseERDCount = hospBaseERD.count()

    val hospDIS = hospCvs.toDIS(MapArgs(Map(
        "hospBaseERD" -> DFArgs(hospBaseERD)
        , "hospAddressERD" -> DFArgs(Parquet2DF(HOSP_ADDRESS_BASE_LOCATION))
        , "hospPrefectureERD" -> DFArgs(Parquet2DF(HOSP_ADDRESS_PREFECTURE_LOCATION))
        , "hospCityERD" -> DFArgs(Parquet2DF(HOSP_ADDRESS_CITY_LOCATION))
        , "hospProvinceERD" -> DFArgs(Parquet2DF(HOSP_ADDRESS_PROVINCE_LOCATION))
    ))).getAs[DFArgs]("hospDIS")
    hospDIS.show(false)
    val hospDISCount = hospDIS.count()
    val hospMinus = hospBaseERDCount - hospDISCount
    assert(hospMinus == 0, "prodIms: 转换后的DIS比ERD减少`" + hospMinus + "`条记录")
}
