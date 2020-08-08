package com.bajaj.currencyconverter

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity__display_info.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Activity_DisplayInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__display_info)
    }
    var num = 0
    fun convert(view: View) {

        // get the no. of dollars entered
        var n  = nOfDollars.text.toString()
        num = n.toInt()

        // data validation
        if(n.isNotEmpty())  {
            if(isNetworkAvaiable()) {
                Toast.makeText(this, "Converting $n USD to INR", Toast.LENGTH_SHORT).show()
                convertTask().execute(n.toString())
            }
            else
                Toast.makeText(this, "Network Not Available, Please Try Again", Toast.LENGTH_SHORT).show()

        }
        else
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()

    }

    fun isNetworkAvaiable():Boolean {
        //connectivity manager

        val cManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network =cManager.activeNetwork

        if(network != null)  return true

        return false

    }

    inner class convertTask: AsyncTask<String, Void, String>()    {
        override fun doInBackground(vararg args: String?): String {

            var result = ""
            val n = args[0]

            val urlS = "https://api.exchangeratesapi.io/latest?base=USD&symbols=INR"

            var url = URL(urlS)
            val connection = url.openConnection() as HttpURLConnection

            connection.connectTimeout = 150000
            connection.readTimeout = 150000

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var line = reader.readLine()

            while (line != null)    {
                result += line
                line = reader.readLine()
            }

            Log.d("convertTask", "Result: $result")
            return result
        }

        //execute on UI thread

        override fun onPostExecute(result: String?) {
            // parsing logic - parse code
            // update on Textview
            inrT.setText("")
            var multiplier = 0.0
            if (result!!.isNotEmpty())  {
                val resultJSON = JSONObject(result)
                val rates = resultJSON.getJSONObject("rates")
                multiplier = rates.getString("INR").toDouble()
                val ans = num * multiplier
                inrT.append("$num USD = $ans INR")
            }
            else    {
                Toast.makeText(baseContext, "Error Occured Try Again", Toast.LENGTH_SHORT).show()
            }

            super.onPostExecute(result)
        }
    }
}