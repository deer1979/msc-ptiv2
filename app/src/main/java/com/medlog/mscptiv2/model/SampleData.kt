package com.medlog.mscptiv2.model

/**
 * SampleData provides sample inspection rows for testing PDF generation.
 * In a production app, this would load from a database or API.
 */
object SampleData {
    fun sampleRows(): List<RowItem> {
        return listOf(
            RowItem(
                step = "1",
                location = "Box External",
                check = "Impact damage",
                description = "Underside, RHS, LHS, Roof panel",
                checked = false
            ),
            RowItem(
                step = "1",
                location = "Box External",
                check = "Impact damage",
                description = "Doors and machinery frame",
                checked = false
            ),
            RowItem(
                step = "2",
                location = "Box Internal",
                check = "Inner panels + gaskets",
                description = "Ceiling, LHS, RHS, Door panels & gaskets",
                checked = false
            ),
            RowItem(
                step = "3",
                location = "Machinery",
                check = "Power cable",
                description = "not less than 18mt from the cable box; max. 2 splices accepted",
                checked = false
            )
        )
    }
}
