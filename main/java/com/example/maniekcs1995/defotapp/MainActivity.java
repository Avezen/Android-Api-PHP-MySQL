package com.example.maniekcs1995.defotapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

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


public class MainActivity extends AppCompatActivity {


    private EditText login;
    private EditText password;
    private TextView info;
    private Button loginButton, registerButton, guestButton;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String Login = "login";
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        login = (EditText) findViewById(R.id.rLoginBox);
        password = (EditText) findViewById(R.id.passwordBox);
        info = (TextView) findViewById(R.id.textView);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        guestButton = findViewById(R.id.guestButton);
        sharedpreferences = getSharedPreferences("com.example.maniekcs1995.defotapp", MODE_PRIVATE);
        editor = sharedpreferences.edit();

        editor.remove("login");
        if(editor.commit())
            Toast.makeText(MainActivity.this, "wylogowano", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(MainActivity.this, "nie wylogowano", Toast.LENGTH_LONG).show();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View view) {
                try {
                    validate(login.getText().toString(), password.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterFormActivity.class);
                startActivity(intent);
            }
        });

        guestButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View view) {
                editor.putString("login","guest");
                editor.commit();
                Intent intent = new Intent(MainActivity.this,MainPageActivity.class);
                startActivity(intent);
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void validate(String login, String password) throws IOException {

        if (TextUtils.isEmpty(login)) {
            info.setText("Proszę wpisać swój login");
            info.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            info.setText("Proszę wpisać swoje hasło");
            info.requestFocus();
            return;
        }

        String post_data = URLEncoder.encode("login", "UTF-8")+"="+URLEncoder.encode(login, "UTF-8");

        String apicall = Api.LOGIN_URL;

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall, post_data);
    }





    protected String doPost(String... params){
        String apicall = params[0];
        String post_data = params[1];

        try {
            URL url = new URL(apicall);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();

            InputStream inputStream = httpURLConnection.getInputStream();

            String result = "";
            String line = "";

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            while ((line = bufferedReader.readLine()) != null) {
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

        return null;
    }

     class BackgroundWorker extends AsyncTask<String,Void,String> {

        Context context;
        AlertDialog alertDialog;
        BackgroundWorker (Context ctx){
            context = ctx;
        }


        @Override
        protected String doInBackground(String... params){

            String result = doPost(params);
            return result;
        }


        @Override
        protected void onPreExecute(){
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Login Status");
            alertDialog.setMessage("Brak połączenia z internetem");

        }


        @Override
        protected void onPostExecute(String result){

            try {

                if(result != null) {

                    JSONObject obj = new JSONObject(result);
                    String pw = password.getText().toString();
                    result = obj.getString("apicall");

                    switch (result) {
                        case "login":
                            try {
                                result = obj.getString("hash");
                                if (BCrypt.checkpw(pw, result)) {
                                    editor.putString("login", login.getText().toString() );
                                    editor.commit();
                                    Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
                                    startActivity(intent);
                                    
                                } else {
                                    alertDialog.setMessage("Nieprawidłowy login lub hasło");
                                    alertDialog.show();
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                                alertDialog.setMessage("Nieprawidłowy login lub hasło");
                                alertDialog.show();
                            }

                            break;
                    }
                }else{
                    alertDialog.show();
                }

            }catch(JSONException e){
                e.getStackTrace();
            }
        }
    }
}


