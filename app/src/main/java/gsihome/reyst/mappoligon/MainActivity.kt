package gsihome.reyst.mappoligon

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import gsihome.reyst.mappoligon.map.MapsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_start)
                .setOnClickListener {
                    Intent(this, MapsActivity::class.java)
                            .also {intent -> startActivity(intent) }
                }
    }
}
