package com.crazygoat.inkubator

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.hardware.ConsumerIrManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    private lateinit var manager: ConsumerIrManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val coordinatorLayout = findViewById(R.id.coordinatorLayout) as CoordinatorLayout

        this.manager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager
        if (this.manager.hasIrEmitter()) {
            val ranges = this.manager.carrierFrequencies

            Snackbar.make(coordinatorLayout, "IR DEVICE FOUND", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            ranges.forEach {
                Logger.getLogger(MainActivity::class.java.name).warning(it.minFrequency.toString() +" = "+it.maxFrequency.toString())
            }
        } else {
            Snackbar.make(coordinatorLayout, "IR DEVICE NOT FOUND", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }


        plus.setOnClickListener { view ->
            Snackbar.make(view, "Plus clicked", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }
        minus.setOnClickListener { view ->
            Snackbar.make(view, "Minus clicked", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
