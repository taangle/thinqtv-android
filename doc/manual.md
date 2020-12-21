The following is a list of what you need to know to successfully develop, maintain, update, and test the ThinQ.tv Android app.

**Development**

- By far the easiest way to do development work on the app is using Android Studio. See [here](https://developer.android.com/training/basics/firstapp) for a tutorial.
- The app is made out of “activities” and “fragments.” The MainActivity activity contains the fragments for each of the main pages in the app: the schedule, the About Us and Invite Us pages, and the profile page. The other activities primarily contain settings pages.
- The “data” package contains classes for handling user data. It also contains the “model” package, which contains models for various representations of data that are necessary for the app.
- The “ui.auth” package contains classes for handling authentication and authorization when users attempt to sign in.
- The “drawable” package contains the picture used by the app.
- The “layout” package contains the app’s page layouts.
- The “values” package contains the app default colors and the text.  This may help next developers to translate the app to other languages.

**Maintenance**

- The app depends on the following endpoints in the API and the way in which they function as of November 2020:
  - https://thinq.tv/api/v1/oauth/facebook
  - https://thinq.tv/api/v1/oauth/google
  - https://thinq.tv/api/v1/login
  - https://thinq.tv/api/v1/register
  - https://thinq.tv/api/v1/events
  - https://thinq.tv/api/v1/users
  - https://thinq.tv/api/v1/users/rsvps
- The app depends on the following website pages and the accompanying HTML ids in order to load informational pages:
  - https://www.thinq.tv/aboutus
    - id=appTitle1
    - id=appTitle2
    - id=appTitle3
    - id=appContent1
    - id=appContent2
    - id=appContent3
  - https://www.thinq.tv/drschaeferspeaking
    - id=appTitle1
    - id=appTitle2
    - id=appContent1
- The app depends on the URL for viewing another user’s profile to have the following format:
  - https://www.thinq.tv/<permalink>
  - Within this page, there is one accompanying HTML id required in order to properly display the user’s profile
    - id=appBackground
- The app depends on configurations in the respective developer consoles of Google and Facebook for OAuth sign-in. Each requires a hash of the release key, and each will provide you with a client ID that needs to be in the app’s strings.xml file.
  - https://console.developers.google.com/
  - https://developers.facebook.com/apps/

**Updates**

- To release the latest version of the master branch of the app to the Google Play Store:
  1. Open Android Studio
  1. Make sure the current branch is “master” by looking at the bottom right corner of the window
  1. Select VCS > Update Project
  1. Select VCS > Git > Pull
  1. Select Build > Generate Signed Bundle / APK
      1. Select APK
      1. Keep selecting Next
      1. Select Finish
  1. Allow the APK to build in the background
  1. Follow the instructions [here](https://support.google.com/googleplay/android-developer/answer/9859348)

**Testing**

- To run the app’s automated unit tests:
  1. Right-click the “com.thinqtv.thinqtv_android (test)” directory in Android Studio
  1. Select “Run Tests in thinqtv_android”
  1. If an error appears reporting that “Command line is too long,” select the link titled “thinqtv_android in thinqtv-android-app.app”
      1. Next to “Shorten command line,” select “JAR manifest”
      1. The test configuration should now be the default run configuration, so you can run the tests again by simply selecting the green play button at the top of the window
- To run the app's automated integration tests:
  - Follow the above procedure with the directory labeled “com.thinqtv.thinqtv_android (androidTest)” in Android Studio
- See also the manual testing instructions in this repository


