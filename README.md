# Client Server applications for  playing music

​	The client application is used to play music from a list of songs stored in the clip-service app. AIDL interface is implemented to allow communication between the client and the server app.

​	The client app has various buttons to start/stop the service or to pause/play the songs from the given list. It then communicates with the server app to play those songs and display a notification acknowledging the same.

Refer: 

1. [Android Interface Definition Language](https://developer.android.com/guide/components/aidl)


​	Following screenshots show the User Interface from the client application and notification.

<img src="https://github.com/yashchitre03/Android-Fragments-and-Permissions/blob/master/portrait_1.png" alt="alt text" width="288" height="576">

#### Application before starting the service



-----------------------

<img src="https://github.com/yashchitre03/Android-Fragments-and-Permissions/blob/master/portrait_2.png" alt="alt text" width="288" height="576">

#### Application showing song options



---------------------------------------

<img src="https://github.com/yashchitre03/Android-Fragments-and-Permissions/blob/master/landscape_fragments.png" alt="alt text" width="288" height="576">

#### Notification if a song is playing
