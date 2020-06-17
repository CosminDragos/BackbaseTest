package ro.iss.backbasetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import ro.iss.backbasetest.callbacks.FragmentCallback
import ro.iss.backbasetest.cities.CitiesFragment
import ro.iss.backbasetest.utils.Constants.ONE_FRAGMENT_LEFT

class MainActivity : AppCompatActivity(), FragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        takeIf { savedInstanceState == null }
                ?.let { openFragmentPage(CitiesFragment.newInstance()) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.backStackEntryCount) {
            ONE_FRAGMENT_LEFT -> finish()
            else -> supportFragmentManager.popBackStack()
        }
    }

    override fun openFragmentPage(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .add(
                R.id.container,
                fragment,
                fragment.tag
            )
            .addToBackStack(fragment.tag)
            .commit()
    }

}