package youtube.demo.spotnwalk.Modules;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Maxence on 07/02/2017.
 */

public class Spot implements Serializable, Cloneable{

    // *******************************************
    //
    //          Variables de la classe
    //
    // *******************************************
    private double latitude_tab[];
    private double longitude_tab[];
    private double latitude;
    private double longitude;
    private LatLng coordonees;
    private String nom_du_spot;
    private String categorie;




    // *******************************************
    //
    //              Constructeurs
    //
    // *******************************************
    public Spot()
    {
        latitude = 0;
        longitude = 0;
        coordonees = new LatLng(latitude, longitude);
        nom_du_spot = "nom inconnu";
        categorie = "categorie inconnue";
    }

    public Spot(double latitude_tab_spot[], double longitude_tab_spot[], String nom_spot)
    {
        for (double v : latitude_tab = latitude_tab_spot) {

        }
        for (double v : longitude_tab = longitude_tab_spot) {

        }

        latitude = latitude_tab[0] + minutesToDegres(latitude_tab[1])+secondesToDegres(latitude_tab[2]);
        longitude = longitude_tab[0] + minutesToDegres(longitude_tab[1])+secondesToDegres(longitude_tab[2]);
        coordonees = new LatLng(latitude, longitude);
        nom_du_spot = nom_spot;
    }

    public Spot(double latitude_spot, double longitude_spot, String nom_spot, String v_categorie)
    {
        latitude = latitude_spot;
        longitude = longitude_spot;
        coordonees = new LatLng(latitude, longitude);
        nom_du_spot = nom_spot;
        categorie = v_categorie;
    }

    public Spot(double latitude_tab_spot[], double longitude_tab_spot[], String nom_spot, String categorie_spot)
    {
        for (double v : latitude_tab = latitude_tab_spot) {

        }
        for (double v : longitude_tab = longitude_tab_spot) {

        }

        latitude = latitude_tab[0] + minutesToDegres(latitude_tab[1])+secondesToDegres(latitude_tab[2]);
        longitude = longitude_tab[0] + minutesToDegres(longitude_tab[1])+secondesToDegres(longitude_tab[2]);
        coordonees = new LatLng(latitude, longitude);
        nom_du_spot = nom_spot;
        categorie = categorie_spot;
    }
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }




    // *******************************************
    //
    //              Accesseurs
    //
    // *******************************************
    public LatLng getCoordonates()
    {
        return coordonees;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public String getName()
    {
        return nom_du_spot;
    }




    // *******************************************
    //
    //          Fonctions annexes
    //
    // *******************************************
    public double secondesToDegres(double secondes)
    {
        return secondes/3600;
    }
    public double minutesToDegres(double minutes)
    {
        return minutes/60;
    }
}
