package mediaplayer.patryk.lms.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.BlurMaskFilter
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class BlurCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var circleColor: Int = ContextCompat.getColor(context, android.R.color.holo_blue_light)
    private var blurRadius: Float = 100f

    init {
        // Enable software layer to allow blur
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        paint.color = circleColor
        paint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
    }

    fun setCircleColor(color: Int) {
        circleColor = color
        paint.color = circleColor
        invalidate()
    }

    fun setBlurRadius(radius: Float) {
        blurRadius = radius
        paint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas ?: return
        val radius = width.coerceAtMost(height) / 2f
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)
    }
}
