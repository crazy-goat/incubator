package com.crazygoat.inkubator

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import android.hardware.ConsumerIrManager
import android.view.View
import android.os.VibrationEffect
import android.os.Build
import android.os.Vibrator

class MainActivity : AppCompatActivity() {

    private var plusIRMsg = intArrayOf(907, 814, 4376, 813, 904)
    private var minusIRMsg = intArrayOf(1771, 821, 3499, 823, 869)
    private var selectedFreq = 0
    private var freqList = mutableListOf<Int>()
    private lateinit var manager: ConsumerIrManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initIR()

        plus.setOnClickListener {
            this.manager.transmit(freqList[selectedFreq], this.plusIRMsg)
            this.vibrate()
        }
        minus.setOnClickListener {
            this.manager.transmit(freqList[selectedFreq], this.minusIRMsg)
            this.vibrate()
        }
    }

    private fun initIR() {
        this.manager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager
        if (this.manager.hasIrEmitter()) {
            val ranges = this.manager.carrierFrequencies
            ranges.forEach {
                freqList.add(it.minFrequency)
            }
            this.setFrequency(0)
        } else {
            Snackbar.make(findViewById<View>(R.id.coordinatorLayout), "IR DEVICE NOT FOUND", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }
    }

    private fun vibrate() {
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v.vibrate(150)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val freqMenu = menu.findItem(R.id.freq_menu)
        freqMenu.subMenu.clear()
        var nb = 0
        this.manager.carrierFrequencies.forEach {
            var item = freqMenu.subMenu.add(0, 100000 + nb, 0, it.maxFrequency.toString() + " Hz")
            item.setCheckable(true)
            item.setChecked(nb == selectedFreq)

            nb++
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.itemId >= 100000 && item.itemId < 100000 + freqList.size) {
            this.setFrequency(item.itemId - 100000)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setFrequency(index: Int) {
        val item = freqList.getOrNull(index)
        if (item === null) {
            Snackbar.make(findViewById<View>(R.id.coordinatorLayout), "FREQ NOT FOUND", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
            return
        }

        this.selectedFreq = index
    }
}
