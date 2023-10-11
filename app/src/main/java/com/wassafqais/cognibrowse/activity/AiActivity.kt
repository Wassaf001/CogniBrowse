package com.wassafqais.cognibrowse.activity

import android.os.Bundle
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.wassafqais.cognibrowse.R
import com.wassafqais.cognibrowse.model.Bookmark
import com.wassafqais.cognibrowse.model.Tab
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.view.Gravity
import android.view.WindowManager
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.wassafqais.cognibrowse.activity.MainActivity.Companion.myPager
import com.wassafqais.cognibrowse.activity.MainActivity.Companion.tabsBtn
import com.wassafqais.cognibrowse.adapter.TabAdapter
import com.wassafqais.cognibrowse.databinding.ActivityMainBinding
import com.wassafqais.cognibrowse.databinding.BookmarkDialogBinding
import com.wassafqais.cognibrowse.databinding.MoreFeaturesBinding
import com.wassafqais.cognibrowse.databinding.TabsViewBinding
import com.wassafqais.cognibrowse.fragment.BrowseFragment
import com.wassafqais.cognibrowse.fragment.HomeFragment
import java.io.ByteArrayOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class AiActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    // creating variables on below line.
    lateinit var txtResponse: TextView
    lateinit var idTVQuestion: TextView
    lateinit var etQuestion: TextInputEditText

    companion object{
        private var isFullscreen: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai)
        etQuestion = findViewById(R.id.etQuestion)
        idTVQuestion = findViewById(R.id.idTVQuestion)
        txtResponse = findViewById(R.id.txtResponse)

        etQuestion.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                // setting response tv on below line.
                txtResponse.text = "Please wait.."

                // validating text
                val question = etQuestion.text.toString().trim()
                Toast.makeText(this, question, Toast.LENGTH_SHORT).show()
                if (question.isNotEmpty()) {
                    getResponse(question) { response ->
                        runOnUiThread {
                            txtResponse.text = response
                        }
                    }
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    fun getResponse(question: String, callback: (String) -> Unit) {
        // setting text for question on below line.
        idTVQuestion.text = question
        etQuestion.setText("")

        val apiKey = "YOUR_API_KEY_HERE"
        val url = "https://api.openai.com/v1/engines/davinci-codex/completions"

        val requestBody = """
            {
                "prompt": "$question",
                "max_tokens": 100,
                "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    Log.v("data", body)
                } else {
                    Log.v("data", "empty")
                }
                val jsonObject = JSONObject(body)
                val jsonArray: JSONArray = jsonObject.getJSONArray("choices")
                val textResult = jsonArray.getJSONObject(0).getString("text")
                callback(textResult)
            }
        })
    }
}
