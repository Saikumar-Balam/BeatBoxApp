package com.sai.beatbox;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import android.Manifest;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(com.karumi.dexter.MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
//                                Toast.makeText(MainActivity.this, "Permissions granted", Toast.LENGTH_SHORT).show();
                                ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                                String[] items = new String[mySongs.size()];
                                for(int i=0;i<mySongs.size();i++){
                                    items[i] = mySongs.get(i).getName().replace(".mp3", "");
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(MainActivity.this, PlaySong.class);
                                        String currentSong = listView.getItemAtPosition(position).toString();
                                        intent.putExtra("songList", mySongs);
                                        intent.putExtra("currentSong", currentSong);
                                        intent.putExtra("position", position);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .check();
        } else {
            // For Android 12 and below
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new com.karumi.dexter.listener.single.PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
//                            Toast.makeText(MainActivity.this, "Runtime permission given", Toast.LENGTH_SHORT).show();
                            ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                            String[] items = new String[mySongs.size()];
                            for(int i=0;i<mySongs.size();i++){
                                items[i] = mySongs.get(i).getName().replace(".mp3", "");
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(MainActivity.this, PlaySong.class);
                                    String currentSong = listView.getItemAtPosition(position).toString();
                                    intent.putExtra("songList", mySongs);
                                    intent.putExtra("currentSong", currentSong);
                                    intent.putExtra("position", position);
                                    startActivity(intent);
                                }
                            });
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .check();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList = new ArrayList();
        File[] songs = file.listFiles();
        if(songs!=null) {
            for (File myfile : songs) {
                if (!myfile.isHidden() && myfile.isDirectory()) {
                    arrayList.addAll(fetchSongs(myfile));
                } else {
                    if (myfile.getName().endsWith(".mp3") && !myfile.getName().startsWith(".")) {
                        arrayList.add(myfile);
                    }
                }
            }
        }
        return arrayList;
    }
}