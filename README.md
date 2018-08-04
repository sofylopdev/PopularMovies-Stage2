# PopularMovies-Stage2
Third Project for the Android Nanodegree

In order to run this project, you need to get an API key from [TMDb](https://www.themoviedb.org/).

Then search for the 'todo:Replace key here' and replace with your own key.

## Project Overview

Welcome back to Popular Movies! In this second and final stage, you’ll add additional functionality to the app you built in Stage 1.

You’ll add more information to your movie details view:

 - You’ll allow users to view and play trailers ( either in the youtube app or a web browser).
 - You’ll allow users to read reviews of a selected movie.
 - You’ll also allow users to mark a movie as a favorite in the details view by tapping a button(star).
 - You'll create a database to store the names and ids of the user's favorite movies (and optionally, the rest of the information needed to display their favorites collection while offline).
 - You’ll modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.

## What Will I Learn After Stage 2?

You will build a fully featured application that looks and feels natural on the latest Android operating system (Nougat, as of November 2016).

## Screenshots
|Movies List| Spinner Options | Landscape List |
| --- | --- | --- |
| ![alt text](https://github.com/sofylopdev/PopularMovies-Stage2/blob/master/MovieList.png) | ![alt text](https://github.com/sofylopdev/PopularMovies-Stage2/blob/master/DetailSpinner.png) | ![alt text](https://github.com/sofylopdev/PopularMovies-Stage2/blob/master/LandscapeList.png) | 

|No Internet| No Movies | Favorites |
| --- | --- | --- |
| ![alt text](https://github.com/sofylopdev/PopularMovies-Stage2/blob/master/No_Internet.png) | ![alt text](https://github.com/sofylopdev/PopularMovies-Stage2/blob/master/No_Movies.png) | ![alt text](https://github.com/sofylopdev/PopularMovies-Stage2/blob/master/Favorites.png) | 

|Movie Details top| Movie Details bottom | 
| --- | --- | 
| ![alt text](https://github.com/sofylopdev/PopularMovies-Stage2/blob/master/MovieDetail1.png) | ![alt text](https://github.com/sofylopdev/PopularMovies-Stage2/blob/master/MovieDetail2.png) |

|Tablet List| Tablet Details | 
| --- | --- | 
| ![alt text](https://github.com/sofylopdev/PopularMovies-Stage2/blob/master/TabletList.png) | ![alt text](https://github.com/sofylopdev/PopularMovies-Stage2/blob/master/TabletDetails.png) |

## Libraries Used
 - [Picasso](http://square.github.io/picasso/)
 - [Retrofit 2](https://github.com/square/retrofit)
 - [Gson](https://github.com/google/gson) (as a converter in Retrofit)
 - [RoundedImageView](https://github.com/vinc3m1/RoundedImageView)
 - [Picasso 2 OkHttp 3 Downloader](https://github.com/JakeWharton/picasso2-okhttp3-downloader)
 
## Extra Funtionalities Implemented
 - Extended the favorites database to store the movie poster, synopsis, user rating, and release date, and display them even when offline.
 - Sharing functionality to allow the user to share YouTube's videos from the movie details screen.
 - 'Lazy loading' / 'Infinite scroll' in MainActivity
 - Custom Picasso caching
 - ConnectivityReceiver that extends BroadcastReceiver to 'listen' to network changes
 - Animation (Transition) between MovieItem and Poster in DetailsActivity in version >= 21

## Icons

From https://icons8.com/

## License

http://www.apache.org/licenses/LICENSE-2.0
