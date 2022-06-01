package com.example.mangareader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewMangaAdapter extends RecyclerView.Adapter<ViewMangaAdapter.ViewMangaViewHolder> {

    Context context;
    List<ViewManga> viewMangas;

    public ViewMangaAdapter(Context context, List<ViewManga> viewMangas) {
        this.context = context;
        this.viewMangas = viewMangas;
    }


    @NonNull
    @Override
    public ViewMangaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewMangaItems = LayoutInflater.from(context).inflate(R.layout.viewmanga_item, parent, false);
        return new ViewMangaAdapter.ViewMangaViewHolder(viewMangaItems);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewMangaViewHolder holder, @SuppressLint("RecyclerView") int position) {

            String image400ViewPath = context.getExternalCacheDir()+"/Pictures400/"+viewMangas.get(position).getImg();
            String image800ViewPath = context.getExternalCacheDir()+"/Pictures800/"+viewMangas.get(position).getImg();

            Handler handler = new Handler(context.getMainLooper());
            handler.post(() -> holder.mangaImage.setImageURI(Uri.parse(image400ViewPath)));

            final float scale = context.getResources().getDisplayMetrics().density;
            int pixels = (int) (197 * scale + 0.5f);
            holder.constraintLayout.setMinWidth(pixels);
            holder.constraintLayout.setMaxWidth(pixels);    

            holder.mangaTitle.setText(viewMangas.get(position).getTitle());

            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, ActivityManga.class);

                intent.putExtra("mangaTitle", viewMangas.get(position).getTitle());
                intent.putExtra("mangaTitleOrig", viewMangas.get(position).getTitleOrig());
                intent.putExtra("mangaDescription", viewMangas.get(position).getDescription());
                intent.putExtra("mangaImageView", image800ViewPath);
                intent.putExtra("chapter1", viewMangas.get(position).getChapter1());


                context.startActivity(intent);
            });


    }

    @Override
    public int getItemCount() {
        return viewMangas.size();
    }

    public static final class ViewMangaViewHolder extends RecyclerView.ViewHolder {

        ImageView mangaImage;
        TextView mangaTitle;
        ConstraintLayout constraintLayout;

        public ViewMangaViewHolder(@NonNull View itemView) {
            super(itemView);
            mangaImage = itemView.findViewById(R.id.mangaImage);
            mangaTitle = itemView.findViewById(R.id.mangaTitle);
            constraintLayout = itemView.findViewById(R.id.constraint_viewmanga_item);

        }
    }
}