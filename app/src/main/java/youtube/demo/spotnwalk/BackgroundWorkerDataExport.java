package youtube.demo.spotnwalk;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Maxence on 19/02/2017.
 */

public class BackgroundWorkerDataExport extends AsyncTask<String, Void, String> {

    Context context;
    BackgroundWorkerDataExport(Context ctx)
    {
        context = ctx;
    }
    @Override
    protected String doInBackground(String... strings) {
        String export_url = "http://spotnwalk.alwaysdata.net/php/add_spot_from_android.php";


        URL url = null;
        try {
            String nom = strings[0];
            String categorie = strings[1];
            String description = strings[2];
            String latitude = strings[3];
            String longitude = strings[4];
            url = new URL(export_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("nom_spot","UTF-8")+"="+URLEncoder.encode(nom,"UTF-8")+"&"
                    +URLEncoder.encode("categorie","UTF-8")+"="+URLEncoder.encode(categorie,"UTF-8")+"&"
                    +URLEncoder.encode("latitude","UTF-8")+"="+URLEncoder.encode(latitude,"UTF-8")+"&"
                    +URLEncoder.encode("longitude","UTF-8")+"="+URLEncoder.encode(longitude,"UTF-8")+"&"
                    +URLEncoder.encode("description","UTF-8")+"="+URLEncoder.encode(description,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result="";
            String line;
            while((line = bufferedReader.readLine())!= null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
