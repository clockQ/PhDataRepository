package com.pharbers.common

import org.apache.spark.sql.DataFrame

/**
  * @ ProjectName pharbers-data-repository.com.pharbers.common.phDataTraie
  * @ author jeorch
  * @ date 19-3-26
  * @ Description: TODO
  */
trait phDataTrait {
    def getDataFromCsv(df: DataFrame): Unit
}
