package ro.iss.backbasetest.model

import com.google.gson.annotations.SerializedName

@Suppress("unused", "SpellCheckingInspection")
class CoordinatesModel(
    @SerializedName("lon") var longitude: Double? = null,
    @SerializedName("lat") var latitude: Double? = null
)