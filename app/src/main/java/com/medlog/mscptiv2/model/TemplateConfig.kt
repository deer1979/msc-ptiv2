package com.medlog.mscptiv2.model

import android.content.Context
import android.graphics.Typeface
import com.medlog.mscptiv2.util.MeasurementUtils

data class TemplateConfig(
    val pageWidthPt: Float,
    val pageHeightPt: Float,
    val marginLeftPt: Float,
    val marginRightPt: Float,
    val marginTopPt: Float,
    val marginBottomPt: Float,
    val titlePt: Float,
    val cellPt: Float,
    val descPt: Float,
    val titleTypeface: Typeface?,
    val cellTypeface: Typeface?,
    val headerTopPt: Float,
    val titleSpacingPt: Float,
    val tableHeaderHeightPt: Float,
    val tableHeaderTextBaselineOffsetPt: Float,
    val rowBaselineOffsetPt: Float,
    val cellPaddingTopPt: Float,
    val cellPaddingBottomPt: Float,
    val cellPaddingExtraForCheckboxPt: Float,
    val checkboxSizePt: Float,
    val col1WidthPt: Float,
    val col2WidthPt: Float,
    val col3WidthPt: Float,
    val col4WidthPt: Float,
    val col5WidthPt: Float,
    val footerHeightPt: Float,
    val lineStrokePt: Float
) {
    fun outputFileName(): String = "MSC_PTI_${System.currentTimeMillis()}.pdf"

    companion object {
        // Build default config for A4 using mm->pt conversion
        fun defaultConfig(context: Context): TemplateConfig {
            val mm = MeasurementUtils
            val pageW = mm.mmToPt(210f)
            val pageH = mm.mmToPt(297f)
            val left = mm.mmToPt(12f)
            val right = mm.mmToPt(12f)
            val top = mm.mmToPt(14f)
            val bottom = mm.mmToPt(14f)
            val titlePt = 16f
            val cellPt = 10f
            val descPt = 9f
            return TemplateConfig(
                pageWidthPt = pageW,
                pageHeightPt = pageH,
                marginLeftPt = left,
                marginRightPt = right,
                marginTopPt = top,
                marginBottomPt = bottom,
                titlePt = titlePt,
                cellPt = cellPt,
                descPt = descPt,
                titleTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD),
                cellTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL),
                headerTopPt = top + 12f,
                titleSpacingPt = 20f,
                tableHeaderHeightPt = 18f,
                tableHeaderTextBaselineOffsetPt = 12f,
                rowBaselineOffsetPt = 12f,
                cellPaddingTopPt = 6f,
                cellPaddingBottomPt = 6f,
                cellPaddingExtraForCheckboxPt = 4f,
                checkboxSizePt = 10f,
                col1WidthPt = mm.mmToPt(12f),
                col2WidthPt = mm.mmToPt(34f),
                col3WidthPt = mm.mmToPt(40f),
                col4WidthPt = mm.mmToPt(86f),
                col5WidthPt = mm.mmToPt(10f),
                footerHeightPt = mm.mmToPt(12f),
                lineStrokePt = 0.8f
            )
        }
    }
}
