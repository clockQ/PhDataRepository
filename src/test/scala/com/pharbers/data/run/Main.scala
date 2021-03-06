package com.pharbers.data.run

/**
  * @description:
  * @author: clock
  * @date: 2019-04-19 18:33
  */
object Main extends App {
    val SAVE = Array("TRUE")
    val NOT_SAVE = Array("FALSE")
    val STATUS = NOT_SAVE

    // address
    TransformAddress.main(STATUS)

    // hosp
    TransformHosp.main(STATUS)

    // product
    TransformProductDev.main(STATUS)
    TransformOadAndAtc3Table.main(STATUS)
    TransformProductIms.main(STATUS)
    TransformProductEtc.main(STATUS)

    // CHC
    TransformCHCDate.main(SAVE)
    TransformCHC.main(STATUS)

    // source
    TransformPHA.main(SAVE)
    TransformCPA.main(STATUS)
    TransformGYCX.main(STATUS)
    TransformMissHosp.main(SAVE)

    TransformPanel.main(STATUS)
    TransformMaxResult.main(STATUS)
}
