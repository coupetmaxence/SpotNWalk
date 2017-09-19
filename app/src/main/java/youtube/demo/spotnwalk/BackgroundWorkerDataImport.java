package youtube.demo.spotnwalk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.List;
import java.util.Random;

import youtube.demo.spotnwalk.Modules.Spot;

import static android.content.ContentValues.TAG;

/**
 * Created by Maxence on 16/02/2017.
 */

public class BackgroundWorkerDataImport extends AsyncTask<Object, Void, JSONArray> {

    public ProgressDialog dialog;
    public IReturnData delegate = null;
    Context context;
    public ArrayList<Spot> tableau_spots;
    public int nbr_spots;
    GoogleMap googleMap;
    boolean parcours = false;
    double latitude_centre = 0;
    double longitude_centre = 0;
    int nbr_spot = 0;
    String cate;
    String descrip;
    SharedPreferences preferences;

    public BackgroundWorkerDataImport(Context ctx)
    {
        context = ctx;
        dialog = new ProgressDialog(context);
    }

    public interface OnUpdateListener {
        public void onUpdate(ArrayList<Spot> liste);
    }
    OnUpdateListener listener;
    public void setUpdateListener(OnUpdateListener listener) {
        this.listener = listener;
    }
    @Override
    protected JSONArray doInBackground(Object... params) {
        String data_url = "http://spotnwalk.alwaysdata.net/php/recuperation_spot.php";
        URL url = null;
        try {
            googleMap = (GoogleMap) params[0];
            String categorie = (String) params[1];
            parcours = (boolean) params[2];
            if(parcours)
            {
                latitude_centre = (double) params[3];
                longitude_centre = (double) params[4];
                nbr_spot = (int) params[5];
                preferences = (SharedPreferences) params[6];
            }
            if(categorie.equals("incontournables"))
                data_url = "http://spotnwalk.alwaysdata.net/php/recuperation_spot_incontournables.php";
            else if(categorie.equals("insolites"))
                data_url = "http://spotnwalk.alwaysdata.net/php/recuperation_spot_insolites.php";
            else if(categorie.equals("espaces_verts"))
                data_url = "http://spotnwalk.alwaysdata.net/php/recuperation_spot_espaces_verts.php";
            else if(categorie.equals("festivals"))
                data_url = "http://spotnwalk.alwaysdata.net/php/recuperation_spot_festivals.php";
            else if(categorie.equals("panoramas"))
                data_url = "http://spotnwalk.alwaysdata.net/php/recuperation_spot_panoramas.php";
            else if(categorie.equals("street_art"))
                data_url = "http://spotnwalk.alwaysdata.net/php/recuperation_spot_street_art.php";

            url = new URL(data_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String strJson="";
            String line;
            while((line = bufferedReader.readLine())!= null) {
                strJson += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();


            JSONArray jsonarray = new JSONArray(strJson);
            return jsonarray;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Récupération des spots");
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        tableau_spots = new ArrayList<Spot>();
        int compteur_spot = 0;


        for(int i=0; i<jsonArray.length(); i++)
        {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                String nom_spot = jsonObject.getString("nom");
                cate = jsonObject.getString("categorie");
                String categorie = jsonObject.getString("categorie");
                descrip = jsonObject.getString("description");
                String description = jsonObject.getString("description");
                double latitude = jsonObject.getDouble("latitude");
                double longitude = jsonObject.getDouble("longitude");

                Spot nouveau_spot = new Spot(latitude, longitude, nom_spot, categorie);
                addMapsMarker(googleMap, nouveau_spot.getCoordonates(), nouveau_spot.getName(), categorie, description);
                if(parcours)
                {
                    if(2 >= Math.acos(Math.sin(radians(latitude))*Math.sin(radians(latitude_centre))
                            +Math.cos(radians(latitude))*Math.cos(radians(latitude_centre))*Math.cos(radians(longitude-longitude_centre)))*6371)
                    {
                        tableau_spots.add(compteur_spot,nouveau_spot);

                        compteur_spot++;
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        if(parcours)
        {
           if(compteur_spot<=nbr_spot)
            {
                int taill = tableau_spots.size();
                Log.d("########zz##########", String.valueOf(taill));
                int taille = tableau_spots.size();
                Log.d("########00##########", String.valueOf(taille));
                listener.onUpdate(tableau_spots);
                //delegate.return_spots(tableau_spots);
            }
            else
            {
                int index = 0;
                ArrayList<Integer> liste_index = new ArrayList<Integer>();
                ArrayList<Spot> liste_spot = new ArrayList<>();
                for(int i=0; i<compteur_spot; i++)
                {
                    liste_index.add(i,i);
                }
                Log.d("########nbr##########", String.valueOf(nbr_spot));
                for(int i=0; i<nbr_spot; i++)
                {
                    Random r = new Random();
                    int i1 = r.nextInt(compteur_spot-index);
                    liste_spot.add(index, tableau_spots.get(i1));
                    liste_index.remove(i1);
                    index++;
                }
                int taill = tableau_spots.size();
                Log.d("########bb##########", String.valueOf(taill));
                int taille = liste_spot.size();
                Log.d("########00##########", String.valueOf(taille));
                listener.onUpdate(liste_spot);
                //delegate.return_spots(liste_spot);
            }
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void addMapsMarker(GoogleMap gMap, LatLng coord, String title, String categorie, String description)
    {
        float couleur;
        if(categorie.equals("Insolites"))
            couleur = BitmapDescriptorFactory.HUE_MAGENTA;
        else if(categorie.equals("Street art"))
            couleur = BitmapDescriptorFactory.HUE_CYAN;
        else if(categorie.equals("Incontournables"))
            couleur = BitmapDescriptorFactory.HUE_ORANGE;
        else if(categorie.equals("Expos et street art"))
            couleur = BitmapDescriptorFactory.HUE_AZURE;
        else if(categorie.equals("Panoramas"))
            couleur = BitmapDescriptorFactory.HUE_BLUE;
        else if(categorie.equals("Festivals et art éphémère"))
            couleur = BitmapDescriptorFactory.HUE_ROSE;
        else if(categorie.equals("Espaces verts"))
            couleur = BitmapDescriptorFactory.HUE_GREEN;
        else
            couleur = BitmapDescriptorFactory.HUE_YELLOW;

        gMap.addMarker(new MarkerOptions().position(coord)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(couleur))
                .snippet(description));
    }
    public double radians(double degre)
    {
        return degre*2*Math.PI/360.0;

    }

}
