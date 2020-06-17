package ro.iss.backbasetest.model

sealed class Resource<T>(val data: T? = null, val codeMessage: String? = null) {
    class Success(data: ArrayList<CityModel>) : Resource<ArrayList<CityModel>>(data)
    class Update(data: ArrayList<CityModel>) : Resource<ArrayList<CityModel>>(data)
    class Empty : Resource<ArrayList<CityModel>>(ArrayList())
    class Loading : Resource<ArrayList<CityModel>>()
    class Error(codeMessage: String) : Resource<ArrayList<CityModel>>(codeMessage = codeMessage)
}