package ro.iss.backbasetest.utils

import ro.iss.backbasetest.model.CityModel
import java.util.*
import kotlin.collections.ArrayList

object SearchUtils {

    var citiesList: ArrayList<CityModel>? = null

    @Synchronized
    fun binarySearch(prefix: String): ArrayList<CityModel> {

        citiesList?.let {

            if (prefix.isBlank())
                return it

            var lowIndexLimit = 0
            var highIndexLimit = it.size - 1

            while (lowIndexLimit <= highIndexLimit) {

                val midIndex = lowIndexLimit + ((highIndexLimit - lowIndexLimit) / 2)
                val midIndexValue =
                    it[midIndex].name
                        ?.toLowerCase(
                            Locale.getDefault()
                        ) ?: Constants.EMPTY_STRING

                when {
                    midIndexValue.startsWith(prefix) ->
                        return foundWordWithPrefix(
                            lowIndexLimit,
                            highIndexLimit,
                            midIndex,
                            prefix,
                            it
                        )
                    prefix > midIndexValue ->
                        lowIndexLimit = midIndex + 1
                    prefix < midIndexValue ->
                        highIndexLimit = midIndex - 1
                }

            }

        }

        return ArrayList()

    }

    private fun foundWordWithPrefix(
        lowIndexLimit: Int,
        highIndexLimit: Int,
        midIndex: Int,
        prefix: String,
        cities: ArrayList<CityModel>
    ): ArrayList<CityModel> {

        var lowLimitInterval: Int = midIndex
        var highLimitInterval: Int = midIndex

        if (midIndex > 0) {

            val aboveIndexValue =
                cities[midIndex - 1].name
                    ?.toLowerCase(
                        Locale.getDefault()
                    ) ?: Constants.EMPTY_STRING

            lowLimitInterval =
                when (aboveIndexValue.startsWith(prefix)) {
                    true ->
                        binarySearchToFindLowLimit(
                            lowIndexLimit,
                            midIndex - 1,
                            midIndex,
                            prefix,
                            cities
                        )
                    else -> midIndex
                }
        }

        if (midIndex < cities.size - 1) {

            val belowIndexValue =
                cities[midIndex + 1].name
                    ?.toLowerCase(
                        Locale.getDefault()
                    ) ?: Constants.EMPTY_STRING

            highLimitInterval =
                when (belowIndexValue.startsWith(prefix)) {
                    true ->
                        binarySearchToFindHighLimit(
                            midIndex + 1,
                            highIndexLimit,
                            midIndex,
                            prefix,
                            cities
                        )
                    else ->  midIndex
                }
        }

        return ArrayList(
            cities.subList(
                lowLimitInterval,
                highLimitInterval + 1
            )
        )

    }

    private fun binarySearchToFindLowLimit(
        low: Int,
        high: Int,
        defaultIndex: Int,
        prefix: String,
        cities: ArrayList<CityModel>
    ): Int {

        var lowIndexLimit = low
        var highIndexLimit = high

        while (lowIndexLimit <= highIndexLimit) {

            val midIndex = lowIndexLimit + ((highIndexLimit - lowIndexLimit) / 2)
            val midIndexValue =
                cities[midIndex].name
                    ?.toLowerCase(
                        Locale.getDefault()
                    ) ?: Constants.EMPTY_STRING

            when {
                midIndexValue.startsWith(prefix) -> {

                    when (midIndex > 0) {
                        true -> {

                            val aboveIndexValue =
                                cities[midIndex - 1].name
                                    ?.toLowerCase(
                                        Locale.getDefault()
                                    )  ?: Constants.EMPTY_STRING

                            when (aboveIndexValue.startsWith(prefix)) {
                                true -> highIndexLimit = midIndex - 1
                                else -> return midIndex
                            }

                        }
                        else -> return midIndex
                    }

                }
                prefix > midIndexValue -> lowIndexLimit = midIndex + 1
                prefix < midIndexValue -> highIndexLimit = midIndex - 1
            }

        }

        return defaultIndex

    }

    private fun binarySearchToFindHighLimit(
        low: Int,
        high: Int,
        defaultIndex: Int,
        prefix: String,
        cities: ArrayList<CityModel>
    ): Int {


        var lowIndexLimit = low
        var highIndexLimit = high

        while (lowIndexLimit <= highIndexLimit) {

            val midIndex = lowIndexLimit + ((highIndexLimit - lowIndexLimit) / 2)
            val midIndexValue =
                cities[midIndex].name
                    ?.toLowerCase(
                        Locale.getDefault()
                    )  ?: Constants.EMPTY_STRING

            when {
                midIndexValue.startsWith(prefix) -> {

                    if (midIndex < cities.size - 1) {
                        val belowIndexValue =
                            cities[midIndex + 1].name
                                ?.toLowerCase(
                                    Locale.getDefault()
                                )  ?: Constants.EMPTY_STRING

                        when (belowIndexValue.startsWith(prefix)) {
                            true -> lowIndexLimit = midIndex + 1
                            else ->  return midIndex
                        }
                    }

                }
                prefix > midIndexValue -> lowIndexLimit = midIndex + 1
                prefix < midIndexValue -> highIndexLimit = midIndex - 1
            }

        }

        return defaultIndex

    }

}