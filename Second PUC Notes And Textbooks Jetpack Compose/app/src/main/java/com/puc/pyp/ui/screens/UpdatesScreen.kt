package com.puc.pyp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.puc.pyp.R
import com.puc.pyp.YearItem
import com.puc.pyp.ui.components.YearItemCard
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import com.puc.pyp.PdfViewerActivity

@Composable
fun UpdatesScreen() {
    val context = LocalContext.current
    val items = listOf(
        YearItem(context.getString(R.string.up_exam3), context.getString(R.string.up_253_pdf)),
        YearItem(context.getString(R.string.up_exam2), context.getString(R.string.up_252_pdf)),
        YearItem(context.getString(R.string.up_exam1), context.getString(R.string.up_251_pdf))
    )

    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items.size) { index ->
            YearItemCard(
                item = items[index],
                onClick = { item ->
                    val intent = Intent(context, PdfViewerActivity::class.java).apply {
                        putExtra("prefix", item.extra)
                        putExtra("desc", item.descLan)
                        putExtra("diff", "up")
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}