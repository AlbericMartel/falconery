package am.falconry

import am.falconry.databinding.ActivityMainBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navController = findNavController(R.id.myNavHostFragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.topAppBar.setupWithNavController(
            navController,
            appBarConfiguration
        )
    }
}
