package com.example.maniekcs1995.defotapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.BaseAdapter;
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

public class CommentsActivity extends AppCompatActivity {

    ArrayList<Object> commentList;
    ListView listView;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    AlertDialog alertDialog;
    Button button;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_comments_list);

        listView = (ListView) findViewById(R.id.listViewComments);

        sharedpreferences = getSharedPreferences("com.example.maniekcs1995.defotapp", MODE_PRIVATE);
        editor = sharedpreferences.edit();

        checkUserId(sharedpreferences.getString("login","DEFAULT"));

        commentList = new ArrayList<>();

        if(!sharedpreferences.getString("login", "guest").equals("guest")) {
            final TextInputEditText textComment = findViewById(R.id.textInputComment);


            Button addCommentButton = findViewById(R.id.buttonAddComment);

            addCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final String input = textComment.getText().toString();
                        if (input != null) {
                            addNewComment(input);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    getDefotComments(sharedpreferences.getInt("defotId",1));
                    textComment.setText("");
                }
            });

        }else{
            findViewById(R.id.textInputComment).setVisibility(View.GONE);
            findViewById(R.id.buttonAddComment).setVisibility(View.GONE);
            TextView textViewInfo = findViewById(R.id.textViewInfo);
            textViewInfo.setWidth(1400);
            textViewInfo.setText("                  Zaloguj się, aby dodać komentarz");
        }

        getDefotComments(sharedpreferences.getInt("defotId",1));
    }


    private void getDefotComments(int defotId){
        String apicall = "getdefotcomments";

        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(apicall, String.valueOf(defotId));
    }


    class CommentsAdapter extends BaseAdapter {

        ArrayList<Object> commentList;
        private static final int COMMENT_ITEM = 0;
        public static final int HEADER = 1;
        LayoutInflater inflater;

        public CommentsAdapter(ArrayList<Object> commentList, Context context) {
            this.commentList = commentList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getItemViewType(int position) {
            if (commentList.get(position) instanceof Comment) {
                return COMMENT_ITEM;
            } else {
                return HEADER;
            }
        }

        @Override
        public int getCount() {
            return commentList.size();
        }

        @Override
        public Object getItem(int i) {
            return commentList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


                switch (getItemViewType(position)) {
                    case COMMENT_ITEM:
                        convertView = inflater.inflate(R.layout.layout_comment_item, null);
                        break;
                    case HEADER:
                        convertView = inflater.inflate(R.layout.activity_comments_header, null);
                        break;
                }


            switch (getItemViewType(position)) {
                case COMMENT_ITEM:

                    TextView textViewCommentAuthor = convertView.findViewById(R.id.textViewCommentAuthor);
                    TextView textViewCommentDate = convertView.findViewById(R.id.textViewCommentDate);
                    TextView textViewComment = convertView.findViewById(R.id.textViewComment);



                    textViewCommentAuthor.setText(String.valueOf(((Comment)commentList.get(position)).getUser_id()));
                    textViewCommentDate.setText(((Comment)commentList.get(position)).getDate());
                    textViewComment.setText(((Comment)commentList.get(position)).getContent());
                    break;

                case HEADER:
                    TextView textViewAuthor = convertView.findViewById(R.id.textViewAuthor);
                    TextView textViewDate = convertView.findViewById(R.id.textViewDate);
                    TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
                    ImageView imageView = convertView.findViewById(R.id.imageView);
                    TextView textViewDesc = convertView.findViewById(R.id.textViewDesc);

                    TextView textViewLogin = convertView.findViewById(R.id.textViewLogin);


                            DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(imageView);
                            downloadTask.execute(((Defot)commentList.get(position)).getURL());

                            //textViewAuthor.setText(String.valueOf(((Defot)commentList.get(position)).getUser_id()));

                            textViewDate.setText(((Defot)commentList.get(position)).getDate());

                            textViewTitle.setText(((Defot)commentList.get(position)).getTitle());

                            textViewDesc.setText(((Defot)commentList.get(position)).getDesc());



                    break;

            }

/*
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
*/
            return convertView;
        }
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
        /*  */

        commentList.add(new Defot( 1,
                sharedpreferences.getString("defotTitle","DEFAULT"),
                sharedpreferences.getString("defotDesc","DEFAULT"),
                sharedpreferences.getString("defotURL","DEFAULT"),
                null,
                sharedpreferences.getString("defotDate","DEFAULT"),
                sharedpreferences.getInt("defotAuthorId",0)
        ));


        int length = comments.length();
        for (int i = 0; i < length; i++) {

            JSONObject obj = comments.getJSONObject(i);

            commentList.add(new Comment(
                    obj.getInt("id"),
                    obj.getInt("defot_id"),
                    obj.getInt("user_id"),
                    obj.getString("content"),
                    obj.getString("date")
            ));
        }

        CommentsAdapter adapter = new CommentsAdapter(commentList , this);
        listView.setAdapter(adapter);
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
                        case "getdefotcomments":
                            if (!obj.getBoolean("error")) {
                                JSONArray comments = obj.getJSONArray("comments");
                                refreshCommentList(comments);
                            }else{
                                editor.putString("callback", "Brak komentarzy");
                                editor.commit();

                                Intent intent = new Intent(CommentsActivity.this, MainPageActivity.class);
                                startActivity(intent);
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
