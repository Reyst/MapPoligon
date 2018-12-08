package gsihome.reyst.mappoligon.map.data

import com.google.android.gms.maps.model.LatLng
import gsihome.reyst.mappoligon.report.data.ReportDto

fun LatLng.toPosition() : Position = Position(latitude, longitude)
fun Position.toLatLng() : LatLng = LatLng(lat, lng)

fun Collection<LatLng>.toPositions() : Collection<Position> = map { it.toPosition() }
fun Collection<Position>.toLatLngs() : Collection<LatLng> = map { it.toLatLng() }


fun Report.toDto(): ReportDto = ReportDto(description, date, imageFileName)
fun ReportDto.toDomain(): Report = Report(description, date, imageFileName)
