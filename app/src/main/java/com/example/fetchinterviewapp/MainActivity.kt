package com.example.fetchinterviewapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL
import java.util.Locale
import java.util.concurrent.Executors

// Data class to parse the JSON data and turn it into accessible children
@Serializable
data class JSONData(val id: Int?, val listId: Int?, val name: String? = null) {
    // Get the int from the name field if there is a value, to be used for sorting
    var strippedName = if (!name.isNullOrEmpty()) name.replace("Item ", "").toInt() else null
}

class MainActivity : AppCompatActivity() {
    private var url = "https://fetch-hiring.s3.amazonaws.com/hiring.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Make sure loading widget is visible during data import
        findViewById<ConstraintLayout>(R.id.loading).visibility = View.VISIBLE

        try {
            //using kotlinx serialization to asynchronously import data
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())

            executor.execute {//asynchronous execution
                val dataFromURL = URL(url).readText() //reads entire set of data from the url
                val data = Json.decodeFromString<List<JSONData>>(dataFromURL)//decode info taken in a list of JSONData children
                    .filter { !it.name.isNullOrEmpty() }//filter out any names null or empty
                val sortedData =
                    data.sortedWith(compareBy(JSONData::listId, JSONData::strippedName))
                        .groupBy { it.listId }//sort by listId first, then by the int in the name field, then group by listIds

                Log.d("test",sortedData[1].toString())
                handler.post {//post upon async task completion

                    // Posts each item in the RecyclerView component via the Custom Adapter
                    val customAdapter = CustomAdapter(sortedData)
                    val recycler: RecyclerView = findViewById(R.id.recycler)
                    recycler.layoutManager = LinearLayoutManager(this)
                    recycler.adapter = customAdapter
                    findViewById<ConstraintLayout>(R.id.loading).visibility = View.GONE//loading widget disappears on data load
                }
            }
        // If any exceptions/errors are thrown, catch here and show a Toast message to user
        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Error parsing data from URL, please check your internet and try again.",
                Toast.LENGTH_SHORT
            ).show()
        }
        catch (err:Error){
            Toast.makeText(
                this,
                "Fatal Error, please check your internet and try again.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

class CustomAdapter(private val data: Map<Int?, List<JSONData>>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Finds each TextView in layout file
        val idView: TextView = view.findViewById(R.id.idView)
        val listIdView: TextView = view.findViewById(R.id.listIdView)
        val nameView: TextView = view.findViewById(R.id.nameView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Attach each view in the Recycler view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Puts data in respective TextViews, joins each id and name group to a single string
        try{
            holder.idView.text = data[position+1]?.joinToString { it.id.toString() }
            holder.listIdView.text = String.format(Locale.ROOT,"%1d", (position+1))
            data[position+1]?.get(0)?.listId.toString()
            holder.nameView.text = data[position+1]?.joinToString { it.name.toString() }
        }catch(e:Exception){
            Log.d("Full Error Message", e.message.toString())
        }
    }
}
