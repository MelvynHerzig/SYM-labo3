package ch.heigvd.iict.sym.labo3.manipulations

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import ch.heigvd.iict.sym.labo3.R
import com.google.zxing.client.android.BeepManager

import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

import com.google.zxing.BarcodeFormat
import java.util.*
import com.google.zxing.ResultPoint
import android.widget.ImageView
import android.widget.TextView

import com.journeyapps.barcodescanner.BarcodeResult

import com.journeyapps.barcodescanner.BarcodeCallback





/**
 * Activité liée à l'utilisation / détection des codes barres et code QR
 * Adapté de: https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/ContinuousCaptureActivity.java
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class CodeBarreActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var beepManager: BeepManager

    private lateinit var imageView: ImageView

    private lateinit var lastText: TextView

    /**
     * À la création, initialisation de la vue, récupération des références.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_barre)

        barcodeView = findViewById(R.id.code_barre_scanner)
        val formats: Collection<BarcodeFormat> =
            Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.initializeFromIntent(intent)
        barcodeView.decodeContinuous(callback)

        beepManager = BeepManager(this)

        imageView = findViewById(R.id.code_barre_image)

        lastText = findViewById(R.id.code_barre_result_content)
    }

    /**
     * Callback lorsqu'un barre code est décodé.
     */
    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText.text) {
                // Prevent duplicate scans
                return
            }
            lastText.text = result.text
            beepManager.playBeepSoundAndVibrate()

            //Added preview of scanned barcode
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW))
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    /**
     * À la récupération
     */
    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    /**
     * Mise en pause
     */
    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    /**
     * Sur pression
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }
}