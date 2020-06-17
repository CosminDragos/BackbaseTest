package ro.iss.backbasetest.utils

import ro.iss.backbasetest.model.CityModel
import java.util.*
import kotlin.collections.ArrayList

/**
 * Object class used for search filtering.
 * The main implemented algorithm is Binary Search and is adapted according
 * to the requirements needed
 *
 */
object SearchUtils {

    /**
     * The cities list used for processing the search filtering.
     *
     * I chose this type of data representation because:
     * Binary Search doesn't need other additional arrays/maps/tries to perform
     * the search as fast as possible and it helps a lot to keep an O(1) spatial complexity
     * regardless of type of scenarios.
     * Beside time complexity, spatial complexity it's another important element
     * that I want to keep it as clean as possible and this algorithm helped me
     * to accomplish my task easily.
     * Obviously, there are another algorithms that are using a lot of data representations
     * to perform a fast search, but I think this one has a balance between the two of them.
     * Also, I needed for getting much easier a sublist from a range interval
     * and ArrayList has an O(1) time complexity to achieve that.
     * Important to mention that this list is also a sorted one
     * because it's an important criteria needed to perform this type of search.
     *
     */
    var citiesList: ArrayList<CityModel>? = null

    /**
     * This method implements the filtering by prefix using binary search algorithm adapted.
     * Instead of searching for only one element in our list, we are searching
     * for interval limits (the lowest one and the highest one). We need to find out the
     * range of our prefix.
     * Basically, until we find at least one element that contains the prefix,
     * the flows for finding lowest and highest interval limits are the same.
     * I chose to implement it in an iterative way to keep the spatial complexity
     * in the worst case scenario at O(1).
     *
     * @param prefix the filtering element
     * @return a list of the cities filtered
     */
    @Synchronized
    fun binarySearch(prefix: String): ArrayList<CityModel> {

        citiesList?.let {

            if (prefix.isBlank())
                return it

            // low and high indexes
            var lowIndexLimit = 0
            var highIndexLimit = it.size - 1

            // We iterate as long as we will have elements to search for
            // Otherwise, it stops and returns an empty list
            while (lowIndexLimit <= highIndexLimit) {

                // in each iteration we are taking the mid element index
                val midIndex = lowIndexLimit + ((highIndexLimit - lowIndexLimit) / 2)

                //mid element index value
                val midIndexValue =
                    it[midIndex].name
                        ?.toLowerCase(
                            Locale.getDefault()
                        ) ?: Constants.EMPTY_STRING

                when {

                    // If the mid element startsWith the prefix
                    midIndexValue.startsWith(prefix) ->
                        return foundWordWithPrefix(
                            lowIndexLimit,
                            highIndexLimit,
                            midIndex,
                            prefix,
                            it
                        )

                    // If prefix is bigger than mid, then
                    // it can only be present in right sublist
                    prefix > midIndexValue ->
                        lowIndexLimit = midIndex + 1

                    // If prefix is smaller than mid, then
                    // it can only be present in left sublist
                    prefix < midIndexValue ->
                        highIndexLimit = midIndex - 1

                }

            }

        }

        return ArrayList()

    }

    /**
     * The purpose of this method is to separate the searches flows needed to find out
     * the interval limits.
     * We are first looking for the lowest limit interval, and then for the highest one.
     * At this point, it's basically two binary searches.
     *
     * @param lowIndexLimit the lowest index of the sublist you need to search in
     * @param highIndexLimit the highest index of the sublist you need to search in
     * @param midIndex the index of the element we know for sure it startsWith prefix
     * @param prefix the filtering element
     * @param cities the unfiltered list of the cities
     * @return a list of the cities filtered
     */
    private fun foundWordWithPrefix(
        lowIndexLimit: Int,
        highIndexLimit: Int,
        midIndex: Int,
        prefix: String,
        cities: ArrayList<CityModel>
    ): ArrayList<CityModel> {

        // We assume at first the we have only one element found in our range
        var lowLimitInterval: Int = midIndex
        var highLimitInterval: Int = midIndex

        // Made the check to make sure that it's not the first element in ArrayList
        if (midIndex > 0) {

            // the above element from the one that we know
            // for sure it starts with the prefix
            val aboveIndexValue =
                cities[midIndex - 1].name
                    ?.toLowerCase(
                        Locale.getDefault()
                    ) ?: Constants.EMPTY_STRING


            lowLimitInterval =
                when (aboveIndexValue.startsWith(prefix)) {

                    // If startsWith prefix, it means that mid index value
                    // is not the lowest limit interval
                    // and we need to go further
                    // and apply binary search for the above sublist
                    true ->
                        binarySearchToFindLowLimit(
                            lowIndexLimit,
                            midIndex - 1,
                            midIndex,
                            prefix,
                            cities
                        )

                    // If it doesn't startsWith prefix,
                    // it means we found out the lowest limit interval
                    else -> midIndex
                }
        }

        // Made the check to make sure that it's not the last element in ArrayList
        if (midIndex < cities.size - 1) {

            // the below element from the one that we know
            // for sure it starts with the prefix
            val belowIndexValue =
                cities[midIndex + 1].name
                    ?.toLowerCase(
                        Locale.getDefault()
                    ) ?: Constants.EMPTY_STRING

            highLimitInterval =
                when (belowIndexValue.startsWith(prefix)) {

                    // If startsWith prefix, it means that mid index value
                    // is not the highest limit interval
                    // and we need to go further
                    // and apply binary search for the below sublist
                    true ->
                        binarySearchToFindHighLimit(
                            midIndex + 1,
                            highIndexLimit,
                            midIndex,
                            prefix,
                            cities
                        )

                    // If it doesn't startsWith prefix,
                    // it means we found out the highest limit interval
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

    /**
     * It'basically a binary search that is looking for the lowest limit interval
     *
     * @param low the lowest index of the sublist you need to search in
     * @param high the highest index of the sublist you need to search in
     * @param defaultIndex the index of the element we know for sure it startsWith prefix
     * @param prefix the filtering element
     * @param cities the unfiltered list of the cities
     * @return the lowest limit range of the elements that startWith prefix
     */
    private fun binarySearchToFindLowLimit(
        low: Int,
        high: Int,
        defaultIndex: Int,
        prefix: String,
        cities: ArrayList<CityModel>
    ): Int {

        // low and high indexes
        var lowIndexLimit = low
        var highIndexLimit = high

        // We iterate as long as we will have elements to search for
        // Otherwise, it stops and returns the defaultIndex
        while (lowIndexLimit <= highIndexLimit) {

            // in each iteration we are taking the mid element index
            val midIndex = lowIndexLimit + ((highIndexLimit - lowIndexLimit) / 2)

            // mid element index value
            val midIndexValue =
                cities[midIndex].name
                    ?.toLowerCase(
                        Locale.getDefault()
                    ) ?: Constants.EMPTY_STRING

            when {

                // If the mid element startsWith the prefix
                midIndexValue.startsWith(prefix) -> {

                    // Made the check to make sure that it's not
                    // the first element in ArrayList
                    when (midIndex > 0) {

                        // if it's not the first element, we can check the one above him
                        true -> {

                            // the above element from the one that we know
                            // for sure it starts with the prefix
                            val aboveIndexValue =
                                cities[midIndex - 1].name
                                    ?.toLowerCase(
                                        Locale.getDefault()
                                    )  ?: Constants.EMPTY_STRING

                            when (aboveIndexValue.startsWith(prefix)) {

                                // If startsWith prefix, it means that mid index value
                                // is not the lowest limit interval
                                // and we need to go further
                                // and apply binary search for the above sublist
                                true -> highIndexLimit = midIndex - 1

                                // If it doesn't startsWith prefix,
                                // it means we found out the lowest limit interval
                                else -> return midIndex

                            }

                        }

                        // otherwise the search ends and midIndex is the low limit range
                        else -> return midIndex

                    }

                }

                // If prefix is bigger than mid, then
                // it can only be present in right sublist
                prefix > midIndexValue -> lowIndexLimit = midIndex + 1

                // If prefix is smaller than mid, then
                // it can only be present in left sublist
                prefix < midIndexValue -> highIndexLimit = midIndex - 1

            }

        }

        return defaultIndex

    }

    /**
     * It'basically a binary search that is looking for the highest limit interval
     *
     * @param low the lowest index of the sublist you need to search in
     * @param high the highest index of the sublist you need to search in
     * @param defaultIndex the index of the element we know for sure it startsWith prefix
     * @param prefix the filtering element
     * @param cities the unfiltered list of the cities
     * @return the highest limit range of the elements that startWith prefix
     */
    private fun binarySearchToFindHighLimit(
        low: Int,
        high: Int,
        defaultIndex: Int,
        prefix: String,
        cities: ArrayList<CityModel>
    ): Int {

        // low and high indexes
        var lowIndexLimit = low
        var highIndexLimit = high

        // We iterate as long as we will have elements to search for
        // Otherwise, it stops and returns the defaultIndex
        while (lowIndexLimit <= highIndexLimit) {

            // in each iteration we are taking the mid element index
            val midIndex = lowIndexLimit + ((highIndexLimit - lowIndexLimit) / 2)

            // mid element index value
            val midIndexValue =
                cities[midIndex].name
                    ?.toLowerCase(
                        Locale.getDefault()
                    )  ?: Constants.EMPTY_STRING

            when {

                // If the mid element startsWith the prefix
                midIndexValue.startsWith(prefix) -> {

                    // Made the check to make sure that it's not
                    // the last element in ArrayList
                    if (midIndex < cities.size - 1) {

                        // the below element from the one that we know
                        // for sure it starts with the prefix
                        val belowIndexValue =
                            cities[midIndex + 1].name
                                ?.toLowerCase(
                                    Locale.getDefault()
                                )  ?: Constants.EMPTY_STRING

                        when (belowIndexValue.startsWith(prefix)) {

                            // If startsWith prefix, it means that mid index value
                            // is not the highest limit interval
                            // and we need to go further
                            // and apply binary search for the below sublist
                            true -> lowIndexLimit = midIndex + 1

                            // If it doesn't startsWith prefix,
                            // it means we found out the highest limit interval
                            else ->  return midIndex

                        }

                    }

                }

                // If prefix is bigger than mid, then
                // it can only be present in right sublist
                prefix > midIndexValue -> lowIndexLimit = midIndex + 1

                // If prefix is smaller than mid, then
                // it can only be present in left sublist
                prefix < midIndexValue -> highIndexLimit = midIndex - 1
            }

        }

        return defaultIndex

    }

}