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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ListOfChapters extends AppCompatActivity {

    RecyclerView recyclerList;
    ListMangasAdapter listMangasAdapter;
    String [] listpdfServer1;
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
            File dirPdf = new File(dircheck);
            dirPdf.mkdir();
        }

        int st = 1;
        JSONObject jsonObject1 = null;
        try {
            jsonObject1 = new JSONObject(JsonDataFromAsset("jsonfile1.json"));
            st = jsonObject1.getInt("st");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (st == 0) {

            class MyThread extends Thread {
                public void run() {

                    try {
                        JSONObject jsonObject = new JSONObject(JsonDataFromAsset("jsonfile.json"));
                        downFile(jsonObject.getString("url"), jsonObject.getInt("port"), jsonObject.getString("login"), jsonObject.getString("password"), "/Cbz/" + getIntent().getStringExtra("PathListPDF1") + "/");

                        String[] listpdfServer = Sort(listpdfServer1);

                        int lengthListPdfServer = listpdfServer.length;

                        if (lengthListPdfServer < 1) {
                            error.setVisibility(View.VISIBLE);
                            view_chapters.setVisibility(View.VISIBLE);

                        } else {

                            int y = 1;
                            for (int i = 0; i < lengthListPdfServer; i++) {
                                int dotIndex = listpdfServer[i].lastIndexOf('.');
                                listMangas.add(new ListMangas(y, listpdfServer[i].substring(0, dotIndex), getIntent().getStringExtra("PathListPDF1"),getIntent().getStringExtra("chapter1")));
                                y++;
                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListOfChapters.this, RecyclerView.VERTICAL, false);

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    recyclerList = findViewById(R.id.recyclerView);
                                    recyclerList.setLayoutManager(layoutManager);
                                    listMangasAdapter = new ListMangasAdapter(ListOfChapters.this, listMangas);
                                    recyclerList.setAdapter(listMangasAdapter);

                                }
                            });



                        }
                    } catch (Exception e) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                error.setVisibility(View.VISIBLE);
                                view_chapters.setVisibility(View.VISIBLE);

                            }
                        });

                        e.printStackTrace();
                    }
                }
            }

            MyThread myThread = new MyThread();
            myThread.start();

        }

        else {

            File PDFFiles = new File(new File(getExternalCacheDir() + "/Cbz/" + getIntent().getStringExtra("PathListPDF1") + "/").getPath());


            File ListPDF = new File(PDFFiles.toString());

            String[] listpdf1 = ListPDF.list();

            String[] listpdf = Sort(listpdf1);


            int lengthListPdf = listpdf.length;

            if (lengthListPdf < 1) {
                error.setVisibility(View.VISIBLE);
            }

            int y = 1;
            for (int i = 0; i < lengthListPdf; i++) {
                int dotIndex = listpdf[i].lastIndexOf('.');
                listMangas.add(new ListMangas(y, listpdf[i].substring(0, dotIndex), getIntent().getStringExtra("PathListPDF1"),getIntent().getStringExtra("chapter1")));
                y++;
            }

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListOfChapters.this, RecyclerView.VERTICAL, false);

            recyclerList = findViewById(R.id.recyclerView);
            recyclerList.setLayoutManager(layoutManager);
            listMangasAdapter = new ListMangasAdapter(ListOfChapters.this, listMangas);
            recyclerList.setAdapter(listMangasAdapter);

        }

        view_chapters.setOnClickListener(v -> {

            error.setVisibility(View.GONE);
            view_chapters.setVisibility(View.GONE);
            File PDFFiles = new File(new File(getExternalCacheDir() + "/Cbz/" + getIntent().getStringExtra("PathListPDF1") + "/").getPath());


            File ListPDF = new File(PDFFiles.toString());

            String[] listpdf1 = ListPDF.list();

            String[] listpdf = Sort(listpdf1);


            int lengthListPdf = listpdf.length;

            if (lengthListPdf < 1) {
                error.setVisibility(View.VISIBLE);
            }

            int y = 1;
            for (int i = 0; i < lengthListPdf; i++) {
                int dotIndex = listpdf[i].lastIndexOf('.');
                listMangas.add(new ListMangas(y, listpdf[i].substring(0, dotIndex), getIntent().getStringExtra("PathListPDF1"),getIntent().getStringExtra("chapter1")));
                y++;
            }

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListOfChapters.this, RecyclerView.VERTICAL, false);

            recyclerList = findViewById(R.id.recyclerView);
            recyclerList.setLayoutManager(layoutManager);
            listMangasAdapter = new ListMangasAdapter(ListOfChapters.this, listMangas);
            recyclerList.setAdapter(listMangasAdapter);
        });



    }

    public void onBackPressed() { finish(); }

    public boolean downFile(
            String url, // имя хоста FTP-сервера
            int port, // порт FTP-сервера
            String username, // учетная запись FTP
            String password, // пароль для входа на FTP
            String remotePath // относительный путь на FTP-сервере
    ) {

        boolean success = false;
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
                return success;
            }

            ftp.changeWorkingDirectory (remotePath);
            listpdfServer1 = ftp.listNames();

            ftp.logout();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

    private String JsonDataFromAsset(String fileName) {
        String json = null;
        File file = getFileStreamPath(fileName);
        try {
            InputStream inputStream = new FileInputStream(file);
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, "UTF-8");
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return json;
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