package com.puc.pyp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.puc.pyp.R

// Define the custom font family
val AbcdFontFamily = FontFamily(
    Font(R.font.abcd, FontWeight.Normal)
)

// Define typography with custom font and styles matching res/values/style.xml
val Typography = Typography(
    // For general body text (similar to bodyLarge in default Typography)
    bodyLarge = TextStyle(
        fontFamily = AbcdFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // For tab text (tabTextAppearance)
    labelMedium = TextStyle(
        fontFamily = AbcdFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp, // Matches 14dp from tabTextAppearance
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center // Matches textAlignment=center
    ),
    // For main toolbar title (ToolbarTitleMain)
    titleLarge = TextStyle(
        fontFamily = AbcdFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp, // Matches 22dp from ToolbarTitleMain
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center // Matches textAlignment=center
    ),
    // For PDF toolbar title (ToolbarTitlePdf)
    titleMedium = TextStyle(
        fontFamily = AbcdFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp, // Matches 18dp from ToolbarTitlePdf
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center, // Matches textAlignment=center
        textDecoration = androidx.compose.ui.text.style.TextDecoration.None // Matches ellipsize=end (handled in composable)
    ),
    // For overflow menu (OverflowMenuTextAppearance)
    labelSmall = TextStyle(
        fontFamily = AbcdFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp, // Matches 16sp from OverflowMenuTextAppearance
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    )
)