package ro.iss.backbasetest.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ro.iss.backbasetest.model.CityModel
import java.io.InputStream
import java.lang.reflect.Type

/**
 * Object class used to process files/sorting operations
 */
object FileUtils {

    /**
     * This method takes the json String parameter that is usually obtained after calling
     * the method loadJsonStringFromAsset and converts it into an ArrayList of cities
     * using gson parsing library.
     *
     * @param jsonString JSON input as String
     * @return the list of the cities processed after parsing
     */

    fun loadCitiesList(
        jsonString: String?
    ): ArrayList<CityModel>? {

        val gsInstance = Gson()
        val type: Type = object: TypeToken<ArrayList<CityModel>>(){}.type

        return try {
            gsInstance.fromJson<ArrayList<CityModel>>(
                jsonString,
                type
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    /**
     * This method is reading the file content and converts it into a String
     *
     * @param context Activity Context
     * @param fileName the name of the file you want to read
     * @return file content as JSON String
     */
    @Throws(Exception::class)
    fun loadJsonStringFromAsset(
        context: Context?,
        fileName: String
    ): String? {

        try {

            val inputStream: InputStream? = context?.assets?.open(fileName)
            val size: Int? = inputStream?.available()

            size?.let {
                val buffer = ByteArray(it)
                inputStream.read(buffer)
                inputStream.close()
                return String(buffer, Charsets.UTF_8)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null

    }

    /**
     * This method takes the list received as a parameter and sorts it by name or country
     * using sortedWith block from Collections
     *
     * @param unsortedCitiesList initial list unsorted
     * @return the list received as parameters sorted
     */
    fun sortCitiesList(
        unsortedCitiesList: ArrayList<CityModel>
    ): ArrayList<CityModel> {

        val sortedList = ArrayList<CityModel>()

        sortedList.addAll(
            unsortedCitiesList.sortedWith(
                compareBy (
                    { it.name },
                    { it.country }
                )
            )
        )

        return sortedList

    }

}