package com.codepath.apps.restclienttemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    lateinit var client:TwitterClient

    lateinit var rvTweets: RecyclerView

    lateinit var  adapter: TweetsAdapter

    lateinit var SwipeContainer: SwipeRefreshLayout


    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this)

        SwipeContainer = findViewById(R.id.swipeContainer)

        SwipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing timeline")
            populationHomeTimeline()
        }

        // Configure the refreshing colors
        SwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);




        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)


        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter


        populationHomeTimeline()
    }

    fun populationHomeTimeline() {
        client.getHomeTimeline(object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "onSuccess")

                val jsonArray = json.jsonArray

                try {
                    //Clear out currently fetched tweets
                    adapter.clear()
                    val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                    tweets.addAll(listOfNewTweetsRetrieved)
                    adapter.notifyDataSetChanged()
                    // Now we call setRefreshing(false) to signal refresh has finished
                    SwipeContainer.setRefreshing(false);
            } catch (e:JSONException){
                    Log.e(TAG, "JSON Exception $e")
                }


            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG, "onFailure")
            }


        } )
    }

    companion object {
        val TAG = "TimelineActivity"
    }
}
