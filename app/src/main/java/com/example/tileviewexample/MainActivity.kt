package com.example.tileviewexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.qozix.tileview.TileView
import com.qozix.tileview.widgets.ZoomPanLayout
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tileView: TileView
    private lateinit var imageView: ImageView
    private var timer: Timer? = null
    private var currentPosition: Int = 0
    private var positionDelta: Int = 0

    private var floorWidth = 1669
    private var floorHeight = 1336

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tileView = TileView(this)
        tileView.id = R.id.tileview_id
        tileView.isSaveEnabled = true

        setContentView(tileView)

        tileView.setSize(floorWidth, floorHeight)

        // we're running from assets, should be fairly fast decodes, go ahead and render asap
        tileView.setShouldRenderWhilePanning(true)

        // detail levels
        tileView.addDetailLevel(1f, "tiles/floors1/1000/%d_%d.jpg")
        tileView.addDetailLevel(0.500f, "tiles/floors1/500/%d_%d.jpg")
        tileView.addDetailLevel(0.250f, "tiles/floors1/250/%d_%d.jpg")
        tileView.addDetailLevel(0.125f, "tiles/floors1/125/%d_%d.jpg")

        tileView.defineBounds(0.0, 0.0, floorWidth.toDouble(), floorHeight.toDouble())
        tileView.setScaleLimits(0f, 5f)
        tileView.setMinimumScaleMode(ZoomPanLayout.MinimumScaleMode.FIT)

        // center markers along both axes
        tileView.setMarkerAnchorPoints(-0.5f, -0.5f)

        imageView = ImageView(this)
        imageView.setImageResource(R.drawable.ic_brightness_1_black_24dp)

        runLocationListener()
    }

    override fun onPause() {
        super.onPause()
        if (timer != null) {
            timer!!.cancel()
        }
        tileView.pause()
    }

    override fun onResume() {
        super.onResume()

        tileView.resume()
        runLocationListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timer != null) {
            timer!!.cancel()
        }
        tileView.destroy()
    }

    private fun addPin(x: Double, y: Double) {

        if (imageView.parent != null) {
            tileView.removeMarker(imageView)
        }
        tileView.addMarker(imageView, x, floorHeight - y, null, null)
    }

    private fun runLocationListener() {
        if (timer != null) {
            timer!!.cancel()
        }

        timer = Timer()
        currentPosition = 650
        positionDelta = 10

        timer!!.scheduleAtFixedRate(object : TimerTask() {

            override fun run() {
                runOnUiThread { addPin(currentPosition.toDouble(), 500.0) }
                currentPosition += positionDelta

                if (currentPosition < 650 || currentPosition > 800) {
                    positionDelta = -positionDelta
                }
            }
        }, 0, 1000)
    }
}
