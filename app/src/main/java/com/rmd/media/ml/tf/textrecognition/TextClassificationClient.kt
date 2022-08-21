package com.rmd.media.ml.tf.textrecognition

import android.content.Context
import android.util.Log
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import java.io.IOException
import java.util.*

/**
 * Load TfLite model and provide predictions with task api.
 */
class TextClassificationClient(private val context: Context) {
    @JvmField
    var classifier: NLClassifier? = null

    /**
     * Load TF Lite model.
     */
    fun load() {
        try {
            classifier = NLClassifier.createFromFile(context, MODEL_PATH)
        } catch (e: IOException) {
            Log.e(TAG, e.message.toString())
        }
    }

    /**
     * Free up resources as the client is no longer needed.
     */
    fun unload() {
        classifier!!.close()
        classifier = null
    }

    /**
     * Classify an input string and returns the classification results.
     */
    fun classify(text: String?): List<Result> {
        val apiResults = classifier!!.classify(text)
        val results: MutableList<Result> = ArrayList(apiResults.size)
        for (i in apiResults.indices) {
            val category = apiResults[i]
            results.add(Result("" + i, category.label, category.score))
        }
        results.sort()
        return results
    }

    companion object {
        private const val TAG = "TaskApi"
        private const val MODEL_PATH = "text_classification.tflite"
    }
}