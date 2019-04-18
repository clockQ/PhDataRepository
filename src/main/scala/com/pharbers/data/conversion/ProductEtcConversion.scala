package com.pharbers.data.conversion

import org.apache.spark.sql.DataFrame
import com.pharbers.phDataConversion.phDataHandFunc

/**
  * @description: product of pharbers
  * @author: clock
  * @date: 2019-03-28 16:40
  */
case class ProductEtcConversion(company_id: String) extends PhDataConversion {

    import com.pharbers.data.util.DFUtil
    import org.apache.spark.sql.functions._
    import com.pharbers.data.util.sparkDriver.ss.implicits._

    def toERD(args: Map[String, DataFrame]): Map[String, DataFrame] = {

        val sourceDataDF = args.getOrElse("sourceDataDF", throw new Exception("not found sourceDataDF"))
        val productDevERD = args.getOrElse("productDevERD", throw new Exception("not found productDevERD"))
        val productMatchDF = args.getOrElse("productMatchDF", throw new Exception("not found productMatchDF"))

        val prodERD = sourceDataDF
            .select("PRODUCT_NAME", "MOLE_NAME", "PACK_DES", "PACK_NUMBER", "DOSAGE", "DELIVERY_WAY", "CORP_NAME")
            .distinct()//pfizer:13851
            .withColumn("MIN1", concat(col("PRODUCT_NAME"), col("DOSAGE"), col("PACK_DES"), col("PACK_NUMBER"), col("CORP_NAME")))
            .join(productMatchDF
                .select("MIN_PRODUCT_UNIT", "MIN_PRODUCT_UNIT_STANDARD")
                .dropDuplicates("MIN_PRODUCT_UNIT")
                .distinct()
                , col("MIN1") === col("MIN_PRODUCT_UNIT"), "left")
            .join(productDevERD
                .withColumnRenamed("_id", "PRODUCT_ID")
                .select(col("PRODUCT_ID"), col("PRODUCT_NAME"), col("DOSAGE_NAME"), col("PACKAGE_DES"), col("PACKAGE_NUMBER"), col("CORP_NAME"))
                .withColumn("MIN2", concat(col("PRODUCT_NAME"), col("DOSAGE_NAME"), col("PACKAGE_DES"), col("PACKAGE_NUMBER"), col("CORP_NAME")))
                .drop("PRODUCT_NAME")
                .drop("DOSAGE_NAME")
                .drop("PACKAGE_DES")
                .drop("PACKAGE_NUMBER")
                .drop("CORP_NAME")
                .dropDuplicates("MIN2")
                , col("MIN_PRODUCT_UNIT_STANDARD") === col("MIN2"), "left")
            .withColumn("COMPANY_ID", lit(company_id))
            .na.fill("")
            .select(
                $"PRODUCT_ID",
                $"COMPANY_ID",
                $"PRODUCT_NAME" as "PH_PRODUCT_NAME",
                $"MOLE_NAME" as "PH_MOLE_NAME",
                $"PACK_DES" as "PH_PACKAGE_DES",
                $"PACK_NUMBER" as "PH_PACKAGE_NUMBER",
                $"DOSAGE" as "PH_DOSAGE_NAME",
                $"DELIVERY_WAY" as "PH_DELIVERY_WAY",
                $"CORP_NAME" as "PH_CORP_NAME"
            )
            .generateId

        Map(
            "productEtcERD" -> prodERD
        )
    }

    def toDIS(args: Map[String, DataFrame]): Map[String, DataFrame] = {
        val productEtcERD = args.getOrElse("productEtcERD", throw new Exception("not found prodERD"))
        val productDevERD = args.getOrElse("productDevERD", Seq.empty[String].toDF("_id"))
        val productImsERD = args.getOrElse("productImsERD", Seq.empty[(String, String)].toDF("_id", "IMS_PACK_ID"))

        val productEtcDIS = productEtcERD
            .join(
                productDevERD.withColumnRenamed("_id", "main-id"),
                col("PRODUCT_ID") === col("main-id"),
                "left"
            ).drop(col("main-id"))
            .join(productImsERD, col("PACK_ID") === col("IMS_PACK_ID"), "left")
            .drop(productImsERD("_id"))

        Map(
            "productEtcDIS" -> productEtcDIS
        )
    }
}