package com.crazygoat.inkubator

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.hardware.ConsumerIrManager
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import java.util.logging.Logger
import android.os.VibrationEffect
import android.os.Build
import android.content.Context.VIBRATOR_SERVICE
import android.os.Vibrator



class MainActivity : AppCompatActivity() {

    private var plusIRMsg = intArrayOf(907, 814, 4376, 813, 904)
    private var minusIRMsg = intArrayOf(1771, 821, 3499, 823, 869)
    private var freq: Int = 0
    private var freqList = listOf<Int>()
    private lateinit var manager: ConsumerIrManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initIR()

        plus.setOnClickListener {
            this.manager.transmit(this.freq, this.plusIRMsg)
            this.vibrate()
        }
        minus.setOnClickListener {
            this.manager.transmit(this.freq, this.minusIRMsg)
            this.vibrate()
        }
    }

    private fun initIR() {
        this.manager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager
        if (this.manager.hasIrEmitter()) {
            val ranges = this.manager.carrierFrequencies
            this.freq = ranges.first().minFrequency
            ranges.forEach {
//                this.freqList.
            }
        } else {
            Snackbar.make(findViewById<View>(R.id.coordinatorLayout), "IR DEVICE NOT FOUND", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }
    }

    private fun vibrate() {
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v.vibrate(100)
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
