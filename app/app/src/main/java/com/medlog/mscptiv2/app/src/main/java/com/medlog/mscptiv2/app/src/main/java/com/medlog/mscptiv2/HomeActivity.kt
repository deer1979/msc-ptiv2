package com.medlog.mscptiv2.pdf

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.ParcelFileDescriptor
import com.medlog.mscptiv2.model.RowItem
import com.medlog.mscptiv2.model.TemplateConfig
import com.medlog.mscptiv2.util.MeasurementUtils
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

/**
 * PdfGenerator: minimal native PDF generator using android.graphics.pdf.PdfDocument.
 * Public API:
 *   suspend fun generatePdf(context, templateConfig, rows): File?
 *
 * Implementation notes and comments inside functions explain what each step controls.
 */
object PdfGenerator {

    // Public suspend function to call from a Coroutine scope (Dispatchers.IO recommended)
    suspend fun generatePdf(context: Context, cfg: TemplateConfig, rows: List<RowItem>): File? =
        withContext(Dispatchers.IO) {
            try {
                // Output file in cacheDir
                val outFile = File(context.cacheDir, cfg.outputFileName())
                if (outFile.exists()) outFile.delete()

                val pdf = PdfDocument()
                val pageInfoBuilder = PdfDocument.PageInfo.Builder(cfg.pageWidthPt.toInt(), cfg.pageHeightPt.toInt(), 1)

                // constants for layout in pts
                val left = cfg.marginLeftPt
                val right = cfg.pageWidthPt - cfg.marginRightPt
                val usableWidth = right - left

                // paints
                val titlePaint = Paint().apply { isAntiAlias = true; textSize = cfg.titlePt; typeface = cfg.titleTypeface }
                val cellPaint = Paint().apply { isAntiAlias = true; textSize = cfg.cellPt; typeface = cfg.cellTypeface }
                val descPaint = Paint().apply { isAntiAlias = true; textSize = cfg.descPt; typeface = cfg.cellTypeface }
                val linePaint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = cfg.lineStrokePt; color = Color.DKGRAY }

                // Start first page
                var pageIndex = 1
                var page = pdf.startPage(pageInfoBuilder.setPageNumber(pageIndex).create())
                var canvas = page.canvas

                // Y cursor in pts; start after header area
                var cursorY = cfg.headerTopPt

                // Draw header (simple)
                canvas.drawText(cfg.titleText, left, cursorY.toFloat(), titlePaint)
                cursorY += cfg.titleSpacingPt

                // Table header height
                val tableHeaderHeight = cfg.tableHeaderHeightPt
                drawTableHeader(canvas, left.toFloat(), cursorY.toFloat(), usableWidth.toFloat(), cfg, cellPaint, linePaint)
                cursorY += tableHeaderHeight

                // For each row: measure and draw; if not fit, finish page and new page
                for (row in rows) {
                    val rowHeight = measureRowHeight(row, descPaint, cfg)
                    val pageBottomLimit = cfg.pageHeightPt - cfg.marginBottomPt - cfg.footerHeightPt

                    if (cursorY + rowHeight > pageBottomLimit) {
                        // finish page and start new one
                        pdf.finishPage(page)
                        pageIndex++
                        page = pdf.startPage(pageInfoBuilder.setPageNumber(pageIndex).create())
                        canvas = page.canvas
                        cursorY = cfg.marginTopPt
                        // draw table header again
                        drawTableHeader(canvas, left.toFloat(), cursorY.toFloat(), usableWidth.toFloat(), cfg, cellPaint, linePaint)
                        cursorY += tableHeaderHeight
                    }

                    drawRow(canvas, left.toFloat(), cursorY.toFloat(), usableWidth.toFloat(), row, cfg, cellPaint, descPaint, linePaint)
                    cursorY += rowHeight
                }

                // footer placeholder
                // pdf page count finalize
                pdf.finishPage(page)

                // write to file
                FileOutputStream(outFile).use { fos ->
                    pdf.writeTo(fos)
                }
                pdf.close()
                // basic integrity check
                if (outFile.exists() && outFile.length() > 200) return@withContext outFile
                return@withContext null
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }

    // Measure height of a row in pts. Uses descPaint to measure wrapped description text.
    private fun measureRowHeight(row: RowItem, descPaint: Paint, cfg: TemplateConfig): Float {
        val padTop = cfg.cellPaddingTopPt
        val padBottom = cfg.cellPaddingBottomPt
        val stepH = textHeightForWidth(row.step, descPaint, cfg.col1WidthPt)
        val locH = textHeightForWidth(row.location, descPaint, cfg.col2WidthPt)
        val checkH = textHeightForWidth(row.check, descPaint, cfg.col3WidthPt)
        val descH = textHeightForWidth(row.description, descPaint, cfg.col4WidthPt)
        val contentH = max(max(stepH, locH), max(checkH, descH))
        val checkboxExtra = cfg.checkboxSizePt + cfg.cellPaddingExtraForCheckboxPt
        return padTop + contentH + padBottom + checkboxExtra
    }

    // Simple wrapper to estimate text height by splitting into lines according to width
    private fun textHeightForWidth(text: String, paint: Paint, widthPt: Float): Float {
        // convert pt to px for StaticLayout if necessary; here we approximate using measureText
        if (text.isEmpty()) {
            val fm = paint.fontMetrics
            return (-(fm.ascent) + fm.descent) * 1.05f
        }
        // naive approximation: measure average char width and compute lines
        val avgCharWidth = paint.measureText("a")
        val approxCharsPerLine = max(1, (widthPt / avgCharWidth).toInt())
        val lines = (text.length / approxCharsPerLine) + 1
        val fm = paint.fontMetrics
        val lineH = (-(fm.ascent) + fm.descent) * 1.05f
        return lines * lineH
    }

    // Draws a simplified table header
    private fun drawTableHeader(canvas: Canvas, left: Float, top: Float, usableWidth: Float, cfg: TemplateConfig, cellPaint: Paint, linePaint: Paint) {
        val x = left
        val y = top
        // Draw header text labels across columns (positions using cfg.col*WidthPt)
        var cx = x
        val labels = listOf("Step", "Reefer Location", "Check", "Description", "")
        val colWidths = listOf(cfg.col1WidthPt, cfg.col2WidthPt, cfg.col3WidthPt, cfg.col4WidthPt, cfg.col5WidthPt)
        for (i in labels.indices) {
            val label = labels[i]
            if (label.isNotEmpty()) canvas.drawText(label, cx + 2f, y + cfg.tableHeaderTextBaselineOffsetPt, cellPaint)
            cx += colWidths[i]
        }
        // draw bottom separator
        canvas.drawLine(x, y + cfg.tableHeaderHeightPt - 2f, x + usableWidth, y + cfg.tableHeaderHeightPt - 2f, linePaint)
    }

    // Draw row: simplified drawing of text in columns and checkbox as vector
    private fun drawRow(canvas: Canvas, left: Float, top: Float, usableWidth: Float, row: RowItem, cfg: TemplateConfig, cellPaint: Paint, descPaint: Paint, linePaint: Paint) {
        val colWidths = listOf(cfg.col1WidthPt, cfg.col2WidthPt, cfg.col3WidthPt, cfg.col4WidthPt, cfg.col5WidthPt)
        var cx = left
        // Draw step
        canvas.drawText(row.step, cx + 2f, top + cfg.rowBaselineOffsetPt, cellPaint)
        cx += colWidths[0]
        // Draw location
        canvas.drawText(row.location, cx + 2f, top + cfg.rowBaselineOffsetPt, cellPaint)
        cx += colWidths[1]
        // Draw check label
        canvas.drawText(row.check, cx + 2f, top + cfg.rowBaselineOffsetPt, cellPaint)
        cx += colWidths[2]
        // Draw description (single-line approximation)
        canvas.drawText(row.description, cx + 2f, top + cfg.rowBaselineOffsetPt, descPaint)
        // Draw checkbox in last col centered vertically
        val checkboxX = left + colWidths[0] + colWidths[1] + colWidths[2] + colWidths[3] + (cfg.col5WidthPt / 2f)
        val checkboxY = top + cfg.cellPaddingTopPt + (measureRowHeight(row, descPaint, cfg) - cfg.cellPaddingTopPt - cfg.cellPaddingBottomPt) / 2f
        drawCheckbox(canvas, checkboxX, checkboxY, row.checked, cfg)
    }

    private fun drawCheckbox(canvas: Canvas, cx: Float, cy: Float, checked: Boolean, cfg: TemplateConfig) {
        val size = cfg.checkboxSizePt
        val left = cx - size / 2f
        val top = cy - size / 2f
        val rect = RectF(left, top, left + size, top + size)
        val boxPaint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 1f; color = Color.DKGRAY }
        val fillPaint = Paint().apply { style = Paint.Style.FILL; color = Color.BLACK }
        canvas.drawRect(rect, boxPaint)
        if (checked) {
            // simple check mark as two lines
            val p = Paint().apply { strokeWidth = 2f; color = Color.BLACK; style = Paint.Style.STROKE; isAntiAlias = true }
            val startX = left + size * 0.2f
            val startY = top + size * 0.55f
            val midX = left + size * 0.45f
            val midY = top + size * 0.8f
            val endX = left + size * 0.85f
            val endY = top + size * 0.2f
            canvas.drawLine(startX, startY, midX, midY, p)
            canvas.drawLine(midX, midY, endX, endY, p)
        }
    }
}
