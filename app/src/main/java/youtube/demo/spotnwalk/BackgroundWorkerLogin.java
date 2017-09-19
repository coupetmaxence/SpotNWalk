package youtube.demo.spotnwalk;



        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.preference.PreferenceManager;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.net.URLEncoder;

/**
 * Created by ProgrammingKnowledge on 1/5/2016.
 */
public class BackgroundWorkerLogin extends AsyncTask<String,Void,String> {
    public final static String USER_REGISTRED = "user_registred";
    Boolean signin = true;
    Context context;
    Activity activity;
    AlertDialog alertDialog;
    String user_name;
    String user_last_name;
    String email;
    String password;
    BackgroundWorkerLogin (Context ctx, Activity activity) {
        context = ctx;
        this.activity = activity;
    }
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String login_url = "http://spotnwalk.alwaysdata.net/php/login.php";
        String logup_url = "http://spotnwalk.alwaysdata.net/php/register.php";
        if(type.equals("login")) {
            try {
                signin = true;
                String user_name = params[1];
                String password = params[2];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (type.equals("logup"))
        {
            try {
                signin = false;
                user_name = params[1];
                user_last_name = params[2];
                email = params[3];
                password = params[4];
                URL url = new URL(logup_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"
                        +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"
                        +URLEncoder.encode("last_name","UTF-8")+"="+URLEncoder.encode(user_last_name,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(String result) {
        if(signin)
        {
            if(result.equals("ok"))
            {
                Intent intent = new Intent(activity, MainActivity.class);
                Bundle b = new Bundle();
                b.putString("activity_source", "LoginActivity");
                intent.putExtras(b);
                activity.startActivity(intent);
            }
            else
            {
                alertDialog.setMessage("connexion failed");
                alertDialog.show();
            }
        }
        else
        {
            if(result.equals("ok"))
            {
                Intent intent = new Intent(activity, MainActivity.class);
                Bundle b = new Bundle();
                b.putString("EMAIL", email);
                b.putString("NAME", user_name);
                b.putString("LAST_NAME", user_last_name);
                b.putString("activity_source", "LogUpActivity");
                intent.putExtras(b);
                activity.startActivity(intent);
            }
            else
            {
                alertDialog.setMessage(result);
                alertDialog.show();
            }
        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}