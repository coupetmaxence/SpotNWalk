
  <img alt="logo" src="../..//tree/master/img/logo_appli.png"/>



# Project Spot&Walk

This Android application gives to its users the ability to share their favorites locations, panoramas, parks or even street art, in their city and all around the world. Moreover, the application can create a random path between diferent locations in the database in order to discover unknown locations of the town.

## Technical details

### Librairies

This application uses the Google Maps API to show the map and to add pointers. To create the random path, we use the Google Direction API with this [independent wrapper](https://github.com/akexorcist/Android-GoogleDirectionLibrary) for Android developement.

### Technologies

Our MySQL database is stored on a webservice called AlwaysData. It contains the different locations that users have added. With different php script stored on servers, we get a JSON file of the locations in our Android application. We just have to treat the JSON and to add all the locations to the user's map using Google Map API. When the user decides to add a location to our database, the path is exactly the same in the oposite way.

