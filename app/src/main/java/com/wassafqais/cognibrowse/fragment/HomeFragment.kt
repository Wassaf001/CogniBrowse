package com.wassafqais.cognibrowse.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.Date
import android.widget.Button
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.wassafqais.cognibrowse.R
import com.wassafqais.cognibrowse.activity.AiActivity
import com.wassafqais.cognibrowse.activity.BookmarkActivity
import com.wassafqais.cognibrowse.activity.HistoryActivity
import com.wassafqais.cognibrowse.activity.MainActivity
import com.wassafqais.cognibrowse.activity.changeTab
import com.wassafqais.cognibrowse.activity.checkForInternet
import com.wassafqais.cognibrowse.adapter.BookmarkAdapter
import com.wassafqais.cognibrowse.adapter.HistoryAdapter
import com.wassafqais.cognibrowse.databinding.FragmentHomeBinding
import com.wassafqais.cognibrowse.model.HistoryItem


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.bind(view)

        return view
    }

    override fun onResume() {
        super.onResume()

        val mainActivityRef = requireActivity() as MainActivity

        MainActivity.tabsBtn.text = MainActivity.tabsList.size.toString()

        MainActivity.tabsList[MainActivity.myPager.currentItem].name = "Home"

        mainActivityRef.binding.topSearchBar.setText("")
        binding.searchView.setQuery("", false)
        mainActivityRef.binding.webIcon.setImageResource(R.drawable.ic_search)

        mainActivityRef.binding.refreshBtn.visibility = View.GONE

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(result: String?): Boolean {
                val currentTime = Date()
                val urL = result
                val h = HistoryItem(urL, currentTime)
                print("url is $urL")
                print("date is $currentTime")

                if (checkForInternet(requireContext()))
                    changeTab(result!!, BrowseFragment(result))
                else
                    Snackbar.make(binding.root, "Internet Not Connected", 3000).show()
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean = false
        })
        mainActivityRef.binding.goBtn.setOnClickListener {
            if (checkForInternet(requireContext()))
                changeTab(
                    mainActivityRef.binding.topSearchBar.text.toString(),
                    BrowseFragment(mainActivityRef.binding.topSearchBar.text.toString())
                )
            else
                Snackbar.make(binding.root, "Internet Not Connected\uD83D\uDE03", 3000).show()
        }

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setItemViewCacheSize(5)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.recyclerView.adapter = BookmarkAdapter(requireContext())

        if (MainActivity.bookmarkList.size < 1)
            binding.viewAllBtn.visibility = View.GONE
        binding.viewAllBtn.setOnClickListener {
            startActivity(Intent(requireContext(), BookmarkActivity::class.java))
        }

        val aiButton = view?.findViewById<Button>(R.id.ai_button)
        aiButton?.setOnClickListener {
            val intent = Intent(requireContext(), AiActivity::class.java)
            startActivity(intent)
        }

        val historyButton = view?.findViewById<Button>(R.id.history_button)
        historyButton?.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            startActivity(intent)
        }
    }
}

public fun loadHistoryData(historyItem: HistoryItem): List<HistoryItem> {
    val historyItems = mutableListOf<HistoryItem>()


    historyItems.add(HistoryItem(historyItem.url, historyItem.date))

    return historyItems
}