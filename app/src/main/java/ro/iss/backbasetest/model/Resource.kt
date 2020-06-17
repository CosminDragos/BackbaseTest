package ro.iss.backbasetest.model

/**
 * Resource type element to map the appropriate Resource according to process result
 *
 * @param T data type
 * @property data instance of data
 * @property codeMessage code message used for Error Type
 */
sealed class Resource<T>(val data: T? = null, val codeMessage: String? = null) {
    class Success(data: ArrayList<CityModel>) : Resource<ArrayList<CityModel>>(data)
    class Update(data: ArrayList<CityModel>) : Resource<ArrayList<CityModel>>(data)
    class Empty : Resource<ArrayList<CityModel>>(ArrayList())
    class Loading : Resource<ArrayList<CityModel>>()
    class Error(codeMessage: String) : Resource<ArrayList<CityModel>>(codeMessage = codeMessage)
}