Deployment Guide 

Project structure:
The java folder contains all the source code including user interface functionality and service components.
The res folder contains all the resource files like images, layouts and xml files related to user interface design.
The manifests folder contains configuration files managing the application permissions.
The assets folder contains font files and html files.
The better-pickers module is a library module whose compiled resources are included in the project at the build time.

Starting point of the application is TUMitfahrApplication.java, which governs the services used for the API controllers. From the end user point of view, there are user interface components such as list views, items, buttons which are implemented in activity and fragment classes in the ui package. 

There are services for different controllers which assign the required values to the designated REST client. These clients compose the given values into request components and finally, API Services sum up the final request. The response from the server is collected via callbacks which are defined in the REST Client and later classified in the services. All functionalities follow this route.

The following credentials have to be added (to the following positions):
Google Analytics Tracking ID (in app_tracker.xml)
Google Places API key (in GooglePlacesAPIHelper.java)

The following changes have to be made before deploying the application to Google Play:
Set version code and version name (in the app's build.gradle)
Add signing information (in the app's build.gradle)
Set the url of the backend (in TUMitfahrApplication.java)
Change Build Variant from releaseFinalDebug to releaseFinalRelease
Run the application (then the APK is automatically created into the folder ...\tumitfahrer-android\app\build\outputs\apk)
