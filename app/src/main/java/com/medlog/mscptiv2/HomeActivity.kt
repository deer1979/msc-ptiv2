package com.medlog.mscptiv2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.medlog.mscptiv2.model.SampleData
import com.medlog.mscptiv2.model.TemplateConfig
import com.medlog.mscptiv2.pdf.PdfGenerator
import kotlinx.coroutines.*

class HomeActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No UI XML for speed: just trigger a sample PDF generation then finish
        scope.launch {
            val cfg = TemplateConfig.defaultConfig(this@HomeActivity)
            val sample = SampleData.sampleRows()
            val out = PdfGenerator.generatePdf(this@HomeActivity, cfg, sample)
            // out = File path or null on error
            finish()
        }
    }
}
