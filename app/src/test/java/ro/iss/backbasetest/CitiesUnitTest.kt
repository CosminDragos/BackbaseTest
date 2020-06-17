package ro.iss.backbasetest

import android.os.Build
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ro.iss.backbasetest.model.CityModel
import ro.iss.backbasetest.utils.FileUtils
import ro.iss.backbasetest.utils.SearchUtils


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class CitiesUnitTest {

    private var activity: MainActivity? = null

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .resume()
            .get()
    }

    @Test
    @Throws(Exception::class)
    fun activityShouldNotBeNull() {
        assertNotNull(activity)
    }

    @Test(expected = Exception::class)
    fun checkFileReadingNonExistentFile() {
        FileUtils.loadJsonStringFromAsset(
            activity,
            "cities.json"
        )
    }

    @Test
    fun checkFileReadingExistentFile() {
        val jsonString = FileUtils.loadJsonStringFromAsset(
            activity,
            "json/cities.json"
        )
        assertTrue(jsonString is String)
    }

    @Test(expected = Exception::class)
    fun checkFileReadingWithNullContext() {
        FileUtils.loadJsonStringFromAsset(
            null,
            "json/cities.json"
        )
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
    fun `test Search Implementation Empty Filter`() {
        val citiesList = getCities()
        SearchUtils.citiesList = citiesList
        assertEquals(SearchUtils.binarySearch("").size, 209557)
    }

    @Test
    fun `test Search Implementation Only Whitespaces`() {
        val citiesList = getCities()
        SearchUtils.citiesList = citiesList
        assertEquals(SearchUtils.binarySearch("    ").size, 209557)
    }

    @Test
    fun `test Search Implementation Only With Special Characters`() {
        val citiesList = getCities()
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("'")
        assertEquals(searchedList.size, 2)
        assertEquals(searchedList[0].name, "'t Hoeksken")
        assertEquals(searchedList[1].name, "'t Zand")
    }

    @Test
    fun `test Search Implementation With Character a`() {
        val citiesList = getCities()
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("a")
        assertEquals(searchedList.size, 11206)
    }

    @Test
    fun `test Search Implementation With a cities Null`() {
        val citiesList = getCitiesWithNullNames()
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("a")
        assertEquals(searchedList.size, 11206)
    }

    @Test
    fun `test Search Implementation With case insensitive`() {
        val citiesList = getCitiesWithNullNames()
        SearchUtils.citiesList = citiesList
        val searchedList = SearchUtils.binarySearch("Kaa-")
        assertEquals(searchedList.size, 11206)
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