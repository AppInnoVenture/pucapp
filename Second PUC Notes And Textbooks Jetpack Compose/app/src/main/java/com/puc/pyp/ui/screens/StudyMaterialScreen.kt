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
import com.puc.pyp.NotesActivity

@Composable
fun StudyMaterialScreen() {
    val context = LocalContext.current
    val items = listOf(
        Item(R.drawable.kan, "ಕನ್ನಡ", "kan"),
        Item(R.drawable.eng, "English", "eng"),
        Item(R.drawable.san, "संस्कृत", "san"),
        Item(R.drawable.hin, "हिन्दी", "hin"),
        Item(R.drawable.phy, context.getString(R.string.phy), "phy"),
        Item(R.drawable.che, context.getString(R.string.che), "che"),
        Item(R.drawable.mat, context.getString(R.string.mat), "mat"),
        Item(R.drawable.bio, context.getString(R.string.bio), "bio"),
        Item(R.drawable.cs, context.getString(R.string.cse), "cs"),
        Item(R.drawable.ece, context.getString(R.string.ece), "ece"),
        Item(R.drawable.stat, context.getString(R.string.stat), "sta"),
        Item(R.drawable.eco, context.getString(R.string.eco), "ecodu"),
        Item(R.drawable.acc, context.getString(R.string.acc), "acc"),
        Item(R.drawable.his, context.getString(R.string.his), "hisdu"),
        Item(R.drawable.bus, context.getString(R.string.bus), "busdu"),
        Item(R.drawable.pol, context.getString(R.string.pol), "poldu"),
        Item(R.drawable.soc, context.getString(R.string.soc), "socdu"),
        Item(R.drawable.geo, context.getString(R.string.geo), "geodu"),
        Item(R.drawable.psy, context.getString(R.string.psy), "psy"),
        Item(R.drawable.hsc, context.getString(R.string.hsc), "hsc"),
        Item(R.drawable.edu, context.getString(R.string.edu), "edt"),
        Item(R.drawable.kan2, "ಐಚ್ಛಿಕ ಕನ್ನಡ", "opt"),
        Item(R.drawable.aut, context.getString(R.string.aut), "aut"),
        Item(R.drawable.ret, context.getString(R.string.ret), "ret"),
        Item(R.drawable.ite, context.getString(R.string.ite), "ite"),
        Item(R.drawable.hea, context.getString(R.string.hea), "hea"),
        Item(R.drawable.bws, context.getString(R.string.bws), "bws")
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
                    val intent = Intent(context, NotesActivity::class.java).apply {
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