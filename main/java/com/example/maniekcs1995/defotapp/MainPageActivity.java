package com.example.maniekcs1995.defotapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity {

    List<Defot> defotList;
    ListView listView;
    ImageView imageView;
    Bitmap bitmap;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        sharedpreferences = getSharedPreferences("com.example.maniekcs1995.defotapp", MODE_PRIVATE);
        editor = sharedpreferences.edit();

        listView = (ListView) findViewById(R.id.listViewDefots);

        defotList = new ArrayList<>();

        getAllDefots();

    }

    private void getAllDefots(){
        String apicall = "getalldefots";

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall);
    }


    private void refreshDefotList(JSONArray defots) throws JSONException {
        defotList.clear();

        for (int i = 0; i < defots.length(); i++) {
            JSONObject obj = defots.getJSONObject(i);


            defotList.add(new Defot(
                    obj.getInt("id"),
                    obj.getString("title"),
                    obj.getString("desc"),
                    obj.getString("url"),
                   null,
                    obj.getString("date"),
                    obj.getInt("user_id")
            ));
        }

        DefotAdapter adapter = new DefotAdapter(defotList);
        listView.setAdapter(adapter);
    }


    class DefotAdapter extends ArrayAdapter<Defot> {

        List<Defot> defotList;

        public DefotAdapter(List<Defot> defotList) {
            super(MainPageActivity.this, R.layout.layout_defot_list, defotList);
            this.defotList = defotList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_defot_list, null, true);

            TextView textViewAuthor = listViewItem.findViewById(R.id.textViewCommentAuthor);
            TextView textViewDate = listViewItem.findViewById(R.id.textViewCommentDate);
            TextView textViewTitle = listViewItem.findViewById(R.id.textViewTitle);
            ImageView imageView = listViewItem.findViewById(R.id.imageView);
            TextView textViewDesc = listViewItem.findViewById(R.id.textViewDesc);

            TextView textViewLogin = listViewItem.findViewById(R.id.textViewLogin);

            DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(imageView);

            final Defot defot = defotList.get(position);

            downloadTask.execute(defot.getURL());

            textViewAuthor.setText(String.valueOf(defot.getId()));
            textViewDate.setText(defot.getDate());
            textViewTitle.setText(defot.getTitle());
            textViewDesc.setText(defot.getDesc());
            textViewLogin.setText(sharedpreferences.getString("login","DEFAULT"));


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position,
                                        long arg3)
                {

                    Defot defotItem = (Defot)adapter.getItemAtPosition(position);

                    editor.putInt("defotId", defotItem.getId());
                    editor.putInt("defotAuthorId", defotItem.getUser_id());
                    editor.putString("defotTitle",defotItem.getTitle());
                    editor.putString("defotDate",defotItem.getDate());
                    editor.putString("defotURL", defotItem.getURL());
                    editor.putString("defotDesc",defotItem.getDesc());

                    editor.commit();


                    Intent intent = new Intent(MainPageActivity.this,CommentsActivity.class);
                    startActivity(intent);
                }
            });
            return listViewItem;
        }
    }



    protected String doGet(String... params) {
        String apicall = params[0];

        switch (apicall){
            case "getalldefots":
                apicall = Api.ROOT_URL + apicall;
                break;

            case "getonedefot":
                String id = params[1];
                apicall = Api.ROOT_URL + apicall + "&id=" + id;
                break;
        }


        try {
            URL url = new URL(apicall);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

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

    private class DownloadImageWithURLTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageWithURLTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String pathToFile = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(pathToFile).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    class BackgroundWorker extends AsyncTask<String,Void,String> {

        Context context;
        AlertDialog alertDialog;
        BackgroundWorker (Context ctx){
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params){

            String result = doGet(params);
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

            try {
                JSONObject obj = new JSONObject(result);

                JSONArray defots = obj.getJSONArray("defots");

                if (!obj.getBoolean("error")) {
                    refreshDefotList(defots);
                }

            }catch(JSONException e){
                e.getStackTrace();
            }
        }

    }
}
