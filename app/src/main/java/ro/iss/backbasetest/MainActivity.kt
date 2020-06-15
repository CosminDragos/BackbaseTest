package ro.iss.backbasetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import ro.iss.backbasetest.cities.CitiesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        takeIf { savedInstanceState == null }
                ?.let { addFragmentPage(CitiesFragment.newInstance()) }
    }

    private fun addFragmentPage(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
    }

}