package ro.iss.backbasetest

import android.os.Build
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ro.iss.backbasetest.model.CityModel
import ro.iss.backbasetest.utils.FileUtils
import ro.iss.backbasetest.utils.SearchUtils

/**
 * Unit tests for json reading, parsing and search filtering
 *
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class CitiesUnitTest {

    private var activity: MainActivity? = null
    private var citiesList: ArrayList<CityModel>? = null
    private var citiesListWithNullNames: ArrayList<CityModel>? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .resume()
            .get()

        citiesList = getCities()
        citiesListWithNullNames = getCitiesWithNullNames()
    }

    @Test
    @Throws(Exception::class)
    fun activityShouldNotBeNull() {
        assertNotNull(activity)
    }

    @Test
    fun checkFileReadingExistentFile() {
        val jsonString = FileUtils.loadJsonStringFromAsset(
            activity,
            "json/cities.json"
        )
        assertTrue(jsonString is String)
    }

    @Test
    fun checkFileParsingWhenJsonNull() {
        val list = FileUtils.loadCitiesList(null)
        assertNull(list)
    }

    @Test
    fun checkFileParsingSuccessFlow() {
        val jsonString = FileUtils.loadJsonStringFromAsset(
            activity,
            "json/cities.json"
        )
        val list = FileUtils.loadCitiesList(jsonString)
        assertEquals(list?.size, 209557)
    }

    @Test
    fun `Test Search Implementation Empty Filter`() {
        SearchUtils.citiesList = citiesList
        assertEquals(SearchUtils.binarySearch("").size, 209557)
    }

    @Test
    fun `Test Search Implementation Only Whitespaces`() {
        SearchUtils.citiesList = citiesList
        assertEquals(SearchUtils.binarySearch("    ").size, 209557)
    }

    @Test
    fun `Test Search Implementation Only With Special Characters`() {
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("'")
        assertEquals(searchedList.size, 2)
        assertEquals(searchedList[0].name, "'t Hoeksken")
        assertEquals(searchedList[1].name, "'t Zand")
    }

    @Test
    fun `Test Search Implementation With Character a`() {
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("a")
        assertEquals(searchedList.size, 11206)
        assertEquals(searchedList[1].name, "A Coruna")
    }

    @Test
    fun `Test Search Implementation With a cities Null`() {
        SearchUtils.citiesList = citiesListWithNullNames
        val searchedList = SearchUtils.binarySearch("a")
        assertEquals(searchedList.size, 11206)
    }

    @Test
    fun `Test Search Implementation With case insensitive`() {
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("kaAawA")
        assertEquals(searchedList.size, 1)
    }

    @Test()
    fun `Test Search Implementation For No Elements Found`() {
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("sujib")
        assertEquals(searchedList.size, 0)
    }

    @Test(timeout=10)
    fun `Test Search Implementation With Text Execution Timeout`() {
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("trAwnIKi")
        assertEquals(searchedList.size, 1)
    }

    @Test
    fun `Test Search Implementation With Successive Calls with whitespaces before`() {
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("    daDap")
        assertEquals(searchedList.size, 0)
    }

    @Test
    fun `Test Search Implementation With Successive Calls with whitespaces after`() {
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("daDap  ")
        assertEquals(searchedList.size, 0)
    }

    @Test
    fun `Test Search Implementation With Successive Calls with prefix number`() {
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("665")
        assertEquals(searchedList[0].name, "665 Site Colonia")
    }

    private fun getCities(): ArrayList<CityModel>? {
        val jsonString = FileUtils.loadJsonStringFromAsset(
            activity,
            "json/cities.json"
        )
        val unsortedList = FileUtils.loadCitiesList(jsonString)
        return unsortedList?.let { FileUtils.sortCitiesList(it) }
    }

    private fun getCitiesWithNullNames(): ArrayList<CityModel>? {
        val jsonString = FileUtils.loadJsonStringFromAsset(
            activity,
            "json/cities.json"
        )
        val unsortedList = FileUtils.loadCitiesList(jsonString)
        unsortedList?.get(0)?.name = null
        unsortedList?.get(unsortedList.size / 2)?.name = null
        unsortedList?.get(unsortedList.size - 1)?.name = null
        return unsortedList?.let { FileUtils.sortCitiesList(it) }
    }

}