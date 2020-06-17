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

/**
 * ViewModel class that deals with fetching model data using Coroutines and LiveData.
 * For each request is used a Dispatchers coroutine that manages better
 * the type requirements needed for that specific operation.
 * In this class we are calling the methods that are reading the content file,
 * parse it from JSON String to a list of objects, sort it and filter it
 * using a prefix that came from the UI user interaction and then sends
 * the appropriate Resource value according to type of Responses
 *
 */
class CitiesViewModel : ViewModel() {

    /**
     * LiveData needed for getting the list of cities
     */
    val citiesLiveData = MutableLiveData<Resource<*>>()

    /**
     * LiveData needed for getting the list of cities filtered by a prefix
     */
    val searchLiveData = MutableLiveData<Resource<*>>()

    /**
     * Method called to get list of the cities.
     *
     * I chose Main Dispatcher because it's designed to interact with the UI
     * and perform light work like calling UI functions, updating LiveData or
     * calling suspend functions.
     *
     * @param context Activity Context
     * @param fileName the name of the file you want to read
     */
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

    /**
     * Method called to get the String content of a file using loadJsonStringFromAsset method
     * from FileUtils.
     *
     * I chose IO Dispatcher because it's optimized for disk and network IO like
     * reading/writing files, networking or database.
     *
     * @param context Activity Context
     * @param fileName the name of the file you want to read
     * @return file content as JSON String from loadJsonStringFromAsset method
     */
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

    /**
     * Method called to get the parsed content list using loadCitiesList method
     * from FileUtils.
     *
     * I chose Default Dispatcher because it's optimized for CPU intensive work like
     * parsing a JSON, sorting a list or searching in a list.
     *
     * @param jsonStringData file content as JSON String
     * @return the list of the cities processed after parsing from loadCitiesList method
     */
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

    /**
     * Method called to sort the cities list using sortCitiesList method from FileUtils
     *
     * I chose Default Dispatcher because it's optimized for CPU intensive work like
     * parsing a JSON, sorting a list or searching in a list.
     *
     * @param unsortedCitiesList initial list unsorted
     * @return the list of the cities sorted from sortCitiesList method
     */
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

    /**
     * Method called to filter the cities by prefix using binarySearch method from SearchUtils.
     *
     * I chose Default Dispatcher because it's optimized for CPU intensive work like
     * parsing a JSON, sorting a list or searching in a list.
     *
     * @param prefix the filtering element
     */
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