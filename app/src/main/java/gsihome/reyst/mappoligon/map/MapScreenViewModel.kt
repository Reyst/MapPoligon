package gsihome.reyst.mappoligon.map

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import gsihome.reyst.mappoligon.map.data.Position
import gsihome.reyst.mappoligon.map.data.Report

class MapScreenViewModel : ViewModel() {

    private val ldCurrentPosition: MutableLiveData<Position> = MutableLiveData()
    private val ldPositions: MutableLiveData<List<Position>> = MutableLiveData()
    private val ldReportVisibility: MutableLiveData<Boolean> = MutableLiveData()
    private val ldReport: MutableLiveData<Report> = MutableLiveData()

    init {
        ldPositions.value = ArrayList()
        ldReportVisibility.value = false
    }

    fun clear() {
        ldReportVisibility.value = false
        ldPositions.value = ArrayList()
        ldReport.value = null
    }

    fun getObservableCurrentPosition(): LiveData<Position> = ldCurrentPosition

    fun getObservablePositions(): LiveData<List<Position>> = ldPositions

    fun getObservableReportVisibility(): LiveData<Boolean> = ldReportVisibility

    fun getObservableReport(): LiveData<Report> = ldReport

    fun updatePosition(index: Int, position: Position) {
        val currentData: MutableList<Position> = ArrayList(ldPositions.value ?: emptyList())

        if (currentData.size <= index) currentData.add(position)
        else currentData[index] = position

        ldPositions.value = currentData
    }

    fun updateCurrentPosition(position: Position) {
        ldCurrentPosition.value = position
    }

    fun toggleReportWindowVisibility() {
        ldReportVisibility.value = !(ldReportVisibility.value ?: false)
    }

}