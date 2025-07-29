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

class StudyMaterialFragment : Fragment() {

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
            Item(R.drawable.phy, getString(R.string.phy), "phy"),
            Item(R.drawable.che, getString(R.string.che), "che"),
            Item(R.drawable.mat, getString(R.string.mat), "mat"),
            Item(R.drawable.bio, getString(R.string.bio), "bio"),
            Item(R.drawable.cs, getString(R.string.cse), "cs"),
            Item(R.drawable.ece, getString(R.string.ece), "ece"),
            Item(R.drawable.stat, getString(R.string.stat), "sta"),
            Item(R.drawable.eco, getString(R.string.eco), "ecodu"),
            Item(R.drawable.acc, getString(R.string.acc), "acc"),
            Item(R.drawable.his, getString(R.string.his), "hisdu"),
            Item(R.drawable.bus, getString(R.string.bus), "busdu"),
            Item(R.drawable.pol, getString(R.string.pol), "poldu"),
            Item(R.drawable.soc, getString(R.string.soc), "socdu"),
            Item(R.drawable.geo, getString(R.string.geo), "geodu"),
            Item(R.drawable.psy, getString(R.string.psy), "psy"),
            Item(R.drawable.hsc, getString(R.string.hsc), "hsc"),
            Item(R.drawable.edu, getString(R.string.edu), "edt"),
            Item(R.drawable.kan2, "ಐಚ್ಛಿಕ ಕನ್ನಡ", "opt"),
            Item(R.drawable.aut, getString(R.string.aut), "aut"),
            Item(R.drawable.ret, getString(R.string.ret), "ret"),
            Item(R.drawable.ite, getString(R.string.ite), "ite"),
            Item(R.drawable.hea, getString(R.string.hea), "hea"),
            Item(R.drawable.bws, getString(R.string.bws), "bws")
        )

        val itemAdapter = ItemAdapter(items) { item ->
            val intent = Intent(context, NotesActivity::class.java)
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