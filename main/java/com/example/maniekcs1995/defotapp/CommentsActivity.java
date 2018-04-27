package com.example.maniekcs1995.defotapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    List<Comment> commentList;
    ListView listView;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    AlertDialog alertDialog;
    TextInputEditText textInputEditText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        listView = (ListView) findViewById(R.id.listViewComments);

        sharedpreferences = getSharedPreferences("com.example.maniekcs1995.defotapp", MODE_PRIVATE);
        editor = sharedpreferences.edit();

        checkUserId(sharedpreferences.getString("login","DEFAULT"));

        commentList = new ArrayList<>();

        getOneDefot();
        getDefotComments();
    }

    private void getOneDefot(){
        String apicall = "getonedefot";

        CommentsActivity.BackgroundWorker backgroundWorker = new CommentsActivity.BackgroundWorker(this);
        backgroundWorker.execute(apicall, Integer.toString(sharedpreferences.getInt("defotId",0)));
    }

    private void getDefotComments(){
        String apicall = "getdefotcomments";

        CommentsActivity.BackgroundWorker backgroundWorker = new CommentsActivity.BackgroundWorker(this);
        backgroundWorker.execute(apicall, Integer.toString(sharedpreferences.getInt("defotId",0)));
    }

    private void refreshDefot(JSONObject defot) throws JSONException {

        Defot newDefot = new Defot(
                    defot.getInt("id"),
                    defot.getString("title"),
                    defot.getString("desc"),
                    defot.getString("url"),
                    null,
                    defot.getString("date"),
                    defot.getInt("user_id")
                );

        TextView textViewAuthor = findViewById(R.id.textViewAuthor);
        TextView textViewDate = findViewById(R.id.textViewDate);
        TextView textViewTitle = findViewById(R.id.textViewTitle);
        ImageView imageView = findViewById(R.id.imageView);
        TextView textViewDesc = findViewById(R.id.textViewDesc);
        final TextInputEditText textInputEditText = findViewById(R.id.textInputComment);
        Button addCommentButton = findViewById(R.id.buttonAddComment);

        TextView textViewLogin = findViewById(R.id.textViewLogin);

        CommentsActivity.DownloadImageWithURLTask downloadTask = new CommentsActivity.DownloadImageWithURLTask(imageView);

        downloadTask.execute(newDefot.getURL());

        textViewAuthor.setText(String.valueOf(newDefot.getUser_id()));
        textViewDate.setText(newDefot.getDate());
        textViewTitle.setText(newDefot.getTitle());
        textViewDesc.setText(newDefot.getDesc());
        textViewLogin.setText(sharedpreferences.getString("login","DEFAULT"));

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addNewComment(textInputEditText.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getDefotComments();


            }
        });


    }

    private void checkUserId(String login){
        String apicall = "checkuserid";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall, login);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void addNewComment(String comment) throws IOException{


        String post_data = URLEncoder.encode("defot_id", "UTF-8")+"="+URLEncoder.encode(String.valueOf(sharedpreferences.getInt("defotId",0)), "UTF-8")
                    +"&"+ URLEncoder.encode("user_id", "UTF-8")+"="+URLEncoder.encode(sharedpreferences.getString("user_id","0"), "UTF-8")
                    +"&"+ URLEncoder.encode("content", "UTF-8")+"="+URLEncoder.encode(comment, "UTF-8");

        String apicall = "createcomment";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall, post_data);
    }

    private void refreshCommentList(JSONArray comments) throws JSONException {

        commentList.clear();

        for (int i = 0; i < comments.length(); i++) {

            JSONObject obj = comments.getJSONObject(i);

            commentList.add(new Comment(
                    obj.getInt("id"),
                    obj.getInt("defot_id"),
                    obj.getInt("user_id"),
                    obj.getString("content"),
                    obj.getString("date")
            ));
        }

        CommentsAdapter adapter = new CommentsAdapter(commentList);
        listView.setAdapter(adapter);
    }

    class CommentsAdapter extends ArrayAdapter<Comment> {

        List<Comment> commentList;

        public CommentsAdapter(List<Comment> commentList) {
            super(CommentsActivity.this, R.layout.layout_comment_list, commentList);
            this.commentList = commentList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_comment_list, null, true);

            TextView textViewCommentAuthor = listViewItem.findViewById(R.id.textViewAuthor);
            TextView textViewCommentDate = listViewItem.findViewById(R.id.textViewDate);
            TextView textViewComment = listViewItem.findViewById(R.id.textViewComment);

            final Comment comment = commentList.get(position);

            textViewCommentAuthor.setText(String.valueOf(comment.getUser_id()));
            textViewCommentDate.setText(comment.getDate());
            textViewComment.setText(comment.getContent());

            listViewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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
            case "getdefotcomments":
                String defot_id = params[1];
                apicall = Api.ROOT_URL + apicall + "&defot_id=" + defot_id;
                break;
            case "checkuserid":
                String login = params[1];
                apicall = Api.ROOT_URL + apicall + "&login=" + login;
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
            case "createcomment":
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

        BackgroundWorker (Context ctx){
            context = ctx;
        }


        @Override
        protected String doInBackground(String... params){
            String result;

            if(params[0] == "createcomment"){
                result = doPost(params);
            }else {
                result = doGet(params);
            }
            return result;
        }


        @Override
        protected void onPreExecute(){
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Register Status");

        }


        @Override
        protected void onPostExecute(String result){

                try {
                    JSONObject obj = new JSONObject(result);

                    switch (obj.getString("apicall")) {
                        case "getonedefot":
                            JSONObject defot = obj.getJSONObject("defot");

                            if (!obj.getBoolean("error")) {
                                refreshDefot(defot);
                            }
                            break;

                        case "getdefotcomments":
                            JSONArray comments = obj.getJSONArray("comments");

                            if (!obj.getBoolean("error")) {
                                refreshCommentList(comments);

                            }
                            break;
                        case "checkuserid":
                            editor.putString("user_id", obj.getString("userId"));
                            editor.commit();
                            break;
                        case "createcomment":

                            break;
                    }
                } catch (JSONException e) {
                    e.getStackTrace();
                }
            }

    }
}
