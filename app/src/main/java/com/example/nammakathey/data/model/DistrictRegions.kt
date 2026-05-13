package com.example.nammakathey.data.model

import com.example.nammakathey.data.model.DistrictRegion

object DistrictRegions {

    fun getRegions(): List<DistrictRegion> {
        return listOf(
            DistrictRegion("Belagavi", 150f, 200f, 80f),
            DistrictRegion("Dharwad", 220f, 300f, 70f),
            DistrictRegion("Mysuru", 300f, 600f, 80f),
            DistrictRegion("Bangalore", 420f, 750f, 70f)
        )
    }
}