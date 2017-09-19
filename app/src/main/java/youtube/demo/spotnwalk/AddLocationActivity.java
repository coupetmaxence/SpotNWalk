package youtube.demo.spotnwalk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Created by Maxence on 24/01/2017.
 */

public class AddLocationActivity extends AppCompatActivity implements OnMapReadyCallback
{
    SupportMapFragment sMapFragment;
    GoogleMap map;
    Marker marqueur;
    double latitude = 0;
    double longitude = 0;
    EditText nom, description;
    Spinner categorie;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        sMapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_map));
        nom = (EditText) findViewById(R.id.nom_spot_ET);
        description = (EditText) findViewById(R.id.description_ET);
        categorie = (Spinner) findViewById(R.id.spinner_categorie);

       sMapFragment.getMapAsync(AddLocationActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        init(googleMap);
        Toast bread = Toast.makeText(getApplicationContext(), "Zoomez au maximum pour être plus précis", Toast.LENGTH_LONG);
        bread.show();
    }
    public void init(GoogleMap googlemap)
    {
        map = googlemap;
        double tab_lattitude2[] = {48,51,30};
        double tab_longitude2[] = {2,17,40};
        double lattitude2 = tab_lattitude2[0]+minutesToDegres(tab_lattitude2[1])+secondesToDegres(tab_lattitude2[2]);
        double longitude2 = tab_longitude2[0]+minutesToDegres(tab_longitude2[1])+secondesToDegres(tab_longitude2[2]);
        LatLng tour_Eiffel = new LatLng(lattitude2,longitude2);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattitude2, longitude2), 12.0f));
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition arg0) {
                map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        LatLng latLng=map.getCameraPosition().target;
                        latitude = latLng.latitude;
                        longitude = latLng.longitude;
                    }
                });
            }
        });
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
    }

    public double secondesToDegres(double secondes)
    {
        return secondes/3600;
    }
    public double minutesToDegres(double minutes)
    {
        return minutes/60;
    }

    public void Submit_location(View v)
    {
        String nom_spot = nom.getText().toString();
        String description_texte = description.getText().toString();
        String categorie_texte = categorie.getSelectedItem().toString();
        String latitude_texte = String.valueOf(latitude);
        String longitude_texte = String.valueOf(longitude);
        BackgroundWorkerDataExport backgroundWorkerDataExport = new BackgroundWorkerDataExport(this);
        backgroundWorkerDataExport.execute(nom_spot, categorie_texte, description_texte, latitude_texte, longitude_texte);
        Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putDouble("latitude", latitude);
        b.putDouble("longitude", longitude);
        b.putString("activity_source", "AddLocationActivity");
        intent.putExtras(b);
        startActivity(intent);
    }
}
