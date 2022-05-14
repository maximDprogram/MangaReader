package com.example.mangareader;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.artifex.mupdf.viewer.DocumentActivity;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

public class ListMangasAdapter extends RecyclerView.Adapter<ListMangasAdapter.ListMangasAdapterViewHolder> {

    Context context;
    List<ListMangas> listMangas;

    public ListMangasAdapter(Context context, List<ListMangas> listMangas) {
        this.context = context;
        this.listMangas = listMangas;
    }


    @NonNull
    @Override
    public ListMangasAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listMangaItems = LayoutInflater.from(context).inflate(R.layout.listmangas_item, parent, false);
        return new ListMangasAdapter.ListMangasAdapterViewHolder(listMangaItems);
    }


    @Override
    public void onBindViewHolder(@NonNull ListMangasAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.checkmark.setVisibility(View.GONE);
        String chapter1 = listMangas.get(position).getTitle();

        String filePath = context.getExternalCacheDir()+"/Cbz/"+listMangas.get(position).getTitleOrig()+"/"+listMangas.get(position).getTitle()+".cbz";

        if (chapter1.equals("ch 0 apter1")) {
            holder.titleManga.setText(listMangas.get(position).getChapter1());
        }
        else {
            holder.titleManga.setText(listMangas.get(position).getTitle());
        }

        if (new  File(filePath).exists()) {
            final float scale = context.getResources().getDisplayMetrics().density;
            int pixels = (int) (15 * scale + 0.5f);
            int pixels1 = (int) (50 * scale + 0.5f);
            holder.titleManga.setPaddingRelative(pixels,0,pixels1,0);
            holder.checkmark.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {


            if (new  File(filePath).exists()) {

                File savedPDFFile = new File(filePath);

                Uri pdfUri = Uri.fromFile(savedPDFFile);

                Intent intent = new Intent(context, DocumentActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(pdfUri);
                context.startActivity(intent);
            }
            else {

                Toast toast = Toast.makeText(context,
                        "Подождите", Toast.LENGTH_SHORT);
                toast.show();
                class MyThread extends Thread {
                    public void run() {

                        boolean status = false;
                        try {
                            JSONObject jsonObject = new JSONObject(JsonDataFromAsset("jsonfile.json"));
                            status = downFile(jsonObject.getString("url"),jsonObject.getInt("port"),jsonObject.getString("login"),jsonObject.getString("password"),"Cbz/"+listMangas.get(position).getTitleOrig(),listMangas.get(position).getTitle()+".cbz",context.getExternalCacheDir()+"/Cbz/"+listMangas.get(position).getTitleOrig()+"/");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if (status == false){
                            Intent intent = new Intent(context, ErrorActivity.class);
                            context.startActivity(intent);
                        }
                        else {

                            if (new  File(filePath).exists()) {
                                File savedPDFFile = new File(filePath);

                                Uri pdfUri = Uri.fromFile(savedPDFFile);

                                Intent intent = new Intent(context, DocumentActivity.class);
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setData(pdfUri);
                                context.startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(context, ErrorActivity.class);
                                context.startActivity(intent);
                            }
                        }


                    }
                }
                MyThread myThread = new MyThread();
                myThread.start();
            }

        });

    }

    private String JsonDataFromAsset(String fileName) {
        String json = null;
        File file = context.getFileStreamPath(fileName);
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
        ftp.setConnectTimeout(60);
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
            createNotificationChannel("Загрузка", "глава", "channel");
            ftp.setCopyStreamListener(streamListener);

            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.retrieveFile(fileName, is);
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
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
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

    private void Notification(int percent) {

        int NOTIFY_ID = 101;

        String CHANNEL_ID = "channel";


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.stat_sys_download)
                        .setContentTitle("Загрузка главы")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setProgress(100,percent,false);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    private void createNotificationChannel(String name, String description, String CHANNEL_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int getItemCount() {
        return listMangas.size();
    }

    public static final class ListMangasAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView titleManga;
        ImageView checkmark;

        public ListMangasAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            titleManga = itemView.findViewById(R.id.titleManga);
            checkmark = itemView.findViewById(R.id.checkmark);

        }
    }
}
