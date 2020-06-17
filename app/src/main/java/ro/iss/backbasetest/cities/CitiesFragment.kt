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


class CitiesFragment : Fragment(), OnQueryTextListener {

    companion object {
        fun newInstance() = CitiesFragment()
    }

    private var viewModel: CitiesViewModel? = null
    private var searchView: SearchView? = null
    private var citiesAdapter: CitiesAdapter? = null
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
        searchView?.setOnQueryTextListener(this)
        return super.onOptionsItemSelected(item)
    }

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

    private fun showLoadingCities() {
        cities_loading_progress?.visibility = VISIBLE
        cities_error_container?.visibility = GONE
        cities_empty_container?.visibility = GONE
        cities_recycler_view?.visibility = GONE
    }

    private fun showErrorCities() {
        cities_error_container?.visibility = VISIBLE
        cities_loading_progress?.visibility = GONE
        cities_empty_container?.visibility = GONE
        cities_recycler_view?.visibility = GONE
    }

    private fun showEmptyCities() {
        cities_empty_container?.visibility = VISIBLE
        cities_error_container?.visibility = GONE
        cities_loading_progress?.visibility = GONE
        cities_recycler_view?.visibility = GONE
    }

    private fun showListOfTheCities() {
        cities_recycler_view?.visibility = VISIBLE
        cities_empty_container?.visibility = GONE
        cities_error_container?.visibility = GONE
        cities_loading_progress?.visibility = GONE
    }

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
        } ?: showErrorCities()
    }

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

    private fun incorrectCoordinates() {
        Toast.makeText(
            context,
            getString(
                R.string.toast_incorrect_coordinates_text
            ),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateCitiesList(citiesList: ArrayList<CityModel>?) {
        citiesList?.let {
            showListOfTheCities()
            citiesAdapter?.updateCitiesList(it)
        } ?: showErrorCities()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            viewModel?.searchByPrefix(it.toLowerCase(Locale.getDefault()))
        }
        return true
    }

}