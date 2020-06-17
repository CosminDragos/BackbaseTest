package ro.iss.backbasetest.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ro.iss.backbasetest.model.CityModel
import java.io.InputStream
import java.lang.reflect.Type

object FileUtils {

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