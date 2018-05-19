package com.example.maniekcs1995.defotapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity {

    List<Defot> defotList;
    List<Rating> ratingList;
    List<userRatings> userRatingsList;
    ListView listView;
    ImageView imageView;
    Bitmap bitmap;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    FloatingActionButton addDefotButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        sharedpreferences = getSharedPreferences("com.example.maniekcs1995.defotapp", MODE_PRIVATE);
        editor = sharedpreferences.edit();



        checkUserId(sharedpreferences.getString("login", "DEFAULT"));

        listView = (ListView) findViewById(R.id.listViewDefots);

        addDefotButton = findViewById(R.id.addDefotButton);
        logoutButton = findViewById(R.id.logoutButton);
        defotList = new ArrayList<>();
        ratingList = new ArrayList<>();
        userRatingsList = new ArrayList<>();



        getAllUserRatings(String.valueOf(sharedpreferences.getInt("userId", 0)));

        getAllDefotsRating();

        if(!sharedpreferences.getString("callback", "No callback").equals("No callback")){
           // Toast.makeText(MainPageActivity.this, sharedpreferences.getString("callback", "No callback"), Toast.LENGTH_LONG).show();
        }



        getAllDefots();



        //Toast.makeText(MainPageActivity.this, String.valueOf(sharedpreferences.getInt("userId", 0)), Toast.LENGTH_LONG).show();
        addDefotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPageActivity.this, AddDefotActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.remove("login");
                editor.commit();
                Intent intent = new Intent(MainPageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void getAllDefots(){
        String apicall = "getalldefots";

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall);
    }

    private void deleteDefot(int id){
        String apicall = "deletedefot";

        String userId = String.valueOf(id);

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall, userId);
    }

    private void checkUserId(String login){
        String apicall = "checkuserid";

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall, login);
    }

    private void getDefotRating(int defotId){
        String apicall = "getdefotrating";

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall, String.valueOf(defotId));
    }

    private void isDefotRated(int defotId, int userId){
        String apicall = "isdefotrated";

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall, String.valueOf(defotId), String.valueOf(userId));
    }

    private void getAllDefotsRating(){
        String apicall = "getalldefotsrating";

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall);
    }

    private void getAllUserRatings(String userId){
        String apicall = "getalluserratings";

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall, userId);
    }

    private void rateDefot(int defotId, int userId, int rate) throws UnsupportedEncodingException {
        String apicall = "ratedefot";

        String post_data = URLEncoder.encode("defot_id", "UTF-8")+"="+URLEncoder.encode(String.valueOf(defotId), "UTF-8")
                +"&"+ URLEncoder.encode("user_id", "UTF-8")+"="+URLEncoder.encode(String.valueOf(userId), "UTF-8")
                +"&"+ URLEncoder.encode("value", "UTF-8")+"="+URLEncoder.encode(String.valueOf(rate), "UTF-8");

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall, post_data);
    }

    private void checkUserRatings(JSONArray userRatings) throws JSONException{
        userRatingsList.clear();

        int l = userRatings.length();
        JSONObject obj1 = userRatings.getJSONObject(0);
        String cos = String.valueOf(obj1.getInt("id"));


        //Toast.makeText(MainPageActivity.this, userRatingsList.get(0).getDefotId(), Toast.LENGTH_LONG).show();

        for(int i = 0; i < l; i++){

            JSONObject obj = userRatings.getJSONObject(i);

            userRatingsList.add(new userRatings(
                    obj.getInt("id"),
                    obj.getInt("userId"),
                    obj.getInt("defotId"),
                    obj.getInt("value")
            ));

        }

    }

    private void loadDefotRatings(JSONArray rating) throws JSONException {
        ratingList.clear();
        int j = 0;

        for(int i = 0; i < rating.length(); i++){

            JSONObject obj = rating.getJSONObject(i);



            if(obj.getInt("value") != 0) {
                if (i > 0) {
                    JSONObject obj2 = rating.getJSONObject(i - 1);

                    if (obj.getInt("defotId") == obj2.getInt("defotId")) {
                        ratingList.get(j).updateRating(obj.getInt("value"));

                    }else{
                        j++;

                         ratingList.add(new Rating(
                                 obj.getInt("defotId"),
                                 obj.getInt("value")
                         ));
                    }

                } else {
                    ratingList.add(new Rating(
                            obj.getInt("defotId"),
                            obj.getInt("value")
                    ));
                }
            }
        }

    }

    private void refreshDefotList(JSONArray defots) throws JSONException {
        defotList.clear();



        for (int i = 0; i < defots.length(); i++) {

            JSONObject obj = defots.getJSONObject(i);
            int rating = 0;

            for(int j = 0; j < userRatingsList.size(); j++){

                if(userRatingsList.get(j).getDefotId() == obj.getInt("id"))
                    rating = userRatingsList.get(j).getRating();
            }


            defotList.add(new Defot(
                    obj.getInt("id"),
                    obj.getString("title"),
                    obj.getString("desc"),
                    obj.getString("url"),
                    rating,
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            final View listViewItem = inflater.inflate(R.layout.layout_defot_list, null, true);

            TextView textViewAuthor = listViewItem.findViewById(R.id.textViewCommentAuthor);
            TextView textViewDate = listViewItem.findViewById(R.id.textViewCommentDate);
            TextView textViewTitle = listViewItem.findViewById(R.id.textViewTitle);
            ImageView imageView = listViewItem.findViewById(R.id.imageView);
            TextView textViewDesc = listViewItem.findViewById(R.id.textViewDesc);
            final TextView textViewRating = listViewItem.findViewById(R.id.textViewRating);
            final Button rateUpButton = listViewItem.findViewById(R.id.rateUpButton);
            final Button rateDownButton = listViewItem.findViewById(R.id.rateDownButton);

            TextView textViewLogin = listViewItem.findViewById(R.id.textViewLogin);

            final Defot defot = defotList.get(position);


            DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(imageView);

            downloadTask.execute(defot.getURL());

            //textViewAuthor.setText(String.valueOf(defot.getId()));
            textViewDate.setText(defot.getDate());
            textViewTitle.setText(defot.getTitle());
            textViewDesc.setText(defot.getDesc());
            try {
                textViewRating.setText(String.valueOf(ratingList.get(position).getRating()));
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Invalid option");
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position,
                                        long arg3)
                {

                    Defot defotItem = (Defot)adapter.getItemAtPosition(position);

                    editor.putInt("defotId", defotItem.getId());
                    editor.putInt("defotAuthorId", defotItem.getUser_id());
                    //editor.putInt("defotRating", defotItem.getRating());
                    editor.putString("defotTitle",defotItem.getTitle());
                    editor.putString("defotDate",defotItem.getDate());
                    editor.putString("defotURL", defotItem.getURL());
                    editor.putString("defotDesc",defotItem.getDesc());

                    editor.commit();


                    Intent intent = new Intent(MainPageActivity.this,CommentsActivity.class);
                    startActivity(intent);
                }
            });


            if(defot.getUser_id() == sharedpreferences.getInt("userId", 0)) {
                final Button deleteDefotButton = listViewItem.findViewById(R.id.deleteDefotButton);

                deleteDefotButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDefot(defot.getId());
                        getAllDefots();
                    }
                });
            }else{
                listViewItem.findViewById(R.id.deleteDefotButton).setVisibility(View.GONE);
            }

            if(!sharedpreferences.getString("login", "guest").equals("guest")){

                    if(defot.getRating() == 0) {
                        rateUpButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    rateDefot(defot.getId(), sharedpreferences.getInt("userId", 0), 1);
                                    rateUpButton.setEnabled(false);
                                    listViewItem.findViewById(R.id.rateDownButton).setVisibility(View.GONE);
                                    try {
                                        if ((textViewRating.getText()).equals("(rating)")) {
                                            textViewRating.setText(String.valueOf(1));
                                        } else {
                                            textViewRating.setText(String.valueOf(ratingList.get(position).getRating() + 1));
                                        }
                                    } catch (IndexOutOfBoundsException e) {
                                        System.out.println("Invalid option");
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        rateDownButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    rateDefot(defot.getId(), sharedpreferences.getInt("userId", 0), -1);
                                    rateDownButton.setEnabled(false);
                                    listViewItem.findViewById(R.id.rateUpButton).setVisibility(View.GONE);
                                    try {
                                        if ((textViewRating.getText()).equals("(rating)")) {
                                            textViewRating.setText(String.valueOf(-1));
                                        } else {
                                            textViewRating.setText(String.valueOf(ratingList.get(position).getRating() - 1));
                                        }
                                    } catch (IndexOutOfBoundsException e) {
                                        System.out.println("Invalid option");
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else if (defot.getRating() == 1){
                        listViewItem.findViewById(R.id.rateDownButton).setVisibility(View.GONE);
                        rateUpButton.setEnabled(false);
                    }else if (defot.getRating() == -1){
                        listViewItem.findViewById(R.id.rateUpButton).setVisibility(View.GONE);
                        rateDownButton.setEnabled(false);
                    }
            }else{
                listViewItem.findViewById(R.id.rateUpButton).setVisibility(View.GONE);
                listViewItem.findViewById(R.id.rateDownButton).setVisibility(View.GONE);
            }



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
            case "checkuserid":
                String login = params[1];
                apicall = Api.ROOT_URL + apicall + "&login=" + login;
                break;
            case "deletedefot":
                String defotId = params[1];
                apicall = Api.ROOT_URL + apicall + "&id=" + defotId;
                break;
            case "getdefotrating":
                defotId = params[1];
                apicall = Api.ROOT_URL + apicall + "&defot_id=" + defotId;
                break;
            case "getalldefotsrating":
                apicall = Api.ROOT_URL + apicall;
                break;
            case "isdefotrated":
                defotId = params[1];
                String userId = params[2];
                apicall = Api.ROOT_URL + apicall + "&defot_id=" + defotId + "&user_id=" + userId;
                break;
            case "getalluserratings":
                userId = params[1];
                apicall = Api.ROOT_URL + apicall + "&user_id=" + userId;
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

    protected String doPost(String... params){
        String apicall = params[0];
        String post_data = params[1];

        switch (apicall) {
            case "ratedefot":
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
            if(params[0].equals("ratedefot")){
                String result = doPost(params);
                return result;
            }else {
                String result = doGet(params);
                return result;
            }

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

                if (!obj.getBoolean("error")) {
                    switch (obj.getString("apicall")) {
                        case "getalldefotsrating":
                            JSONArray ratings = obj.getJSONArray("rating");

                            loadDefotRatings(ratings);

                            break;
                        case "getalldefots":
                            JSONArray defots = obj.getJSONArray("defots");

                            refreshDefotList(defots);
                            break;
                        case "checkuserid":
                            editor.putInt("userId",obj.getInt("userId"));
                            editor.commit();
                            break;
                        case "deletedefot":
                            Toast.makeText(MainPageActivity.this, "Pomyślnie usunięto", Toast.LENGTH_LONG).show();
                            break;
                        case "getdefotrating":
                            editor.putInt("defotRating",obj.getInt("rating"));
                            editor.commit();
                            break;
                        case "ratedefot":
                            Toast.makeText(MainPageActivity.this, "Dzięki za ocenę! :D", Toast.LENGTH_LONG).show();
                            break;
                        case "getalluserratings":

                            JSONArray userRatings = obj.getJSONArray("userRatings");

                            checkUserRatings(userRatings);
                            break;
                    }

                }


            }catch(JSONException e){
                e.getStackTrace();
            }
        }

    }
}
