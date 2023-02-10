package com.mnw.deverestinterview

import android.app.Application
import com.mnw.deverestinterview.net.MoviesClient
import dagger.hilt.android.HiltAndroidApp

/**
 *
 * Your task is to design and develop an Android application, which displays movies and their budget.
 * You have 3 days to fulfill this assignment.
 * To get the necessary information you will use The Movie DB’s API for search and details.

Necessary information to retrieve data:
API key: 555dd34b51d2f5b7f9fdb39e04986933


Requirements are:

- The application can connect to the TMDB API and displays the damped response.

- The response is rendered on a user-friendly GUI. You have to represent the search results in a list with an image and the budget. Additionally, you can show some more information of your choice (e.g. rate, …)

- Users can type a search keyword and the results are filtered accordingly.

 */

@HiltAndroidApp
open class MovieApplication: Application() {

    override fun onCreate() {
        super.onCreate()



    }
}