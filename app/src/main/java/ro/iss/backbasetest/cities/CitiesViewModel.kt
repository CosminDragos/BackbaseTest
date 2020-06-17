package ro.iss.backbasetest.cities

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ro.iss.backbasetest.model.CityModel
import ro.iss.backbasetest.model.Resource
import ro.iss.backbasetest.model.Resource.Success
import ro.iss.backbasetest.model.Resource.Update
import ro.iss.backbasetest.model.Resource.Error
import ro.iss.backbasetest.model.Resource.Loading
import ro.iss.backbasetest.model.Resource.Empty
import ro.iss.backbasetest.utils.Constants.GENERAL_ERROR_CITIES
import ro.iss.backbasetest.utils.SearchUtils
import ro.iss.backbasetest.utils.FileUtils
import ro.iss.backbasetest.utils.SortUtils
import java.io.File

class CitiesViewModel : ViewModel() {

    val citiesLiveData = MutableLiveData<Resource<*>>()
    val searchLiveData = MutableLiveData<Resource<*>>()

    fun getCities(
        context: Context?,
        fileName: String
    ) {

        viewModelScope.launch(Dispatchers.Main) {

            citiesLiveData.postValue(
                Loading()
            )

            val jsonStringData = readFromFile(context, fileName)
            val unsortedCitiesList = parseCitiesJsonString(jsonStringData)
            val citiesList = sortCitiesList(unsortedCitiesList)

            SearchUtils.citiesList = citiesList

            citiesList?.let {
                when {
                    it.isEmpty() ->
                        citiesLiveData.postValue(
                            Empty()
                        )
                    else ->
                        citiesLiveData.postValue(
                            Success(it)
                        )
                }

            } ?:
            citiesLiveData.postValue(
                Error(GENERAL_ERROR_CITIES)
            )

        }

    }

    private suspend fun readFromFile(
        context: Context?,
        fileName: String
    ): String?  {

        val deferredReading =
            viewModelScope.async(Dispatchers.IO) {
                FileUtils.loadJsonStringFromAsset(
                    context,
                    fileName
                )
            }

        return deferredReading.await()

    }

    private suspend fun parseCitiesJsonString(
        jsonStringData: String?
    ): ArrayList<CityModel>? {

        val deferredParsing =
            viewModelScope.async(Dispatchers.Default) {
                FileUtils.loadCitiesList(
                    jsonStringData
                )
            }

        return deferredParsing.await()
    }

    private suspend fun sortCitiesList(
        unsortedCitiesList: ArrayList<CityModel>?
    ): ArrayList<CityModel>? {

        val deferredSorting =
            viewModelScope.async(Dispatchers.Default) {

                unsortedCitiesList?.let { unsortedCitiesListNonNull ->
                    return@async FileUtils.sortCitiesList(
                        unsortedCitiesListNonNull
                    )
                }

                null

            }

        return deferredSorting.await()
    }

    fun searchByPrefix(prefix: String) {

        viewModelScope.launch(Dispatchers.Main) {

            val deferredSearch =
                viewModelScope.async(Dispatchers.Default) {
                    SearchUtils.binarySearch(prefix)
                }

            val searchedList = deferredSearch.await()

            when {
                searchedList.isEmpty() ->
                    searchLiveData.postValue(
                        Empty()
                    )
                else ->
                    searchLiveData.postValue(
                        Update(searchedList)
                    )
            }

        }

    }

}