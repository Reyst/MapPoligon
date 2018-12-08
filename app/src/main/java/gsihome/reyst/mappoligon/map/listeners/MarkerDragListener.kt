package gsihome.reyst.mappoligon.map.listeners

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerDragListener(
        private val endDragAction: ((marker: Marker) -> Unit)? = null,
        private val startDragAction: ((marker: Marker) -> Unit)? = null,
        private val dragAction: ((marker: Marker) -> Unit)? = null
): GoogleMap.OnMarkerDragListener {
    override fun onMarkerDragEnd(marker: Marker?) {
        if (marker != null) endDragAction?.invoke(marker)
    }

    override fun onMarkerDragStart(marker: Marker?) {
        if (marker != null) startDragAction?.invoke(marker)
    }

    override fun onMarkerDrag(marker: Marker?) {
        if (marker != null) dragAction?.invoke(marker)
    }
}