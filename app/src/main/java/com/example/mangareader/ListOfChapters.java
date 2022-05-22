package com.example.mangareader;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListOfChapters extends AppCompatActivity {

    RecyclerView recyclerList;
    ListMangasAdapter listMangasAdapter;
    String [] listCbzServer1;
    List<ListMangas> listMangas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_chapters);

        TextView error = findViewById(R.id.error);
        TextView view_chapters = findViewById(R.id.view_chapters);
        error.setVisibility(View.GONE);
        view_chapters.setVisibility(View.GONE);

        String dircheck = getExternalCacheDir()+"/Cbz/"+getIntent().getStringExtra("PathListPDF1");

        File dir = new File(dircheck);

        if (!dir.exists()) {
            dir.mkdir();
        }

        int st = 1;
        JSONObject jsonObject1;
        try {
            jsonObject1 = new JSONObject(Objects.requireNonNull(CheckAndDownload.JsonDataFromAsset(getFileStreamPath("jsonfile1.json"))));
            st = jsonObject1.getInt("st");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (st == 0) {

            class MyThread extends Thread {
                public void run() {

                    try {
                        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(CheckAndDownload.JsonDataFromAsset(getFileStreamPath("jsonfile.json"))));
                        downFile(jsonObject.getString("url"), jsonObject.getInt("port"), jsonObject.getString("login"), jsonObject.getString("password"), "/Cbz/" + getIntent().getStringExtra("PathListPDF1") + "/");

                        String[] listCbzServer = Sort(listCbzServer1);
                        int lengthListCbzServer = listCbzServer.length;

                        if (lengthListCbzServer < 1) {
                            error.setVisibility(View.VISIBLE);
                            view_chapters.setVisibility(View.VISIBLE);

                        } else {
                            ListOutput(lengthListCbzServer,listCbzServer);
                        }
                    } catch (Exception e) {

                        runOnUiThread(() -> {

                            error.setVisibility(View.VISIBLE);
                            view_chapters.setVisibility(View.VISIBLE);

                        });

                        e.printStackTrace();
                    }
                }
            }

            MyThread myThread = new MyThread();
            myThread.start();

        }

        else {
            DisplayListWithoutInternet(dir,error);
        }

        view_chapters.setOnClickListener(v -> {

            error.setVisibility(View.GONE);
            view_chapters.setVisibility(View.GONE);

            DisplayListWithoutInternet(dir,error);
        });
    }

    public void onBackPressed() { finish(); }

    private void DisplayListWithoutInternet(File fileCBZ, TextView error) {

        String[] listCbz = fileCBZ.list();

        assert listCbz != null;
        String[] listCbz1 = Sort(listCbz);

        int lengthListCbz = listCbz1.length;

        if (lengthListCbz < 1) {
            error.setVisibility(View.VISIBLE);
        }

        ListOutput(lengthListCbz, listCbz1);
    }

    private void ListOutput(int lengthListCbzServer, String[] listCbzServer) {
        for (int i = 0; i < lengthListCbzServer; i++) {
            int dotIndex = listCbzServer[i].lastIndexOf('.');
            listMangas.add(new ListMangas(i+1, listCbzServer[i].substring(0, dotIndex), getIntent().getStringExtra("PathListPDF1"),getIntent().getStringExtra("chapter1")));
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListOfChapters.this, RecyclerView.VERTICAL, false);

        runOnUiThread(() -> {

            recyclerList = findViewById(R.id.recyclerView);
            recyclerList.setLayoutManager(layoutManager);
            listMangasAdapter = new ListMangasAdapter(ListOfChapters.this, listMangas);
            recyclerList.setAdapter(listMangasAdapter);

        });
    }

    private void downFile(
            String url, // имя хоста FTP-сервера
            int port, // порт FTP-сервера
            String username, // учетная запись FTP
            String password, // пароль для входа на FTP
            String remotePath // относительный путь на FTP-сервере
    ) {
        FTPClient ftp = new FTPClient();
        ftp.setConnectTimeout(100);
        ftp.setControlEncoding("UTF-8");
        try {
            int reply;
            ftp.connect(url, port);
            ftp.login (username, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
            }

            ftp.changeWorkingDirectory (remotePath);
            listCbzServer1 = ftp.listNames();

            ftp.logout();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public String[] Sort(String[] list){
        int y = list.length;
        String[] listtest;
        double[] listtesttest = new double[y];
        for (int i = 0; i<y; i++) {
            listtest = list[i].split(" ");
            listtesttest[i] = Double.parseDouble((listtest[1]));
        }

        int gap = listtesttest.length / 2;

        while (gap >= 1) {
            for (int right = 0; right < listtesttest.length; right++) {

                for (int c = right - gap; c >= 0; c -= gap) {
                    if (listtesttest[c] > listtesttest[c + gap]) {
                        double tmp = listtesttest[c];
                        String tmp1 = list[c];
                        listtesttest[c] = listtesttest[c + gap];
                        list[c] = list[c + gap];
                        listtesttest[c + gap] = tmp;
                        list[c + gap] = tmp1;
                    }
                }
            }

            gap = gap / 2;
        }
        return list;
    }
}