package gsihome.reyst.mappoligon.report

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import gsihome.reyst.mappoligon.R
import gsihome.reyst.mappoligon.map.data.toDomain
import gsihome.reyst.mappoligon.map.data.toDto
import gsihome.reyst.mappoligon.report.data.ReportDto

class ReportActivity : AppCompatActivity() {

    private lateinit var keyForResult: String

    private lateinit var viewModel: ReportScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        findViewById<Button>(R.id.btn_save_report)
                .setOnClickListener {
                    Intent().apply { putExtra(keyForResult, viewModel.report?.toDto()) }
                            .also { resultIntent -> setResult(Activity.RESULT_OK, resultIntent) }

                    finish()
                }

        viewModel = ViewModelProviders.of(this).get(ReportScreenViewModel::class.java)

        keyForResult = intent.getStringExtra(KEY_RESULT)
        val report: ReportDto = intent.getParcelableExtra(KEY_REPORT)

        if (viewModel.report == null)
            viewModel.initReport(report.toDomain())

    }

    companion object {

        private const val KEY_REPORT = "report"
        private const val KEY_RESULT = "result_key"

        fun startForResult(parent: Activity, report: ReportDto?, reqCode: Int, resultKey: String) {
            Intent(parent, ReportActivity::class.java)
                    .apply {
                        putExtra(KEY_RESULT, resultKey)
                        putExtra(KEY_REPORT, report ?: ReportDto())
                    }
                    .also { parent.startActivityForResult(it, reqCode) }
        }
    }
}
