package com.example.mangareader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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



        String imageViewPath = context.getExternalCacheDir()+"/Pictures/"+viewMangas.get(position).getImg();

        class MyThread extends Thread {
            public void run() {
                Bitmap imageView = decodeSampledBitmapFromResource(imageViewPath, 400, 566);
                Handler handler = new Handler(context.getMainLooper());
                handler.post( new Runnable() {
                    @Override
                    public void run() {
                        holder.mangaImage.setImageBitmap(imageView);
                    }
                } );
            }
        }
        MyThread myThread = new MyThread();
        myThread.start();

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
                intent.putExtra("mangaImageView", imageViewPath);
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

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

}
