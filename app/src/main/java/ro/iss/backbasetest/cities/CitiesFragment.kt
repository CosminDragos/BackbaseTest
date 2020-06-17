package ro.iss.backbasetest.cities

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.cities_fragment.*
import ro.iss.backbasetest.MainActivity
import ro.iss.backbasetest.MapFragment
import ro.iss.backbasetest.R
import ro.iss.backbasetest.adapter.CitiesAdapter
import ro.iss.backbasetest.adapter.CitiesAdapter.OnCityItemClickListener
import ro.iss.backbasetest.callbacks.FragmentCallback
import ro.iss.backbasetest.model.CityModel
import ro.iss.backbasetest.model.Resource.*
import ro.iss.backbasetest.utils.Constants.JSON_CITIES_FILENAME
import java.util.*

/**
 * The fragment needed for showing the cities list
 *
 */
class CitiesFragment : Fragment(), OnQueryTextListener {

    companion object {
        fun newInstance() = CitiesFragment()
    }

    /**
     * Cities ViewModel to get the cities list, perform the search filtering
     */
    private var viewModel: CitiesViewModel? = null

    /**
     * SearchView element UI used by the user to insert the filtering prefixes
     */
    private var searchView: SearchView? = null

    /**
     * Cities Adapter needed for RecyclerView to map the data list elements
     * on the graphic cells
     */
    private var citiesAdapter: CitiesAdapter? = null

    /**
     * Callback need for communication with the Activity
     */
    private var fragmentCallback: FragmentCallback? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentCallback = context as? FragmentCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.cities_fragment,
            container,
            false
        )
    }

    override fun onActivityCreated(
        savedInstanceState: Bundle?
    ) {

        super.onActivityCreated(savedInstanceState)

        (context as? MainActivity)
            ?.supportActionBar
            ?.title =
            getString(
                R.string.cities_page_title
            )

        viewModel =
            ViewModelProvider(this)
                .get(CitiesViewModel::class.java)

        observeLiveData()
        viewModel?.getCities(
            context,
            JSON_CITIES_FILENAME
        )

    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {

        inflater.inflate(R.menu.menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchManager =
            activity?.getSystemService(Context.SEARCH_SERVICE) as? SearchManager

        searchItem?.let {

            searchView = it.actionView as? SearchView
            searchView?.queryHint = getString(R.string.search_title_hint)

        }

        searchView?.let {

            val options: Int = it.imeOptions
            it.imeOptions = options or EditorInfo.IME_FLAG_NO_EXTRACT_UI

            it.setSearchableInfo(
                searchManager?.getSearchableInfo(
                    activity?.componentName
                )
            )

            it.setOnQueryTextListener(this)

        }

        super.onCreateOptionsMenu(menu, inflater)

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Method called to set the observer on the live data elements from ViewModel
     * to listen each time a value is emitted.
     * Also manages the observer resource response to decide the type of flow needed
     * to go further: Loading flow, Empty List Flow, Error Flow, Success Flow.
     *
     */
    private fun observeLiveData() {

        viewModel?.citiesLiveData
            ?.observe(
                viewLifecycleOwner,
                Observer { resource ->
                    when (resource) {
                        is Loading ->
                            showLoadingCities()
                        is Success ->
                            setCitiesList(
                                resource.data
                            )
                        is Empty ->
                            showEmptyCities()
                        is Error ->
                            showErrorCities()
                    }
                }
            )

        viewModel?.searchLiveData
            ?.observe(
                viewLifecycleOwner,
                Observer { resource ->
                    when (resource) {
                        is Loading ->
                            showLoadingCities()
                        is Update ->
                            updateCitiesList(
                                resource.data
                            )
                        is Empty ->
                            showEmptyCities()
                        is Error ->
                            showErrorCities()
                    }
                }
            )

    }

    /**
     * Method called to show Loading Container and hide anything else from UI user interface.
     *
     */
    private fun showLoadingCities() {
        cities_loading_progress?.visibility = VISIBLE
        cities_error_container?.visibility = GONE
        cities_empty_container?.visibility = GONE
        cities_recycler_view?.visibility = GONE
        searchView?.visibility = VISIBLE
    }

    /**
     * Method called to show Error Container and hide anything else from UI user interface.
     *
     */
    private fun showErrorCities() {
        cities_error_container?.visibility = VISIBLE
        cities_loading_progress?.visibility = GONE
        cities_empty_container?.visibility = GONE
        cities_recycler_view?.visibility = GONE
        searchView?.visibility = GONE
    }

    /**
     * Method called to show Empty List Container and hide anything else from UI user interface.
     *
     */
    private fun showEmptyCities() {
        cities_empty_container?.visibility = VISIBLE
        cities_error_container?.visibility = GONE
        cities_loading_progress?.visibility = GONE
        cities_recycler_view?.visibility = GONE
        searchView?.visibility = VISIBLE
    }

    /**
     * Method called to show RecyclerView Container with listed citiesand hide anything else
     * from UI user interface.
     *
     */
    private fun showListOfTheCities() {
        cities_recycler_view?.visibility = VISIBLE
        cities_empty_container?.visibility = GONE
        cities_error_container?.visibility = GONE
        cities_loading_progress?.visibility = GONE
        searchView?.visibility = VISIBLE
    }

    /**
     * Method called on Success Flow, after content was parsed and sorted
     * and successfully retrieved.
     *
     * @param citiesList ArrayList of parsed and sorted cities
     */
    private fun setCitiesList(citiesList: ArrayList<CityModel>?) {
        citiesList?.let {
            showListOfTheCities()
            citiesAdapter = CitiesAdapter(
                context,
                it,
                object: OnCityItemClickListener {

                    override fun onItemClick(
                        item: CityModel,
                        markerTitle: String?
                    ) = goToMapFragment(item, markerTitle)

                }
            )
            cities_recycler_view.layoutManager = LinearLayoutManager(context)
            cities_recycler_view.adapter = citiesAdapter
            searchView?.let {
                if (!searchView?.query.isNullOrEmpty())
                    onQueryTextChange(searchView?.query.toString())
            }
        } ?: showErrorCities()
    }

    /**
     * Method used for fragment redirection to Map interface
     *
     * @param item city data object of the one clicked by the user
     * @param markerTitle concatenation between city and country code
     */
    private fun goToMapFragment(
        item: CityModel,
        markerTitle: String?
    ) {

        item.coordinates?.latitude?.let { lat ->

            item.coordinates?.longitude?.let { lon ->

                fragmentCallback
                    ?.openFragmentPage(
                        MapFragment.newInstance(
                            LatLng(lat, lon),
                            markerTitle
                        )
                    )

            } ?: incorrectCoordinates()

        } ?: incorrectCoordinates()

    }

    /**
     * Method used to show error Toast if coordinates are null or have wrong format
     *
     */
    private fun incorrectCoordinates() {
        Toast.makeText(
            context,
            getString(
                R.string.toast_incorrect_coordinates_text
            ),
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Method used to update the cities list from adapter when filtering was performed
     *
     * @param citiesList filtered cities list
     */
    private fun updateCitiesList(citiesList: ArrayList<CityModel>?) {
        citiesList?.let {
            showListOfTheCities()
            citiesAdapter?.updateCitiesList(it)
        } ?: showErrorCities()
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    /**
     * Method called each time user taps a character in SearchView Editable Text
     *
     * @param newText text to filter
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            if (cities_loading_progress.visibility == GONE)
                viewModel?.searchByPrefix(it)
        }
        return true
    }

}