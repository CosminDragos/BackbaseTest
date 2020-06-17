package ro.iss.backbasetest.model

import com.google.gson.annotations.SerializedName

@Suppress("unused", "SpellCheckingInspection")
class CityModel(
    @SerializedName("_id") var id: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("name")  var name: String? = null,
    @SerializedName("coord") var coordinates: CoordinatesModel? = null
)