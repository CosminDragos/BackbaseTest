package ro.iss.backbasetest.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ro.iss.backbasetest.R
import ro.iss.backbasetest.adapter.CitiesAdapter.CityViewHolder
import ro.iss.backbasetest.model.CityModel
import ro.iss.backbasetest.utils.Constants.DELIMITER_CITY_STRING
import ro.iss.backbasetest.utils.Constants.DELIMITER_COORDINATES_STRING
import ro.iss.backbasetest.utils.Constants.EMPTY_STRING
import ro.iss.backbasetest.utils.Constants.FIRST_ELEMENT_INDEX
import ro.iss.backbasetest.utils.Constants.PADDING_VALUE_24DP
import ro.iss.backbasetest.utils.Constants.PADDING_VALUE_12DP


class CitiesAdapter(
    private val context: Context?,
    private var citiesList: ArrayList<CityModel>,
    private val itemClickListener: OnCityItemClickListener
): RecyclerView.Adapter<CityViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityViewHolder {
        return CityViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.city_item_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return citiesList.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(citiesList[position], position)
    }

    @Synchronized
    fun updateCitiesList(citiesList: ArrayList<CityModel>) {

        this.citiesList = citiesList
        notifyDataSetChanged()

//        val task: RunnableFuture<Void?> =
//            FutureTask(
//                Runnable {
//                    this.citiesList = citiesList
//                    notifyDataSetChanged()
//                },
//                null
//            )
//
//        (context as? Activity)?.runOnUiThread(task)
//
//        try {
//            // this will suspend coroutine that called the method until UI updates are done
//            task.get()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

    }

    inner class CityViewHolder(
        private val cityView: View
    ) : RecyclerView.ViewHolder(cityView) {

        private val cityTitle: TextView?
        private val citySubtitle: TextView?

        init {
            with(cityView) {
                cityTitle = findViewById(R.id.city_title)
                citySubtitle = findViewById(R.id.city_subtitle)
            }
        }

        fun bind(item: CityModel, position: Int) {
            setPadding(position)
            setTitleValue(item)
            setSubTitleValue(item)
            setOnItemListeners(item)
        }

        private fun setPadding(position: Int) {
            when (position) {
                FIRST_ELEMENT_INDEX ->
                    cityView.setPadding(
                        convertDpToPx(PADDING_VALUE_24DP).toInt(),
                        convertDpToPx(PADDING_VALUE_24DP).toInt(),
                        convertDpToPx(PADDING_VALUE_24DP).toInt(),
                        convertDpToPx(PADDING_VALUE_12DP).toInt()
                    )
                itemCount - 1 ->
                    cityView.setPadding(
                        convertDpToPx(PADDING_VALUE_24DP).toInt(),
                        convertDpToPx(PADDING_VALUE_12DP).toInt(),
                        convertDpToPx(PADDING_VALUE_24DP).toInt(),
                        convertDpToPx(PADDING_VALUE_24DP).toInt()
                    )
                else ->
                    cityView.setPadding(
                        convertDpToPx(PADDING_VALUE_24DP).toInt(),
                        convertDpToPx(PADDING_VALUE_12DP).toInt(),
                        convertDpToPx(PADDING_VALUE_24DP).toInt(),
                        convertDpToPx(PADDING_VALUE_12DP).toInt()
                    )
            }
        }

        private fun convertDpToPx(dp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context?.resources?.displayMetrics
            )
        }

        private fun setTitleValue(item: CityModel) {

            val city = item.name ?: EMPTY_STRING
            val country = item.country ?: EMPTY_STRING

            val titleDelimiter = when {
                city.isEmpty() || country.isEmpty() -> EMPTY_STRING
                else -> DELIMITER_CITY_STRING
            }

            val titleValue ="$city$titleDelimiter$country"

            cityTitle?.text = titleValue

        }

        private fun setSubTitleValue(item: CityModel) {

            val longitude =
                item.coordinates
                    ?.longitude
                    ?.let { "Lon: $it" }
                    ?: EMPTY_STRING

            val latitude =
                item.coordinates
                    ?.latitude
                    ?.let { "Lat: $it" }
                    ?: EMPTY_STRING


            val subTitleDelimiter = when {
                longitude.isEmpty() || latitude.isEmpty() -> EMPTY_STRING
                else -> DELIMITER_COORDINATES_STRING
            }

            val subTitleValue = "$longitude$subTitleDelimiter$latitude"

            citySubtitle?.text = subTitleValue

        }

        private fun setOnItemListeners(item: CityModel) {
            cityView.setOnClickListener {
                itemClickListener.onItemClick(
                    item,
                    cityTitle?.text?.toString()
                )
            }
        }

    }

    interface OnCityItemClickListener {
        fun onItemClick(item: CityModel, markerTitle: String?)
    }

}