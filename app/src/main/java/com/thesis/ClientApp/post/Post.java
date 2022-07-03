package com.thesis.ClientApp.post;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.thesis.ClientApp.R;
import com.thesis.ClientApp.SharedPref;
import com.thesis.ClientApp.notifications.Data;
import com.thesis.ClientApp.notifications.Sender;
import com.thesis.ClientApp.notifications.Token;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class Post extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    String current_category;
    ImageView meme, cancel;
    VideoView vines;
    ConstraintLayout add_meme, add_vines, remove_lt;
    Button post, post_vine;
    EditText text;
    TextView mName, type, currentStatus;
    CircleImageView circleImageView3;
    ProgressBar pd;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String name, dp, id;
    Spinner spinnerCategory;
    private Uri image_uri, video_uri;
    MediaController mediaController;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    SharedPref sharedPref;
    private FirebaseAnalytics mFirebaseAnalytics;
    String btnName;
    String setStatus;
    private Object TAG;
    String myUid;
    String hisUid;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        currentStatus = findViewById(R.id.status);
        meme = findViewById(R.id.meme);
        add_meme = findViewById(R.id.constraintLayout3);
        post = findViewById(R.id.post_it);
        post_vine = findViewById(R.id.post_vine);
        text = findViewById(R.id.post_text);
        type = findViewById(R.id.username);
        mName = findViewById(R.id.name);
        circleImageView3 = findViewById(R.id.circleImageView3);
        pd = findViewById(R.id.pb);
        cancel = findViewById(R.id.imageView);
        cancel.setOnClickListener(v -> onBackPressed());
        add_vines = findViewById(R.id.vines_lt);
        remove_lt = findViewById(R.id.remove_lt);
        vines = findViewById(R.id.vines);
        spinnerCategory = findViewById(R.id.category_list);
        mediaController = new MediaController(this);
        vines.setMediaController(mediaController);
        mediaController.setAnchorView(vines);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);



        MediaController ctrl = new MediaController(Post.this);

        //Array Category List
        ArrayList<String> categfromdb = new ArrayList<>();

        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categfromdb);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner sp1 = findViewById(R.id.category_list);

        DatabaseReference categref =  FirebaseDatabase.getInstance().getReference().child("Categories");

        categref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categfromdb.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    categfromdb.add(ds.getValue().toString());
                }
                sp1.setAdapter(adp1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.id.category_list, R.layout.support_simple_spinner_dropdown_item);
//        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//
//        spinnerCategory.setAdapter(adapter);

        ctrl.setVisibility(View.GONE);
        vines.setMediaController(ctrl);
        vines.start();
        vines.setOnPreparedListener(mp -> mp.setLooping(true));

        remove_lt.setOnClickListener(v -> {

            meme.setImageURI(null);
            image_uri = null;
            post.setVisibility(View.VISIBLE);
            vines.setVisibility(View.GONE);
            post_vine.setVisibility(View.GONE);
            remove_lt.setVisibility(View.GONE);
            type.setText("Text");
        });

        post_vine.setOnClickListener(v -> {
            pd.setVisibility(View.VISIBLE);
            String mText = text.getText().toString().trim();

            if (TextUtils.isEmpty(mText)) {
                Alerter.create(Post.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .setDuration(10000)
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .enableSwipeToDismiss()
                        .setText("Enter caption")
                        .show();
            } else {
                uploadVine(mText, String.valueOf(video_uri));
            }

        });

        add_vines.setOnClickListener(v -> {
            //Check Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    chooseVideo();
                }
            } else {
                chooseVideo();
            }

        });

        firebaseAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                mName.setText(name);
                dp = Objects.requireNonNull(dataSnapshot.child("photo").getValue()).toString();
                try {
                    Picasso.get().load(dp).into(circleImageView3);
                } catch (Exception e) {
                    Picasso.get().load(R.drawable.avatar).into(circleImageView3);
                }
                id = Objects.requireNonNull(dataSnapshot.child("id").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        add_meme.setOnClickListener(v -> {
            type.setText("Meme");
            //Check Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    pickImageFromGallery();
                }
            } else {
                pickImageFromGallery();
            }
        });

//Post
        post.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Are you sure you want to post this?");
            dialog.setMessage("To post you need to Accept this");
            dialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String mText = text.getText().toString().trim();
                    current_category = spinnerCategory.getSelectedItem().toString();

                    if (TextUtils.isEmpty(mText)) {
                        Alerter.create(Post.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_error)
                                .setBackgroundColorRes(R.color.colorPrimary)
                                .setDuration(10000)
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .enableSwipeToDismiss()
                                .setText("Enter caption")
                                .show();
                        return;
                    }
                    if (image_uri == null) {
                        pd.setVisibility(View.VISIBLE);
                        uploadData(mText, "noImage");
                    } else {
                        uploadData(mText, String.valueOf(image_uri));
                        pd.setVisibility(View.VISIBLE);
                    }
                    Bundle params = new Bundle();
                    params.putString("text", mText);
                    params.putString("type", current_category);
                    mFirebaseAnalytics.logEvent("Category_List", params);
                    Log.d((String) TAG, "onClick: post");
                }
            });

            dialog.setNegativeButton("No", null);
            dialog.show();
        });

// Category Java
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                sendText(intent);
            } else if (type.startsWith("image")) {
                sendImage(intent);
            } else if (type.startsWith("video")) {
                sendVideo(intent);
            }
        }

    }


    //SendPostNotification
    private void sendNotification(final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUid, name + " : " + message, "New Message", hisId, R.drawable.logo);
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON_RESPONSE", "onResponse" + response.toString());

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse" + error.toString());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAAOoc8VaE:APA91bG4mhdXbN3_qKswzvgbHS_-hk2aRhQrKQtR4-lLah5cL_pz_K6s8EHWSuHCpz8OGCE1hvwf2JEZu0N7aCEx7XQaCiRRbU63jetNlOyLEBJLRksEYIymLT21refoghs-vqq_Cz_x");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void sendVideo(Intent intent) {
        Uri videoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (videoUri != null) {
            video_uri = videoUri;
            vines.setVideoURI(video_uri);
            vines.setVisibility(View.VISIBLE);
            remove_lt.setVisibility(View.VISIBLE);
            post_vine.setVisibility(View.VISIBLE);
            post.setVisibility(View.GONE);
        }
    }

    private void sendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            image_uri = imageUri;
            meme.setImageURI(image_uri);
            meme.setVisibility(View.VISIBLE);
            remove_lt.setVisibility(View.VISIBLE);

        }
    }

    private void sendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            text.setText(sharedText);
        }
    }

    private void uploadData(String mText, String uri) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "Post_" + timeStamp;
        if (!uri.equals("noImage")) {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                        if (uriTask.isSuccessful()) {
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("id", id);
                            hashMap.put("name", name);
                            hashMap.put("dp", dp);
                            hashMap.put("pId", timeStamp);
                            hashMap.put("text", mText);
                            hashMap.put("type", current_category);
                            hashMap.put("pViews", "0");
                            hashMap.put("pComments", "0");
                            hashMap.put("meme", downloadUri);
                            hashMap.put("vine", "noVideo");
                            hashMap.put("pTime", timeStamp);
                            DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                            dRef.child(timeStamp).setValue(hashMap)
                                    .addOnSuccessListener(aVoid -> {
                                        text.setText("");
                                        meme.setImageURI(null);
                                        image_uri = null;
                                        remove_lt.setVisibility(View.GONE);
                                        pd.setVisibility(View.GONE);
                                        type.setText("Text");
                                        Alerter.create(Post.this)
                                                .setTitle("Successful")
                                                .setIcon(R.drawable.ic_check_wt)
                                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                                .setDuration(10000)
                                                .enableSwipeToDismiss()
                                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                                .setText("Post Uploaded")
                                                .show();
                                    })
                                    .addOnFailureListener(e -> {
                                        pd.setVisibility(View.GONE);
                                        Alerter.create(Post.this)
                                                .setTitle("Successful")
                                                .setIcon(R.drawable.ic_check_wt)
                                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                                .setDuration(10000)
                                                .enableSwipeToDismiss()
                                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                                .setText(e.getMessage())
                                                .show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        pd.setVisibility(View.GONE);
                        Alerter.create(Post.this)
                                .setTitle("Successful")
                                .setIcon(R.drawable.ic_check_wt)
                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                .setDuration(10000)
                                .enableSwipeToDismiss()
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .setText(e.getMessage())
                                .show();
                    });
        } else {
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("id", id);
            hashMap.put("name", name);
            hashMap.put("dp", dp);
            hashMap.put("pId", timeStamp);
            hashMap.put("text", mText);
            hashMap.put("pViews", "0");
            hashMap.put("meme", "noImage");
            hashMap.put("type", current_category);
            hashMap.put("vine", "noVideo");
            hashMap.put("pComments", "0");
            hashMap.put("pTime", timeStamp);
            DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
            dRef.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(aVoid -> {
                        text.setText("");
                        meme.setImageURI(null);
                        image_uri = null;
                        type.setText("Text");
                        pd.setVisibility(View.GONE);

                        Alerter.create(Post.this)
                                .setTitle("Successful")
                                .setIcon(R.drawable.ic_check_wt)
                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                .setDuration(10000)
                                .enableSwipeToDismiss()
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .setText("Post Uploaded")
                                .show();
                    })
                    .addOnFailureListener(e -> {
                        pd.setVisibility(View.GONE);
                        Alerter.create(Post.this)
                                .setTitle("Successful")
                                .setIcon(R.drawable.ic_check_wt)
                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                .setDuration(10000)
                                .enableSwipeToDismiss()
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .setText(e.getMessage())
                                .show();
                    });
        }
    }

    private void uploadVine(String mText, String uri) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "Post_" + timeStamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(uri)).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()) {
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("name", name);
                hashMap.put("dp", dp);
                hashMap.put("pId", timeStamp);
                hashMap.put("text", mText);
                hashMap.put("type", current_category);
                hashMap.put("pViews", "0");
                hashMap.put("pComments", "0");
                hashMap.put("meme", "noImage");
                hashMap.put("vine", downloadUri);
                hashMap.put("pTime", timeStamp);
                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Posts");
                dRef.child(timeStamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> {

                            Alerter.create(Post.this)
                                    .setTitle("Successful")
                                    .setIcon(R.drawable.ic_check_wt)
                                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                                    .setDuration(10000)
                                    .enableSwipeToDismiss()
                                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                    .setText("Post Uploaded")
                                    .show();

                            text.setText("");
                            vines.setVideoURI(null);
                            video_uri = null;
                            vines.setVisibility(View.GONE);
                            post_vine.setVisibility(View.GONE);
                            post.setVisibility(View.VISIBLE);
                            type.setText("Text");
                            pd.setVisibility(View.GONE);
                            remove_lt.setVisibility(View.GONE);
                        })
                        .addOnFailureListener(e -> Alerter.create(Post.this)
                                .setTitle("Error")
                                .setIcon(R.drawable.ic_check_wt)
                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                .setDuration(10000)
                                .enableSwipeToDismiss()
                                .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                                .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                                .setText(e.getMessage())
                                .show());
            }
        }).addOnFailureListener(e -> {
            pd.setVisibility(View.GONE);
            Alerter.create(Post.this)
                    .setTitle("Error")
                    .setIcon(R.drawable.ic_check_wt)
                    .setBackgroundColorRes(R.color.colorPrimaryDark)
                    .setDuration(10000)
                    .enableSwipeToDismiss()
                    .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                    .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                    .setText(e.getMessage())
                    .show();
        });
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Alerter.create(Post.this)
                        .setTitle("Successful")
                        .setIcon(R.drawable.ic_check_wt)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText("Storage permission Allowed")
                        .show();
            } else {
                Alerter.create(Post.this)
                        .setTitle("Error")
                        .setIcon(R.drawable.ic_error)
                        .setBackgroundColorRes(R.color.colorPrimaryDark)
                        .setDuration(10000)
                        .enableSwipeToDismiss()
                        .setTitleTypeface(Typeface.createFromAsset(getAssets(), "bold.ttf"))
                        .setTextTypeface(Typeface.createFromAsset(getAssets(), "med.ttf"))
                        .setText("Storage permission is required")
                        .show();
            }
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null && data.getData() != null) {
            image_uri = data.getData();
            meme.setImageURI(image_uri);
            post_vine.setVisibility(View.GONE);
            post.setVisibility(View.VISIBLE);
            meme.setVisibility(View.VISIBLE);
            vines.setVisibility(View.GONE);
            post_vine.setVisibility(View.GONE);
            remove_lt.setVisibility(View.VISIBLE);
            type.setText("Image");
        }
        if (image_uri == null) {
            meme.setVisibility(View.GONE);
            post.setVisibility(View.VISIBLE);
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            video_uri = data.getData();
            vines.setVideoURI(video_uri);
            post.setVisibility(View.GONE);
            remove_lt.setVisibility(View.VISIBLE);
            vines.setVisibility(View.VISIBLE);
            post_vine.setVisibility(View.VISIBLE);
            meme.setVisibility(View.GONE);
            type.setText("Video");
        }
        if (video_uri == null) {
            vines.setVisibility(View.GONE);
        }
    }

    private String getfileExt(Uri video_uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(video_uri));
    }

}