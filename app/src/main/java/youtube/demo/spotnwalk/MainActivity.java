package youtube.demo.spotnwalk;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import youtube.demo.spotnwalk.Fragments.ImportFragment;
import youtube.demo.spotnwalk.Fragments.MainFragment;
import youtube.demo.spotnwalk.Modules.Spot;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, DirectionCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SupportMapFragment sMapFragment;
    GoogleMap map;


    double latitude_centre_parcours = 0;
    double longitude_centre_parcours = 0;
    LatLng centre_carte;
    int nbr_spot = 0;
    String categorie_parcours = "";
    boolean parcours = false;
    GoogleApiClient mGoogleApiClient;
    public final static String USER_REGISTRED = "user_registred";
    public final static String USER_NAME = "user_name";
    public final static String USER_EMAIL = "user_email";
    public final static String USER_LAST_NAME = "user_last_name";
    SharedPreferences preferences;
    ArrayList<Spot> liste_spot_parcours;
    private String serverKey = "AIzaSyC38Y0CEBIaFYovRNb0hDwWlpH3QHQV-d0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // On récupère l'éditeur de préférences pour stocker les données
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        // Portion de code servant à afficher la page de connexion à chaque lancement de l'appli
        // TODO : à retirer
        /*editor.putBoolean(USER_REGISTRED, false);
        editor.commit();*/

        // On récupère les éventuelles valeurs mises dans l'intent (i.e: si l'activité a été lancée depuis
        // une autre activité
        Bundle b = getIntent().getExtras();
        if (b != null) {
            String source = b.getString("activity_source", "default");
            // Si on vient d'ajouter un lieu à la carte
            if (source.equals("AddLocationActivity")) {
            }
            // Si on vient de se connecter
            else if (source.equals("LoginActivity")) {
                editor.putBoolean(USER_REGISTRED, true);
                editor.commit();
            }
            // Si on vient de s'enregistrer
            else if (source.equals("LogUpActivity")) {
                String name = b.getString("NAME", "Name");
                String last_name = b.getString("LAST_NAME", "Last name");
                String email = b.getString("EMAIL", "email");
                editor.putBoolean(USER_REGISTRED, true);
                editor.putString(USER_EMAIL, email);
                editor.putString(USER_LAST_NAME, last_name);
                editor.putString(USER_NAME, name);
                editor.commit();
            }
            // Si on viens de demander un parcours
            else if (source.equals("Parcours")){
                parcours = true;
                latitude_centre_parcours = b.getDouble("latitude", 0);
                longitude_centre_parcours = b.getDouble("longitude", 0);
                nbr_spot = b.getInt("nbr_spot", 0);
                categorie_parcours = b.getString("categorie", "");
            }
        }

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // On vérifie que l'utilisateur est bien enregistré
        // S'il ne l'est pas on le redirige vers la page de connexion
        boolean registred = preferences.getBoolean(USER_REGISTRED, false);
        if (!registred) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        // Affectation du layout principal et de la toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Affectation du floating button et de l'action à réaliser quand on appuis dessus
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addLocationIntent = new Intent(MainActivity.this, AddLocationActivity.class);
                startActivity(addLocationIntent);
            }
        });

        // Affectation du drawer et du toggle (listener du drawer)
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView info_nom = (TextView) headerLayout.findViewById(R.id.info_nom_prenom);
        info_nom.setText(preferences.getString(USER_NAME, "Name") + " " + preferences.getString(USER_LAST_NAME, "Last_Name"));
        TextView info_email = (TextView) headerLayout.findViewById(R.id.info_email);
        info_email.setText(preferences.getString(USER_EMAIL, "Email"));


        // On récupère le fragment de la carte
        sMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.main_map);
        sMapFragment.getMapAsync(MainActivity.this);

        /*ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);*/
        //initalisation_carte(map);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.action_settings) {

        } else if (id == R.id.action_about) {

        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_all) {
            map.clear();
            actualisation_carte(map, "all");
        } else if (id == R.id.nav_incontournables) {
            map.clear();
            actualisation_carte(map, "incontournables");
        } else if (id == R.id.nav_street_art) {
            map.clear();
            actualisation_carte(map, "street_art");
        } else if (id == R.id.nav_insolites) {
            map.clear();
            actualisation_carte(map, "insolites");
        } else if (id == R.id.nav_espaces_verts) {
            map.clear();
            actualisation_carte(map, "espaces_verts");
        } else if (id == R.id.nav_festivals) {
            map.clear();
            actualisation_carte(map, "festivals");
        } else if (id == R.id.nav_panoramas) {
            map.clear();
            actualisation_carte(map, "panoramas");
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(MainActivity.this, Parcours.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void actualisation_carte(GoogleMap googleMap, String categorie) {
        if(isOnline())
        {
            if(parcours)
            {
                BackgroundWorkerDataImport backgroundWorkerDataImport = new BackgroundWorkerDataImport(this);
                backgroundWorkerDataImport. setUpdateListener(new BackgroundWorkerDataImport. OnUpdateListener() {
                    @Override
                    public void onUpdate(ArrayList<Spot> liste) {


                        int taille = liste.size();
                        if(taille!=0)
                        {
                            LatLng start = liste.get(0).getCoordonates();
                            LatLng end = liste.get(taille-1).getCoordonates();
                            for(int i=0; i<taille-1; i++)
                            {
                                LatLng origin = liste.get(i).getCoordonates();
                                LatLng destination = liste.get(i+1).getCoordonates();
                                requestDirection(origin, destination);
                            }
                            requestDirection(start,end);
                        }
                    }
                });
                backgroundWorkerDataImport.execute(googleMap, categorie, true, latitude_centre_parcours, longitude_centre_parcours, nbr_spot, preferences);




            }
            else
                new BackgroundWorkerDataImport(this).execute(googleMap, categorie, false);
        }
        else
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Problème de connexion");
            alertDialog.setMessage("Il semblerait que vous n'êtes pas connecté au réseau.\nConnectez-vous pour profiter des services de Spot&Walk");
            alertDialog.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initalisation_carte(GoogleMap googleMap) {

        actualisation_carte(googleMap, "all");
        requestDirection(new LatLng(2.5,45), new LatLng(2.5,46));
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /*boolean gps_enabled = false;
boolean network_enabled = false;

LocationManager lm = (LocationManager) mCtx
                .getSystemService(Context.LOCATION_SERVICE);

gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

Location net_loc = null, gps_loc = null, finalLoc = null;

if (gps_enabled)
    gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
if (network_enabled)
    net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

if (gps_loc != null && net_loc != null) {

    //smaller the number more accurate result will
    if (gps_loc.getAccuracy() > net_loc.getAccuracy())
        finalLoc = net_loc;
    else
        finalLoc = gps_loc;

        // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)

} else {

    if (gps_loc != null) {
        finalLoc = gps_loc;
    } else if (net_loc != null) {
        finalLoc = net_loc;
    }
}*/



        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        // Création du point de la tour Eiffel
        double tab_lattitude2[] = {48, 51, 30};
        double tab_longitude2[] = {2, 17, 40};
        String title2 = "Tour Eiffel";
        Spot tour_eiffel = new Spot(tab_lattitude2, tab_longitude2, title2);


        // On positionne la caméra sur la tour Eiffel
        if(centre_carte!=null)
        {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centre_carte, 12.0f));
        }
        else
        {
            Log.d("################", "Pas centré");
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tour_eiffel.getCoordonates(), 12.0f));
        }



        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file infowindowlayout.xml
                View v = getLayoutInflater().inflate(R.layout.map_infobox_layout, null);

                LatLng latLng = arg0.getPosition();
                TextView tv1 = (TextView) v.findViewById(R.id.textView1);
                TextView tv2 = (TextView) v.findViewById(R.id.textView2);
                String title = arg0.getTitle();
                String informations = arg0.getSnippet();

                tv1.setText(title);
                tv2.setText(informations);
                return v;

            }
        });
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {


            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        initalisation_carte(map);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    map.setMyLocationEnabled(true);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public void requestDirection(LatLng origin, LatLng destination) {
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.WALKING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {

            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            map.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
            Toast.makeText(this,"success", Toast.LENGTH_SHORT);
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(this,"falure", Toast.LENGTH_SHORT);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            centre_carte = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
