package youtube.demo.spotnwalk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.dynamic.LifecycleDelegate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Parcours extends AppCompatActivity implements OnMapReadyCallback {
    private Button btnRequestDirection;
    private GoogleMap googleMap;
    private String serverKey = "AIzaSyC38Y0CEBIaFYovRNb0hDwWlpH3QHQV-d0";
    private LatLng camera = new LatLng(48.855278631136336, 2.3158263042569165);
    private LatLng origin = new LatLng(48.855278631136336, 2.3158263042569165);
    private LatLng origin2 = new LatLng(48.866106462033514, 2.3124614730477333);
    private LatLng destination = new LatLng(48.85836339757817, 2.2944792732596397);
    private Circle circle;
    private int radius= 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.parcours_map)).getMapAsync(this);
        final SeekBar seekBar=(SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                radius = progress+500;
                circle = AddCircle(camera,radius);
                if(radius < 1500)
                {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 13));
                } else
                {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 12));
                }


            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googlemap) {
        this.googleMap = googlemap;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera, 13));
        if(circle!=null)
            circle.remove();
        circle = AddCircle(camera, 500);
        this.googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition arg0) {
                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        LatLng latLng= googleMap.getCameraPosition().target;
                        camera = new LatLng(latLng.latitude,latLng.longitude);
                        circle = AddCircle(camera, radius);
                    }
                });
            }
        });
    }

    public Circle AddCircle(LatLng center, int radius)
    {
        if(circle!=null)
            circle.remove();
        return googleMap.addCircle(new CircleOptions()
                .center(center)
                .radius(radius)
                .strokeWidth(0f)
                .fillColor(0x550000FF));
    }

    public void submit_parcours(View v)
    {
        double rayon_terre = 6371*1000;
        double alpha = Math.acos((2*Math.pow(rayon_terre,2)-Math.pow(radius,2))/(4*rayon_terre));
        Intent intent = new Intent(Parcours.this, MainActivity.class);
        Bundle b = new Bundle();
        b.putDouble("rayon", radius);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String categorie = spinner.getSelectedItem().toString();
        b.putString("categorie", categorie);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        String nbr_spot = spinner.getSelectedItem().toString();
        Log.d("############Parcours", nbr_spot);
        int nbr = 4;
        if(nbr_spot.equals("2-3"))
            nbr=3;
        else if(nbr_spot.equals("4-6"))
            nbr=6;
        else if(nbr_spot.equals("7-10"))
            nbr=9;
        else if(nbr_spot.equals("11+"))
            nbr=14;
        b.putInt("nbr_spot", nbr);
        b.putDouble("latitude", camera.latitude);
        b.putDouble("longitude", camera.longitude);
        b.putString("activity_source", "Parcours");
        intent.putExtras(b);
        startActivity(intent);

    }


}