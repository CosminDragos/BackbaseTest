package ro.iss.backbasetest.model

import com.google.gson.annotations.SerializedName

/**
 * Object Model Class for a City Representation Data
 *
 * @property id city id
 * @property country country code
 * @property name city name
 * @property coordinates coordinates values
 */
@Suppress("unused", "SpellCheckingInspection")
class CityModel(
    @SerializedName("_id") var id: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("name")  var name: String? = null,
    @SerializedName("coord") var coordinates: CoordinatesModel? = null
)