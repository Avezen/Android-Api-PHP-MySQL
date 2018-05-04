package com.example.maniekcs1995.defotapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
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

public class AddDefotActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_defot);

        sharedpreferences = getSharedPreferences("com.example.maniekcs1995.defotapp", MODE_PRIVATE);
        editor = sharedpreferences.edit();

        final EditText editTextDefotTitle = findViewById(R.id.editTextDefotTitle);
        final EditText editTextDefotDesc = findViewById(R.id.editTextDefotDesc);
        final EditText editTextDefotUrl = findViewById(R.id.editTextDefotUrl);
        Button addDefotButton = findViewById(R.id.addDefotButton);



        final String userId = sharedpreferences.getString("login", "DEFAULT");

        addDefotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addNewDefot(editTextDefotTitle.getText().toString(), editTextDefotDesc.getText().toString(), editTextDefotUrl.getText().toString(), 2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void addNewDefot(String title, String desc, String URL, int userId) throws IOException{

        String post_data = URLEncoder.encode("title", "UTF-8")+"="+URLEncoder.encode(title, "UTF-8")
                +"&"+ URLEncoder.encode("desc", "UTF-8")+"="+URLEncoder.encode(desc, "UTF-8")
                +"&"+ URLEncoder.encode("url", "UTF-8")+"="+URLEncoder.encode(URL, "UTF-8")
                +"&"+ URLEncoder.encode("user_id", "UTF-8")+"="+URLEncoder.encode(String.valueOf(userId), "UTF-8");

        String apicall = "createdefot";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall, post_data);
    }

    protected String doPost(String... params){
        String apicall = params[0];
        String post_data = params[1];

        switch (apicall) {
            case "createdefot":
                apicall = Api.ROOT_URL + apicall;
                break;
        }
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

        BackgroundWorker (Context ctx){
            context = ctx;
        }


        @Override
        protected String doInBackground(String... params){
            String result;
                result = doPost(params);
            return result;
        }


        @Override
        protected void onPreExecute(){
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Register Status");


        }


        @Override
        protected void onPostExecute(String result){
            alertDialog.setMessage(result);
            alertDialog.show();
            try {
                JSONObject obj = new JSONObject(result);


                switch (obj.getString("apicall")) {
                    case "createdefot":
                        if(obj.getString("error").equals("false")){
                            Intent intent = new Intent(AddDefotActivity.this, MainPageActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(AddDefotActivity.this, "Coś poszło nie tak, spróbuj ponownie", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            } catch (JSONException e) {
                e.getStackTrace();
            }
        }

    }
}
