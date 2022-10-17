package com.waycool.uicomponents.Text

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.waycool.uicomponents.R

class SmallTextSemiBold : AppCompatTextView {
    constructor(context: Context) : super(context) {
        val typeface = ResourcesCompat.getFont(context, R.font.notosans_semibold)
        this.typeface = typeface
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        // this.setTextColor(context.resources.getColor(R.color.black))
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typeface = ResourcesCompat.getFont(context, R.font.notosans_semibold)
        this.typeface = typeface
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        // this.setTextColor(context.resources.getColor(R.color.black))

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typeface = ResourcesCompat.getFont(context, R.font.notosans_semibold)
        this.typeface = typeface
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        //  this.setTextColor(context.resources.getColor(R.color.black))
    }

}