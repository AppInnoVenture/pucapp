package com.puc.pyp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.puc.pyp.Item
import com.puc.pyp.R
import com.puc.pyp.ui.components.ItemCard
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import com.puc.pyp.TextBookActivity

@Composable
fun TextScreen() {
    val context = LocalContext.current
    val items = listOf(
        Item(R.drawable.kan, "ಕನ್ನಡ", "kan"),
        Item(R.drawable.eng, "English", "eng"),
        Item(R.drawable.san, "संस्कृत", "san"),
        Item(R.drawable.hin, "हिन्दी", "hin"),
        Item(R.drawable.phy, context.getString(R.string.phy), "phydual"),
        Item(R.drawable.che, context.getString(R.string.che), "chedual"),
        Item(R.drawable.mat, context.getString(R.string.mat), "matdual"),
        Item(R.drawable.bio, context.getString(R.string.bio), "biodual"),
        Item(R.drawable.cs, context.getString(R.string.cse), "cs"),
        Item(R.drawable.ece, context.getString(R.string.ece), "ece"),
        Item(R.drawable.stat, context.getString(R.string.stat), "sta"),
        Item(R.drawable.bmat, context.getString(R.string.bmat), "bmat"),
        Item(R.drawable.eco, context.getString(R.string.eco), "ecodual"),
        Item(R.drawable.acc, context.getString(R.string.acc), "accdual"),
        Item(R.drawable.bus, context.getString(R.string.bus), "busdual"),
        Item(R.drawable.pol, context.getString(R.string.pol), "poldual"),
        Item(R.drawable.psy, context.getString(R.string.psy), "psydual"),
        Item(R.drawable.his, context.getString(R.string.his), "hisdual"),
        Item(R.drawable.soc, context.getString(R.string.soc), "socdual"),
        Item(R.drawable.geo, context.getString(R.string.geo), "geodual"),
        Item(R.drawable.edu, context.getString(R.string.edu), "edudual"),
        Item(R.drawable.log, context.getString(R.string.logic), "logdual"),
        Item(R.drawable.hsc, context.getString(R.string.hsc), "hsc"),
        Item(R.drawable.ggy, context.getString(R.string.ggy), "ggy"),
        Item(R.drawable.hmu, context.getString(R.string.hmu), "hmudual"),
        Item(R.drawable.kmu, context.getString(R.string.kmu), "kmu"),
        Item(R.drawable.kan2, "ಐಚ್ಛಿಕ ಕನ್ನಡ", "opt"),
        Item(R.drawable.mar, "मराठी", "mar"),
        Item(R.drawable.tel, "తెలుగు", "tel"),
        Item(R.drawable.tam, "தமிழ்", "tam"),
        Item(R.drawable.mal, "മലയാളം", "mal"),
        Item(R.drawable.urd, "اردو", "urd"),
        Item(R.drawable.ara, "العربية", "ara")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items.size) { index ->
            ItemCard(
                item = items[index],
                onClick = { item ->
                    val intent = Intent(context, TextBookActivity::class.java).apply {
                        putExtra("prefix", item.extra)
                        putExtra("desc", item.description.replace("\n", " "))
                        putExtra("img", item.img)
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}