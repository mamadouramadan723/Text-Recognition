package com.rmd.media.ml.tf.textrecognition

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rmd.media.ml.tf.textrecognition.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var handler: Executor? = null
    private var client: TextClassificationClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        client = TextClassificationClient(applicationContext)
        handler = ContextCompat.getMainExecutor(this)

        binding.button.setOnClickListener { classify(binding.inputText.text.toString()) }
    }

    override fun onStart() {
        super.onStart()
        handler!!.execute { client!!.load() }
    }

    override fun onStop() {
        super.onStop()
        handler!!.execute { client!!.unload() }
    }


    /**
     * Send input text to TextClassificationClient and get the classify messages.
     */
    private fun classify(text: String) {

        handler!!.execute {

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
            binding.resultTextView.append(textToShow)

            // Clear the input text.
            binding.inputText.text.clear()

            // Scroll to the bottom to show latest entry's classification result.
            binding.scrollView.post { binding.scrollView.fullScroll(View.FOCUS_DOWN) }
        }
    }
}