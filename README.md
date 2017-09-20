<img alt="logo" src="../master/img/logo_appli.png" width=200>
  



# Project Spot&Walk

This Android application gives to its users the ability to share their favorites locations, panoramas, parks or even street art, in their city and all around the world. Moreover, the application can create a random path between diferent locations in the database in order to discover unknown locations of the town. Finally, those locations are grouped in catagories and the user can choose to show only a certain category of locations in the application.

## Technical details

### Librairies

This application uses the Google Maps API to show the map and to add pointers. To create the random path, we use the Google Direction API with this [independent wrapper](https://github.com/akexorcist/Android-GoogleDirectionLibrary) for Android developement.

### Technologies

Our MySQL database is stored on a webservice called AlwaysData. It contains the different locations that users have added. With different php script stored on servers, we get a JSON file of the locations in our Android application. We just have to treat the JSON and to add all the locations to the user's map using Google Map API. When the user decides to add a location to our database, the path is exactly the same in the oposite way.

## Screenshots

Main view            |  Categories         |Adding a location |Creating a path            |  Path in the application    
:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:
<img src="../master/img/marqueurs.png">  |  <img src="../master/img/categories.png"> | <img src="../master/img/ajout_spot.png"> |<img src="../master/img/ajout_parcours.png">  |  <img src="../master/img/parcours.png"> 

Creating a path            |  Path in the application         
:-------------------------:|:-------------------------:
<img src="../master/img/ajout_parcours.png">  |  <img src="../master/img/parcours.png"> 


