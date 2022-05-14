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

public class CheckAndDownload extends AppCompatActivity {
public String urltext;
public int porttext;
public String logintext;
public String passwordtext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_and_download);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        EditText url = findViewById(R.id.editTextNumber);
        EditText port = findViewById(R.id.editTextNumber1);
        EditText login = findViewById(R.id.editTextNumber2);
        EditText password = findViewById(R.id.editTextNumber3);
        ImageView image = findViewById(R.id.imageView4);

        image.setOnClickListener(v -> {
            saveToJsonStatus(1);

            String dircheck = getExternalCacheDir()+"/Json";
            File dir = new File(dircheck);
            if (!dir.exists()) {
                File dirPdf = new File(dircheck);
                dirPdf.mkdir();
            }

            String filePath = getExternalCacheDir() +"/Json"+"/completed.json";

            File file = new File(filePath);

            if (!file.exists()) {

                Intent intentPdf = new Intent(CheckAndDownload.this, ErrorActivity.class);
                startActivity(intentPdf);

            }
            else {
                Intent intentPdf1 = new Intent(CheckAndDownload.this, MainActivity.class);
                startActivity(intentPdf1);
            }
        });


        progressBar.setOnClickListener(v -> {
            String dircheck = getExternalCacheDir()+"/Cbz";
            File dir = new File(dircheck);
            if (!dir.exists()) {
                File dirPdf = new File(dircheck);
                dirPdf.mkdir();
            }

            String dircheck1 = getExternalCacheDir()+"/Pictures";
            File dir1 = new File(dircheck1);
            if (!dir1.exists()) {
                File dirPictures = new File(dircheck1);
                dirPictures.mkdir();
            }

            String dircheck2 = getExternalCacheDir()+"/Json";
            File dir2 = new File(dircheck2);
            if (!dir2.exists()) {
                File dirPdf = new File(dircheck2);
                dirPdf.mkdir();
            }

            saveToJsonStatus(0);
            urltext = url.getText().toString();
            logintext = login.getText().toString();
            passwordtext = password.getText().toString();
            String portcheck = port.getText().toString();
            progressBar.setMax(100);
            progressBar.setProgress(0);


            if (portcheck.equals("")){
                porttext = 21;
            }
            else {
                porttext = Integer.parseInt(port.getText().toString());
            }

            saveToJson(urltext,porttext, logintext, passwordtext);

            class MyThread extends Thread {
                public void run() {

                    boolean status;

                    status = downFile(urltext, porttext, logintext, passwordtext, "Json", "ongoings.json",getExternalCacheDir() + "/Json");

                    if (status == false){
                        toast("Сервер недоступен");
                    }
                    else {

                        progressBar.setProgress(1);

                        downFile(urltext, porttext, logintext, passwordtext, "Json", "completed.json",getExternalCacheDir() + "/Json");
                        progressBar.setProgress(12);

                        progressBar.setProgress(24);

                        Json("ongoings.json", "ongoings", urltext, porttext, logintext, passwordtext, 30);
                        Json("completed.json", "completed", urltext, porttext, logintext, passwordtext, 60);

                        progressBar.setProgress(100);
                        Intent intentPdf = new Intent(CheckAndDownload.this, MainActivity.class);
                        startActivity(intentPdf);
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
        ftp.setConnectTimeout(1000);
        try {
            int reply;
            ftp.connect(url, port);
            ftp.login (username, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                toast("Неверный логин или пароль");
                return success;
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
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

    private String JsonDataFromAsset(String fileName) {
        String json = null;
        File file = getExternalPath(fileName);
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

    private File getExternalPath(String fileName) {
        return new File(this.getExternalCacheDir()+"/Json", fileName);
    }

    final Handler h = new Handler();
    private void toast(final String Text) {
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void Json(String filename,
                      String name,
                      String urltext,
                      int porttext,
                      String logintext,
                      String passwordtext,
                      int setprogress) {
        try {
            JSONObject jsonObject = new JSONObject(JsonDataFromAsset(filename));
            JSONArray jsonArray = jsonObject.getJSONArray(name);
            ProgressBar progressBar = findViewById(R.id.progressBar);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userData = jsonArray.getJSONObject(i);

                File pic = new File(getExternalCacheDir() + "/Pictures/"+userData.getString("img"));
                if(pic.exists()){
                    progressBar.setProgress(setprogress + i * (30/jsonArray.length()));
                }else{
                    downFile(urltext, porttext, logintext, passwordtext, "Pictures", userData.getString("img"),getExternalCacheDir() + "/Pictures/");
                    progressBar.setProgress(setprogress + i * (30/jsonArray.length()));
                }

            }
        } catch (Exception e) {
            Intent intent = new Intent(CheckAndDownload.this, ErrorActivity.class);
            startActivity(intent);
        }
    }

    public void saveToJson(String url, int port, String login, String password){
        JSONObject json = new JSONObject();
        try {
            json.put("url", url);
            json.put("port", port);
            json.put("login", login);
            json.put("password", password);

            String jsonString = json.toString();

            FileOutputStream fos = this.openFileOutput("jsonfile.json", this.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveToJsonStatus(int st){
        JSONObject json = new JSONObject();
        try {
            json.put("st", st);

            String jsonString = json.toString();

            FileOutputStream fos = this.openFileOutput("jsonfile1.json", this.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}