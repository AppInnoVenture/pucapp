package com.puc.pyp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.puc.pyp.databinding.FragmentStudyMaterialBinding

class UpdatesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentStudyMaterialBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_study_material, container, false)

        val items = listOf( YearItem(getString(R.string.up_exam3),getString(R.string.up_253_pdf)),
YearItem(getString(R.string.up_exam2),getString(R.string.up_252_pdf)), 
YearItem(getString(R.string.up_exam1),getString(R.string.up_251_pdf)) )

        val itemAdapter = AdapterLan(items){ item ->
            val intent=Intent(context,PdfViewerActivity::class.java)
            intent.putExtra("prefix", item.extra)
            intent.putExtra("desc", item.descLan)
            intent.putExtra("diff", "up")
            startActivity(intent)
        }

        binding.recyclerViewStudy.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewStudy.adapter = itemAdapter

        return binding.root
    }
}