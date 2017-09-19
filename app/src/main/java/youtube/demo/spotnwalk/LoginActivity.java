package youtube.demo.spotnwalk;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import youtube.demo.spotnwalk.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    EditText user_name_ET, password_ET;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_name_ET = (EditText) findViewById(R.id.email);
        password_ET = (EditText) findViewById(R.id.password);
    }

    public void OnLogin(View view)
    {
        String user_name = user_name_ET.getText().toString();
        String password = password_ET.getText().toString();
        String type = "login";

        BackgroundWorkerLogin backgroundWorkerLogin = new BackgroundWorkerLogin(this, LoginActivity.this);
        backgroundWorkerLogin.execute(type, user_name, password);
    }

    public void OnLogUp(View view)
    {
        Intent intent = new Intent(this, LogUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }
}

