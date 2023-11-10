package com.wassafqais.cognibrowse.activity

import android.os.Bundle
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.wassafqais.cognibrowse.R
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AiActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    lateinit var txtResponse: TextView
    lateinit var idTVQuestion: TextView
    lateinit var etQuestion: TextInputEditText

    companion object {
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
        idTVQuestion.text = question
        etQuestion.setText("")

        val apiKey = "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"
        val url = "https://api.openai.com/v1/engines/text-davinci-003/completions"

        val requestBody = """
        {
            "prompt": "$question",
            "max_tokens": 50,
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
                callback("Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.body?.string()
                    if (body != null) {
                        Log.v("data", body)
                        val jsonObject = JSONObject(body)

                        if (jsonObject.has("choices")) {
                            val jsonArray: JSONArray = jsonObject.getJSONArray("choices")

                            if (jsonArray.length() > 0) {
                                val textResult = jsonArray.getJSONObject(0).getString("text")
                                callback(textResult)
                            } else {
                                callback("No response from the server.")
                            }
                        } else {
                            callback("No 'choices' field in the response.")
                        }
                    } else {
                        Log.v("data", "empty")
                        callback("No response from the server.")
                    }
                } catch (e: Exception) {
                    Log.e("error", "Response parsing error", e)
                    callback("Error: ${e.message}")
                }
            }
        })
    }
}