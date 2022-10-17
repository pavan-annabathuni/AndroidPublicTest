package com.waycool.uicomponents.Text;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import com.waycool.uicomponents.R;

public class Title1SemiBold extends AppCompatTextView {
    public Title1SemiBold(Context context) {
        super(context);
        Typeface face = ResourcesCompat.getFont(context, R.font.notosans_semibold);
        this.setTypeface(face);
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
      //  this.setTextColor(context.getResources().getColor(R.color.black));

    }

    public Title1SemiBold(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Typeface face = ResourcesCompat.getFont(context, R.font.notosans_semibold);
        this.setTypeface(face);
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
       // this.setTextColor(context.getResources().getColor(R.color.black));

    }

    public Title1SemiBold(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface face = ResourcesCompat.getFont(context, R.font.notosans_semibold);
        this.setTypeface(face);
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
       // this.setTextColor(context.getResources().getColor(R.color.black));

    }

}
