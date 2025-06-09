package com.puc.pyp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.puc.pyp.databinding.FragmentStudyMaterialBinding

class TextFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentStudyMaterialBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_study_material, container, false)
        val items = listOf(
            Item(R.drawable.kan, "ಕನ್ನಡ", "kan"),
            Item(R.drawable.eng, "English", "eng"),
            Item(R.drawable.san, "संस्कृत", "san"),
            Item(R.drawable.hin, "हिन्दी", "hin"),
            Item(R.drawable.phy, getString(R.string.phy), "phydual"),
            Item(R.drawable.che, getString(R.string.che), "chedual"),
            Item(R.drawable.mat, getString(R.string.mat), "matdual"),
            Item(R.drawable.bio, getString(R.string.bio), "biodual"),
            Item(R.drawable.cs, getString(R.string.cse), "cs"),
            Item(R.drawable.ece, getString(R.string.ece), "ece"),
            Item(R.drawable.stat, getString(R.string.stat), "sta"),
            Item(R.drawable.bmat, getString(R.string.bmat), "bmat"),
            Item(R.drawable.eco, getString(R.string.eco), "ecodual"),
            Item(R.drawable.acc, getString(R.string.acc), "accdual"),
            Item(R.drawable.bus, getString(R.string.bus), "busdual"),
            Item(R.drawable.pol, getString(R.string.pol), "poldual"),
            Item(R.drawable.psy, getString(R.string.psy), "psydual"),
            Item(R.drawable.his, getString(R.string.his), "hisdual"),
            Item(R.drawable.soc, getString(R.string.soc), "socdual"),
            Item(R.drawable.geo, getString(R.string.geo), "geodual"),
            Item(R.drawable.edu, getString(R.string.edu), "edudual"),
            Item(R.drawable.log, getString(R.string.logic), "logdual"),
            Item(R.drawable.hsc, getString(R.string.hsc), "hsc"),
            Item(R.drawable.ggy, getString(R.string.ggy), "ggy"),
            Item(R.drawable.hmu, getString(R.string.hmu), "hmudual"),
            Item(R.drawable.kmu, getString(R.string.kmu), "kmu"),
            Item(R.drawable.kan2, "ಐಚ್ಛಿಕ ಕನ್ನಡ", "opt"),
            Item(R.drawable.mar, "मराठी", "mar"),
            Item(R.drawable.tel, "తెలుగు", "tel"),
            Item(R.drawable.tam, "தமிழ்", "tam"),
            Item(R.drawable.mal, "മലയാളം", "mal"),
            Item(R.drawable.urd, "اردو", "urd"),
            Item(R.drawable.ara, "العربية", "ara")
        )

        val itemAdapter = ItemAdapter(items) { item ->
            val intent = Intent(context, TextBookActivity::class.java)
            intent.putExtra("prefix", item.extra)
            intent.putExtra("desc", item.description.replace("\n"," "))
            intent.putExtra("img",item.img)
            startActivity(intent)
        }

        binding.recyclerViewStudy.layoutManager = GridLayoutManager(context, 2).apply {
            isItemPrefetchEnabled=true
        }
        binding.recyclerViewStudy.adapter = itemAdapter

        return binding.root
    }
}