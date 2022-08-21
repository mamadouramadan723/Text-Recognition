package com.rmd.media.ml.tf.textrecognition

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.EditText
import android.widget.ScrollView
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button


class MainActivity : AppCompatActivity() {
    private var client: TextClassificationClient? = null
    private var resultTextView: TextView? = null
    private var inputEditText: EditText? = null
    private var handler: Handler? = null
    private var scrollView: ScrollView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.v(TAG, "onCreate")
        client = TextClassificationClient(applicationContext)
        handler = Handler()
        val classifyButton = findViewById<Button>(R.id.button)
        classifyButton.setOnClickListener { v: View? -> classify(inputEditText!!.text.toString()) }
        resultTextView = findViewById(R.id.result_text_view)
        inputEditText = findViewById(R.id.input_text)
        scrollView = findViewById(R.id.scroll_view)
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart")
        handler!!.post { client!!.load() }
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "onStop")
        handler!!.post { client!!.unload() }
    }

    /**
     * Send input text to TextClassificationClient and get the classify messages.
     */
    private fun classify(text: String) {
        handler!!.post {

            // Run text classification with TF Lite.
            val results = client!!.classify(text)

            // Show classification result on screen
            showResult(text, results)
        }
    }

    /**
     * Show classification result on the screen.
     */
    private fun showResult(inputText: String, results: List<Result>) {
        // Run on UI thread as we'll updating our app UI
        runOnUiThread {
            var textToShow = "Input: $inputText\nOutput:\n"
            for (i in results.indices) {
                val result = results[i]
                textToShow += String.format("    %s: %s\n", result.title, result.confidence)
            }
            textToShow += "---------\n"

            // Append the result to the UI.
            resultTextView!!.append(textToShow)

            // Clear the input text.
            inputEditText!!.text.clear()

            // Scroll to the bottom to show latest entry's classification result.
            scrollView!!.post { scrollView!!.fullScroll(View.FOCUS_DOWN) }
        }
    }

    companion object {
        private const val TAG = "TextClassificationDemo"
    }
}