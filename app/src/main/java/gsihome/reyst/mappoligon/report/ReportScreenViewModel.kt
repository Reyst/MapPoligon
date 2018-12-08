package gsihome.reyst.mappoligon.report

import android.arch.lifecycle.ViewModel
import gsihome.reyst.mappoligon.map.data.Report

class ReportScreenViewModel : ViewModel() {

    var report: Report? = null
        private set

    fun initReport(report: Report) {
        this.report = report
    }
}