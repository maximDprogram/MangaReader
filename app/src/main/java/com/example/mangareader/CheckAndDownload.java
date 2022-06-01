package com.example.mangareader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CheckAndDownload extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_and_download);

        ProgressBar progressBar = findViewById(R.id.progressBarConnect);

        String dircheck = getExternalCacheDir()+"/Json";
        String dircheckPic400 = getExternalCacheDir()+"/Pictures400/";
        String dircheckPic800 = getExternalCacheDir()+"/Pictures800/";

        EditText url = findViewById(R.id.editTextUrl);
        EditText port = findViewById(R.id.editTextPort);
        EditText login = findViewById(R.id.editTextLogin);
        EditText password = findViewById(R.id.editTextPassword);

        ImageView image = findViewById(R.id.imageViewOffline);

        String filePath = getExternalCacheDir() +"/Json"+"/completed.json";

        String dircheckCbz = getExternalCacheDir()+"/Cbz";

        File dir = new File(dircheck);
        File file = new File(filePath);
        File dirCbz = new File(dircheckCbz);
        File dirPic400 = new File(dircheckPic400);
        File dirPic800 = new File(dircheckPic800);

        image.setOnClickListener(v -> {
            saveToJsonStatus(1);

            if (!dir.exists()) {
                dir.mkdir();
            }

            if (!file.exists()) {
                startErrorActivity();
            }
            else {
                startMainActivity();
            }
        });


        progressBar.setOnClickListener(v -> {

            int porttext;

            String urltext = url.getText().toString();
            String logintext = login.getText().toString();
            String passwordtext = password.getText().toString();
            String portcheck = port.getText().toString();

            if (!dirCbz.exists()) {
                dirCbz.mkdir();
            }

            if (!dirPic400.exists()) {
                dirPic400.mkdir();
            }

            if (!dirPic800.exists()) {
                dirPic800.mkdir();
            }

            if (!dir.exists()) {
                dir.mkdir();
            }

            saveToJsonStatus(0);

            progressBar.setMax(100);
            progressBar.setProgress(0);

            if (portcheck.equals("")){
                porttext = 21;
            }
            else {
                porttext = Integer.parseInt(portcheck);
            }

            saveToJson(urltext,porttext, logintext, passwordtext);

            class MyThread extends Thread {
                public void run() {

                    boolean status;

                    status = downFile(urltext, porttext, logintext, passwordtext, "Json", "ongoings.json",dircheck);

                    if (!status){
                        toast("Сервер недоступен");
                    }
                    else {

                        progressBar.setProgress(1);

                        downFile(urltext, porttext, logintext, passwordtext, "Json", "completed.json",dircheck);

                        progressBar.setProgress(12);

                        progressBar.setProgress(24);

                        Json(dircheckPic800,dircheckPic400,dircheck,progressBar,"ongoings.json", "ongoings", urltext, porttext, logintext, passwordtext, 30);
                        Json(dircheckPic800,dircheckPic400,dircheck,progressBar,"completed.json", "completed", urltext, porttext, logintext, passwordtext, 60);

                        progressBar.setProgress(100);

                        startMainActivity();

                        progressBar.setProgress(0);

                    }

                }
            }

            MyThread myThread = new MyThread();
            myThread.start();

        });
    }

    public boolean downFile(
            String url, // имя хоста FTP-сервера
            int port, // порт FTP-сервера
            String username, // учетная запись FTP
            String password, // пароль для входа на FTP
            String remotePath, // относительный путь на FTP-сервере
            String fileName, // Имя файла для загрузки
            String localPath // Путь к локальному сохранению после загрузки
    ) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        ftp.setConnectTimeout(100);
        try {
            int reply;
            ftp.connect(url, port);
            ftp.login (username, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                toast("Неверный логин или пароль");
                return false;
            }
            ftp.changeWorkingDirectory (remotePath);

            File localFile = new File(localPath+"/"+fileName);
            OutputStream is = new FileOutputStream(localFile);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.retrieveFile(fileName, is);
            is.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
        return success;
    }

    private void startErrorActivity() {
        Intent intent = new Intent(CheckAndDownload.this, ErrorActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(CheckAndDownload.this, MainActivity.class);
        startActivity(intent);
    }

    public static String JsonDataFromAsset(File file) {
        String json;
        try {
            InputStream inputStream = new FileInputStream(file);
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, StandardCharsets.UTF_8);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return json;
    }

    final Handler h = new Handler();
    private void toast(final String Text) {
        h.post(() -> Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT).show());
    }


    private void Json(String dircheckPic800,
                      String dircheckPic400,
                      String dircheck,
                      ProgressBar progressBar,
                      String filename,
                      String name,
                      String urltext,
                      int porttext,
                      String logintext,
                      String passwordtext,
                      int setprogress) {
        try {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(JsonDataFromAsset(new File(dircheck, filename))));
            JSONArray jsonArray = jsonObject.getJSONArray(name);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userData = jsonArray.getJSONObject(i);
                File pic400 = new File(dircheckPic400+userData.getString("img"));
                if (!pic400.exists()) {
                    downFile(urltext, porttext, logintext, passwordtext, "Pictures400", userData.getString("img"), dircheckPic400);
                }
                File pic800 = new File(dircheckPic800+userData.getString("img"));
                if (!pic800.exists()) {
                    downFile(urltext, porttext, logintext, passwordtext, "Pictures800", userData.getString("img"), dircheckPic800);
                }
                progressBar.setProgress(setprogress + i * (30/jsonArray.length()));

            }
        } catch (Exception e) {
            startErrorActivity();
        }
    }

    private void saveToJson(String url, int port, String login, String password){
        JSONObject json = new JSONObject();
        try {
            json.put("url", url);
            json.put("port", port);
            json.put("login", login);
            json.put("password", password);

            String jsonString = json.toString();

            FileOutputStream fos = this.openFileOutput("jsonfile.json", MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveToJsonStatus(int st){
        JSONObject json = new JSONObject();
        try {
            json.put("st", st);

            String jsonString = json.toString();

            FileOutputStream fos = this.openFileOutput("jsonfile1.json", MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}