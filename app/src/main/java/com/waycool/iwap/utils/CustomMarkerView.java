package com.waycool.iwap.utils;


import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.waycool.iwap.R;
import com.waycool.iwap.graphs.GraphsFragment;

import java.util.List;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    private String units;
    private List<String> dates;
    private String paramType;
    private GraphsFragment.GraphSelection duration;
    Context context;

    public CustomMarkerView(Context context, int layoutResource, String units, List<String> dates, String paramType, GraphsFragment.GraphSelection duration) {
        super(context, layoutResource);
        this.units = units;
        this.dates = dates;
        this.paramType = paramType;
        this.duration = duration;
        this.context = context;
        // find your layout components
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
// content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        try {
            if (paramType.equalsIgnoreCase("leaf_wetness") && duration == GraphsFragment.GraphSelection.LAST12HRS) {
                tvContent.setText((e.getY() >= 1f ? "Wet" : "Dry"  + "\n" + dates.get((int) e.getX())));
            } else {
                tvContent.setText(e.getY() + units + "\n" + dates.get((int) e.getX()));
            }
        } catch (Exception exception) {
            tvContent.setText(e.getY() + units);
        }
        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -(getHeight()));
        }

        return mOffset;
    }
}

