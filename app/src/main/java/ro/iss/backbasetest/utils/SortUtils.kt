package ro.iss.backbasetest.utils

import ro.iss.backbasetest.model.CityModel
import ro.iss.backbasetest.utils.Constants.EMPTY_STRING

object SortUtils {

    fun sortList(items: List<CityModel>): List<CityModel> {

        if (items.count() < 2){
            return items
        }

        val pivot = items[items.count() / 2]

        val equal = items.filter { it.name == pivot.name && it.country == pivot.country}
        val less = items.filter { it.name ?: EMPTY_STRING < pivot.name ?: EMPTY_STRING }
        val greater = items.filter { it.name ?: EMPTY_STRING > pivot.name ?: EMPTY_STRING }

        return sortList(less) + equal + sortList(greater)

    }

}