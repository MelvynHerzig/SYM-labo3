package ch.heigvd.iict.sym.labo3.manipulations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.heigvd.iict.sym.labo3.R

/**
 * Activité liée à l'utilisation / détection des codes barres et code QR
 * @author Berney Alec
 * @author Forestier Quentin
 * @author Herzig Melvyn
 */
class CodeBarreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_barre)
    }
}