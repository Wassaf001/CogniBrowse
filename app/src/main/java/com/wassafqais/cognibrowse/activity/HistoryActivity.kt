package com.wassafqais.cognibrowse.activity

import android.os.Bundle
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.wassafqais.cognibrowse.R
import com.wassafqais.cognibrowse.adapter.HistoryAdapter
import com.wassafqais.cognibrowse.databinding.ActivityHistoryBinding
import com.wassafqais.cognibrowse.fragment.loadHistoryData
import com.wassafqais.cognibrowse.model.HistoryItem

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "History"
        supportActionBar?.title?.let {
            val style = SpannableString(it)
            style.setSpan(TextAppearanceSpan(this, R.style.ToolbarTitleStyle), 0, it.length, 0)
            supportActionBar?.title = style
        }
//        val hi = HistoryItem(result, currentTime)
//        val historyItems = loadHistoryData(hi)
//        val recyclerView = binding.rvHistory
//        recyclerView.adapter = HistoryAdapter(historyItems)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return true
    }
}
