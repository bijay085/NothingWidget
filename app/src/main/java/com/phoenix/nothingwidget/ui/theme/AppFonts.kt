package com.phoenix.nothingwidget.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.phoenix.nothingwidget.R

/**
 * App UI fonts (SIL Open Font License, bundled locally — see /licenses).
 *
 * Space Grotesk is a variable font, so each weight declares its own axis value.
 * Widget fonts are separate and live with each widget.
 */
@OptIn(ExperimentalTextApi::class)
private fun groteskWeight(weight: FontWeight) = Font(
    resId = R.font.space_grotesk,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

/** Technical grotesque used for headings, body, and buttons. */
val SpaceGrotesk = FontFamily(
    groteskWeight(FontWeight.Normal),
    groteskWeight(FontWeight.Medium),
    groteskWeight(FontWeight.SemiBold),
    groteskWeight(FontWeight.Bold),
)

/** Monospace used for metadata tags and filter chips. */
val SpaceMono = FontFamily(
    Font(R.font.space_mono, FontWeight.Normal),
    Font(R.font.space_mono_bold, FontWeight.Bold),
)
