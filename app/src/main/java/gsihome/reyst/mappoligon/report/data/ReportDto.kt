package gsihome.reyst.mappoligon.report.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ReportDto(
        val description: String = "",
        val date: Date = Date(),
        val imageFileName: String? = null
) : Parcelable