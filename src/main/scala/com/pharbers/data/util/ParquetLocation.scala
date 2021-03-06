package com.pharbers.data.util

/**
  * @description:
  * @author: clock
  * @date: 2019-03-28 17:29
  */
object ParquetLocation {
    // COMPANY_ID
    val NHWA_COMPANY_ID = "5ca069bceeefcc012918ec72"
    val PFIZER_COMPANY_ID = "5ca069e2eeefcc012918ec73"

    // hospital address
    val HOSP_ADDRESS_BASE_LOCATION = "/repository/address"
    val HOSP_ADDRESS_CITY_LOCATION = "/repository/city"
    val HOSP_ADDRESS_MEDLE_LOCATION = "/test/hosp/Address/medle"
    val HOSP_ADDRESS_PREFECTURE_LOCATION = "/repository/prefecture"
    val HOSP_ADDRESS_PROVINCE_LOCATION = "/repository/province"
    val HOSP_ADDRESS_REGION_LOCATION = "/repository/region"
    val HOSP_ADDRESS_TIER_LOCATION = "/repository/tier"
    val ADDRESS_DIS_LOCATION = "/repository/address_dis"
    // hospital
    val HOSP_BASE_LOCATION = "/repository/hosp"
    val HOSP_BED_LOCATION = "/test/hosp/bed"
    val HOSP_ESTIMATE_LOCATION = "/test/hosp/estimate"
    val HOSP_OUTPATIENT_LOCATION = "/test/hosp/outpatient"
    val HOSP_REVENUE_LOCATION = "/test/hosp/revenue"
    val HOSP_SPECIALTY_LOCATION = "/test/hosp/specialty"
    val HOSP_STAFFNUM_LOCATION = "/test/hosp/staffNum"
    val HOSP_UNIT_LOCATION = "/test/hosp/unit"
    // hospital pha
    val HOSP_PHA_LOCATION = "/repository/pha"
    val HOSP_DIS_LOCATION = "/repository/hosp_dis_max"

    // Product Dev
    val PROD_DEV_LOCATION = "/repository/prod_dev_max"
    // Product IMS
    val PROD_ATC3TABLE_LOCATION = "/repository/atc3_table5"
    val PROD_OADTABLE_LOCATION = "/repository/oad_table5"
    val PROD_IMS_LOCATION = "/repository/prod_ims5"
    // Product ATC
    val PROD_ATCTABLE_LOCATION = "/repository/atc_table5"
    // Product Etc
    val PROD_ETC_LOCATION = "/repository/prod_etc_max"
    val PROD_ETC_DIS_LOCATION = "/repository/prod_etc_dis_max"
    // Product Market
    val PROD_MARKET_LOCATION = "/repository/market5"

    // SOURCE
    val CHC_LOCATION = "/repository/chc5"
    val CHC_CA_LOCATION = "/repository/chc_ca"
    val CHC_DATE_LOCATION = "/repository/chc_date3"

    // MAX
    val CPA_LOCATION = "/repository/cpa"
    val GYCX_LOCATION = "/repository/gycx"
    val FULL_HOSP_LOCATION = "/repository/full_hosp"
    val MISS_HOSP_LOCATION = "/repository/miss_hosp"
    val NOT_PUBLISHED_HOSP_LOCATION = "/repository/not_published_hosp"
    val SAMPLE_HOSP_LOCATION = "/repository/sample_hosp"
    val UNIVERSE_HOSP_LOCATION = "/repository/universe_hosp"

    val PANEL_LOCATION = "/repository/panel3"
    val MAX_RESULT_LOCATION = "/repository/max3"

    // Agg
    val MAX_RESULT_MARKET_AGG_LOCATION = "/repository/agg/maxResult/market3"
    val MAX_RESULT_ADDRESS_AGG_LOCATION = "/repository/agg/maxResult/address3"
}
