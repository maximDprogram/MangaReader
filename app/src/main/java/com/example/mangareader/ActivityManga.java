package com.example.mangareader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdf.viewer.DocumentActivity;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class ActivityManga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga);

        ImageView mangaImageView = findViewById(R.id.mangaImageView);
        TextView mangaTitle = findViewById(R.id.mangaTitle);
        TextView mangaTitleOrig = findViewById(R.id.mangaTitleOrig);
        TextView textDescription = findViewById(R.id.textDescription);

        TextView button = findViewById(R.id.button);
        TextView button1 = findViewById(R.id.button1);

        float scale = this.getResources().getDisplayMetrics().density;

        int pixels = (int) (200 * scale + 0.5f);

        String dircheck = getExternalCacheDir() + "/Cbz/" + getIntent().getStringExtra("mangaTitleOrig");
        String filePath = getExternalCacheDir() + "/Cbz/" + getIntent().getStringExtra("mangaTitleOrig") + "/ch 0 apter1.cbz";

        File dir = new File(dircheck);
        File file = new File(filePath);

        Uri cbzUri = Uri.fromFile(file);

        Bitmap mangaImageViewBm = decodeSampledBitmapFromResource(getIntent().getStringExtra("mangaImageView"), 800, 1133);

        button.setMinWidth(pixels);
        button.setMaxWidth(pixels);
        button1.setMinWidth(pixels);
        button1.setMaxWidth(pixels);

        mangaImageView.setImageBitmap(mangaImageViewBm);
        mangaTitle.setText(getIntent().getStringExtra("mangaTitle"));
        mangaTitleOrig.setText(getIntent().getStringExtra("mangaTitleOrig"));
        textDescription.setText(getIntent().getStringExtra("mangaDescription"));

        button.setOnClickListener(v -> {

            if (!dir.exists()) {
                dir.mkdir();
            }

            if (file.exists()) {
                startDocumentActivity(cbzUri);
            }
            else {

                int st = 1;

                try {
                    JSONObject jsonObject1 = new JSONObject(Objects.requireNonNull(CheckAndDownload.JsonDataFromAsset(getFileStreamPath("jsonfile1.json"))));
                    st = jsonObject1.getInt("st");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (st == 0) {

                    toast("Подождите");

                    class MyThread extends Thread {
                        public void run() {

                            boolean status = false;
                            try {
                                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(CheckAndDownload.JsonDataFromAsset(getFileStreamPath("jsonfile.json"))));
                                status = downFile(jsonObject.getString("url"), jsonObject.getInt("port"), jsonObject.getString("login"), jsonObject.getString("password"), "Cbz/" + getIntent().getStringExtra("mangaTitleOrig"), "ch 0 apter1.cbz", getExternalCacheDir() + "/Cbz/" + getIntent().getStringExtra("mangaTitleOrig") + "/");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if (!status) {
                                toast("Первая глава не найдена");
                            } else {

                                if (file.exists()) {
                                    startDocumentActivity(cbzUri);
                                } else {
                                    toast("Первая глава не найдена");
                                }
                            }
                        }
                    }
                    MyThread myThread = new MyThread();
                    myThread.start();
                }
                else {
                    toast("Первая глава не найдена");
                }
            }
        });

        button1.setOnClickListener(v -> {

            Intent intentCbz = new Intent(ActivityManga.this, ListOfChapters.class);
            intentCbz.putExtra("PathListPDF1",getIntent().getStringExtra("mangaTitleOrig"));
            intentCbz.putExtra("chapter1",getIntent().getStringExtra("chapter1"));

            startActivity(intentCbz);
        });
        }

    public void onBackPressed() { finish(); }

    private boolean downFile(
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
        ftp.setControlEncoding("UTF-8");
        try {
            int reply;
            ftp.connect(url, port);
            ftp.login (username, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return false;
            }
            ftp.changeWorkingDirectory (remotePath);

            File localFile = new File(localPath+"/"+fileName);
            FTPFile file = ftp.mlistFile(fileName);
            OutputStream is = new FileOutputStream(localFile);

            CopyStreamAdapter streamListener = new CopyStreamAdapter() {

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {

                    int percent = (int)(totalBytesTransferred*100/file.getSize());

                    if (percent % 25 == 0) {
                        Notification(percent);
                    }
                }

            };
            createNotificationChannel();
            ftp.setCopyStreamListener(streamListener);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.retrieveFile(fileName, is);
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(this);
            notificationManager.cancelAll();
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

    private void Notification(int percent) {

        int NOTIFY_ID = 101;

        String CHANNEL_ID = "channel";


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.stat_sys_download)
                        .setContentTitle("Загрузка главы")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setProgress(100,percent,false);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel", "Загрузка", importance);
            channel.setDescription("глава");
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(String res, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(res, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = Math.min(heightRatio, widthRatio);
        }

        return inSampleSize;
    }

    private void startDocumentActivity(Uri cbzUri){
        Intent intent = new Intent(this, DocumentActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(cbzUri);
        startActivity(intent);
    }

    final Handler h = new Handler();
    private void toast(final String Text) {
        h.post(() -> Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT).show());
    }
}