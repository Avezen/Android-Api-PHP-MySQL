package com.example.maniekcs1995.defotapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import java.net.URL;
import java.net.URLEncoder;

public class RegisterFormActivity extends AppCompatActivity {

    private EditText login;
    private EditText password, password2, email;
    private TextView info;
    private Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        login = (EditText) findViewById(R.id.rLoginBox);
        email = (EditText) findViewById(R.id.rEmailBox);
        password = (EditText) findViewById(R.id.rPasswordBox);
        password2 = (EditText) findViewById(R.id.rPasswordBox2);
        info = (TextView) findViewById(R.id.rTextView);
        registerButton = findViewById(R.id.rRegisterButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View view) {
                try {
                    register(login.getText().toString(), password.getText().toString(), password2.getText().toString(), email.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void register(String login, String password, String password2, String email) throws IOException {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);

        if (TextUtils.isEmpty(login)) {
            info.setText("Proszę wpisać swój login");
            info.requestFocus();
            return;
        }else if(login.length() < 3)
            return;

        /*else if (backgroundWorker.loginExist(login)){
            info.setText("Istnieje konto o podanym loginie");
            info.requestFocus();
            return;
        }
*/

        if (TextUtils.isEmpty(email)) {
            info.setText("Proszę wpisać swój adres email");
            info.requestFocus();
            return;
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return;
        /*else if (backgroundWorker.emailExist(email)){
            info.setText("Istnieje konto o podanym emailu");
            info.requestFocus();
            return;
        }
*/
        if (TextUtils.isEmpty(password)) {
            info.setText("Proszę wpisać swoje hasło");
            info.requestFocus();
            return;
        }else if(password.length() < 6)
            return;

        if (TextUtils.isEmpty(password2)) {
            info.setText("Proszę ponownie wpisać hasło");
            info.requestFocus();
            return;
        }

        if(!password.equals(password2)){
            info.setText("Podane hasła nie są indentyczne");
            info.requestFocus();
            return;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String post_data = URLEncoder.encode("login", "UTF-8")+"="+URLEncoder.encode(login, "UTF-8")
                +"&"+URLEncoder.encode("pass", "UTF-8")+"="+URLEncoder.encode(hashedPassword, "UTF-8")
                +"&"+URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email, "UTF-8");


        String apicall = Api.CREATE_USER_URL;


        backgroundWorker.execute(apicall,post_data);


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
            alertDialog.setTitle("Register Status");
            alertDialog.setMessage("Brak połączenia z internetem lub bazą danych");

            alertDialog.show();

        }

        @Override
        protected void onPostExecute(String result){
            alertDialog.setMessage(result);

            try {
                JSONObject obj = new JSONObject(result);
                Boolean isError = obj.getBoolean("error");
                if (!isError) {
                    alertDialog.setMessage("Udało się. Teraz możesz się zalogować");

                    alertDialog.show();
                    Intent intent = new Intent(RegisterFormActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    alertDialog.setMessage(obj.getString("message"));
                    alertDialog.show();
                }
            }catch(JSONException e){
                e.getStackTrace();
            }

        }
/*
        protected boolean loginExist(String login) {
            String apicall = "loginexist";
            String response = doGet(apicall,login);

            try {
                JSONObject obj = new JSONObject(response);
                login = obj.getString("error");
                if(login.equals("true"))
                    return false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }

        protected boolean emailExist(String email) {
            String apicall = "emailexist";
            String response = doGet(apicall,email);

            try {
                JSONObject obj = new JSONObject(response);
                email = obj.getString("error");
                if(email.equals("true"))
                    return false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        */
    }
}
