package ro.iss.backbasetest.cities

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ro.iss.backbasetest.R

class CitiesFragment : Fragment() {

    companion object {
        fun newInstance() = CitiesFragment()
    }

    private lateinit var viewModel: CitiesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CitiesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}