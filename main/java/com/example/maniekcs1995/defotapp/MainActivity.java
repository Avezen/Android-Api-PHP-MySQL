package com.example.maniekcs1995.defotapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

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
    private Button loginButton;
    private boolean doUserExist=false;
    private String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (EditText) findViewById(R.id.loginBox);
        password = (EditText) findViewById(R.id.passwordBox);
        info = (TextView) findViewById(R.id.textView);
        loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View view) {

                try {
                    validate(login.getText().toString(), password.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
               /*
                if (login.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
                    Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
                    startActivity(intent);
                }else{
                    info.setText("Wrong login or password");
                }
                */

            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void validate(String login, String password) throws IOException {

        if (TextUtils.isEmpty(login)) {
            info.setText("Please enter login");
            info.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            info.setText("Please enter your password");
            info.requestFocus();
            return;
        }


        String type = "login";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, login, password);


    }

     class BackgroundWorker extends AsyncTask<String,Void,String> {

        Context context;
        AlertDialog alertDialog;
        BackgroundWorker (Context ctx){
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params){
            String type = params[0];
            String login_url = Api.URL_CHECK_USER;

            if(type.equals("login")){
                try{
                    String login = params[1];
                    String password = params[2];
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8" ));
                    String post_data = URLEncoder.encode("login", "UTF-8")+"="+URLEncoder.encode(login, "UTF-8")
                            +"&"+URLEncoder.encode("pass", "UTF-8")+"="+URLEncoder.encode(password, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result="";
                    String line="";
                    while((line = bufferedReader.readLine()) != null){
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                    return result;
                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
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
                    JSONObject obj = new JSONObject(result);

                    result = obj.getString("doUserExist");

                    if (result == "true") {
                        Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
                        startActivity(intent);
                    } else if (result == "false") {
                        alertDialog.setMessage("Nieprawidłowy login lub hasło");
                        alertDialog.show();
                    }


                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
                }

            if(result != "true" && result != "false") {
                alertDialog.show();
            }
        }
    }
}


