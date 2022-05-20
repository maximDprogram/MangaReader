package com.example.mangareader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.artifex.mupdf.viewer.DocumentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Objects;

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

        File chapter = new File(filePath);

        Uri cbzUri = Uri.fromFile(chapter);

        if (chapter1.equals("ch 0 apter1")) {
            holder.titleManga.setText(listMangas.get(position).getChapter1());
        }
        else {
            holder.titleManga.setText(listMangas.get(position).getTitle());
        }

        if (chapter.exists()) {
            final float scale = context.getResources().getDisplayMetrics().density;
            int pixels = (int) (15 * scale + 0.5f);
            int pixels1 = (int) (50 * scale + 0.5f);
            holder.titleManga.setPaddingRelative(pixels,0,pixels1,0);
            holder.checkmark.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {


            if (chapter.exists()) {
                startDocumentActivity(cbzUri);
            }
            else {

                Toast toast = Toast.makeText(context,
                        "Подождите", Toast.LENGTH_SHORT);
                toast.show();

                class MyThread extends Thread {
                    public void run() {

                        boolean status = false;
                        try {
                            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(CheckAndDownload.JsonDataFromAsset(context.getFileStreamPath("jsonfile.json"))));
                            status = ActivityManga.downFile(context,jsonObject.getString("url"),jsonObject.getInt("port"),jsonObject.getString("login"),jsonObject.getString("password"),"Cbz/"+listMangas.get(position).getTitleOrig(),listMangas.get(position).getTitle()+".cbz",context.getExternalCacheDir()+"/Cbz/"+listMangas.get(position).getTitleOrig()+"/");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (!status){
                            startErrorActivity();
                        }
                        else {

                            if (chapter.exists()) {
                                startDocumentActivity(cbzUri);
                            }
                            else {
                                startErrorActivity();
                            }
                        }


                    }
                }
                MyThread myThread = new MyThread();
                myThread.start();
            }

        });

    }

    private void startErrorActivity() {
        Intent intent = new Intent(context, ErrorActivity.class);
        context.startActivity(intent);
    }

    private void startDocumentActivity(Uri cbzUri) {
        Intent intent = new Intent(context, DocumentActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(cbzUri);
        context.startActivity(intent);
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
