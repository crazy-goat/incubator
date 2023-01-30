package com.crazygoat.inkubator

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.hardware.ConsumerIrManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var plusIRMsg = intArrayOf(907, 814, 4376, 813, 904)
    private var minusIRMsg = intArrayOf(1771, 821, 3499, 823, 869)
    private var selectedFreq = 0
    private var freqList = mutableListOf<Int>()
    private lateinit var manager: ConsumerIrManager
    private lateinit var mAdView: AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initAds()
        initIR()
        registerButtons()
    }

    private fun registerButtons() {
        plus.setOnClickListener { this.transmit(this.plusIRMsg); }
        minus.setOnClickListener { this.transmit(this.minusIRMsg); }
    }

    private fun transmit(data: IntArray) {
        if (this.manager.hasIrEmitter()) {
            this.manager.transmit(freqList[selectedFreq], data)
            this.vibrate()
        } else {
            displayNoIRWarning()
        }
    }

    private fun initAds() {
        MobileAds.initialize(this)
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder()

        mAdView.loadAd(adRequest.build())
    }

    private fun displayNoIRWarning() {
        Snackbar.make(
            findViewById(R.id.coordinatorLayout),
            getString(R.string.no_ir_text),
            Snackbar.LENGTH_LONG
        )
            .setAction("Action", null)
            .show()
    }

    private fun initIR() {
        this.manager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager
        if (this.manager.hasIrEmitter()) {
            val ranges = this.manager.carrierFrequencies
            ranges.forEach {
                freqList.add(it.minFrequency)
            }

            this.setFrequency(
                getPreferences(Context.MODE_PRIVATE).getInt(
                    getString(R.string.settigns_freq_key),
                    0
                )
            )
        } else {
            this.displayNoIRWarning()
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
        if (freqList.size > 0) {
            freqMenu.isVisible = true
            freqMenu.subMenu?.clear()
            var nb = 0
            freqList.forEach {
                val item = freqMenu.subMenu?.add(0, 100000 + nb, 0, it.toString() + " Hz")
                item?.isCheckable = true
                item?.isChecked = nb == selectedFreq

                nb++
            }
        } else {
            freqMenu.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId >= 100000 && item.itemId < 100000 + freqList.size) {
            val freqId = item.itemId - 100000
            this.setFrequency(freqId)
            return true
        } else {
            when (item.itemId) {
                R.id.action_homepage -> {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                getString(R.string.home_page)
                            )
                        )
                    )
                    return true
                }
                R.id.action_help -> {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle(getString(R.string.dialog_help_title))
                    builder.setMessage(getString(R.string.dialog_help_message))
                    builder.setPositiveButton(getString(R.string.dialog_close)) { _, _ ->

                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setFrequency(index: Int) {
        val item = freqList.getOrNull(index)
        if (item === null) {
            Snackbar.make(
                findViewById<View>(R.id.coordinatorLayout),
                "FREQ NOT FOUND",
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null)
                .show()
            return
        }

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(getString(R.string.settigns_freq_key), index)
            apply()
        }


        this.selectedFreq = index
    }
}
