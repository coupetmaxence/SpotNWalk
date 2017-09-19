package youtube.demo.spotnwalk;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * A login screen that offers login via email/password.
 */
public class LogUpActivity extends AppCompatActivity {

    EditText email_ET, password_ET, user_last_name_ET, user_name_ET,confirmez_ET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_up);
        email_ET = (EditText) findViewById(R.id.email);
        password_ET = (EditText) findViewById(R.id.password);
        user_name_ET = (EditText) findViewById(R.id.name);
        user_last_name_ET = (EditText) findViewById(R.id.last_name);
        confirmez_ET =(EditText) findViewById(R.id.password_verification);
    }

    protected boolean VerifierMdP(){
        boolean verification=false;
        String confirmez = confirmez_ET.getText().toString();
        String password = password_ET.getText().toString();
        if(password.equals(confirmez)){
            verification=true;
        }
        return verification;
    }


    public void OnLogUp(View view)
    {
        String user_name = user_name_ET.getText().toString();
        String password = password_ET.getText().toString();
        String email = email_ET.getText().toString();
        String user_last_name = user_last_name_ET.getText().toString();
        String type="logup";

        if(user_name==""||user_last_name==""||email==""||password==""){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Champs");
            alertDialog.setMessage("Veuillez remplir tous les champs\n");
            alertDialog.show();
        }

        if(VerifierMdP()==false) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Mot de Passe");
            alertDialog.setMessage("Erreur d'identification, vos mots de passes ne correspondent pas.\n");
            alertDialog.show();
        }

        // envoie la requette sql pour enregistrer l'utilisateur
        BackgroundWorkerLogin backgroundWorkerLogin = new BackgroundWorkerLogin(this, LogUpActivity.this);
        backgroundWorkerLogin.execute(type,user_name, user_last_name, email, password);
    }
}