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

/**
 * Class used for inflating graphic cells and binding data elements on them.
 *
 * @property context Activity Context
 * @property citiesList elements list to bind data from
 * @property itemClickListener callback listener for onClick Item Events
 */
class CitiesAdapter(
    private val context: Context?,
    private var citiesList: ArrayList<CityModel>,
    private val itemClickListener: OnCityItemClickListener
): RecyclerView.Adapter<CityViewHolder>() {

    /**
     * Called when RecyclerView needs a new CityViewHolder of the given type to represent
     * the CityModel item.
     * The CityViewHolder will be used to display cities using onBindViewHolder.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return A new CityViewHolder that holds a View of the given view type.
     */
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

    /**
     * Returns the total number of cities in the list set held by the adapter.
     *
     * @return The total number of cities in this adapter.
     */
    override fun getItemCount(): Int {
        return citiesList.size
    }

    /**
     * Called by RecyclerView to bind the data at the specified position. This method should
     * update the contents of the CityViewHolder to reflect the item at the given
     * position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(citiesList[position], position)
    }

    /**
     * Method used to update items list after search filtering.
     * This method is synchronized to avoid having different coroutines operating
     * with cities list data at the same time.
     *
     * @param citiesList filtered cities list
     */
    @Synchronized
    fun updateCitiesList(citiesList: ArrayList<CityModel>) {
        this.citiesList = citiesList
        notifyDataSetChanged()
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

        /**
         * Method used to bind data from CityModel to CityViewHolder associated views.
         *
         * @param item data object to map on graphic elements
         * @param position position of item in list
         */
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

        /**
         * Method used to convert dp to px.
         *
         * @param dp value to convert
         * @return converted value
         */
        private fun convertDpToPx(dp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context?.resources?.displayMetrics
            )
        }

        /**
         * Method used to set title in the associated view element.
         *
         * @param item data object to map on the graphic element
         */
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

        /**
         * Method used to set subtitle in the associated view element.
         *
         * @param item data object to map on the graphic element
         */
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


        /**
         * Method used to set click listener on the associated view element.
         *
         * @param item data object to that was mapped on the graphic element
         */
        private fun setOnItemListeners(item: CityModel) {
            cityView.setOnClickListener {
                itemClickListener.onItemClick(
                    item,
                    cityTitle?.text?.toString()
                )
            }
        }

    }

    /**
     * Callback Interface Listener
     *
     */
    interface OnCityItemClickListener {
        fun onItemClick(item: CityModel, markerTitle: String?)
    }

}