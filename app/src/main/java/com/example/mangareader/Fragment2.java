package com.example.mangareader;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Fragment2 extends Fragment {

    RecyclerView recyclerView;
    ViewMangaAdapter viewMangaAdapter;
    private final static String FILE_NAME = "completed.json";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);

        List<ViewManga> mangaList = new ArrayList<>();


        try {
            JSONObject jsonObject = new JSONObject(JsonDataFromAsset("completed.json"));
            JSONArray jsonArray = jsonObject.getJSONArray("completed");
            for (int i=0;i< jsonArray.length();i++){
                JSONObject userData=jsonArray.getJSONObject(i);

                File pic = new File(getActivity().getExternalCacheDir() + "/Pictures/"+userData.getString("img"));
                if(pic.exists()){
                    mangaList.add(new ViewManga(userData.getInt("id"),userData.getString("img"),userData.getString("title"),userData.getString("titleOrig"),userData.getString("description"),userData.getString("chapter1")));
                }else{
                    Intent intent = new Intent(getActivity(), ErrorActivity.class);
                    startActivity(intent);
                }

            }
        } catch (Exception e) {
            Intent intent = new Intent(getActivity(), ErrorActivity.class);
            startActivity(intent);
        }

        recyclerView = view.findViewById(R.id.recyclerView);

        viewMangaAdapter = new ViewMangaAdapter(view.getContext(), mangaList);
        recyclerView.setAdapter(viewMangaAdapter);

        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), numberOfColumns));


        return view;
    }

    private String JsonDataFromAsset(String fileName) {
        String json = null;
        File file = getExternalPath();
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

    private File getExternalPath() {
        return new File(getActivity().getExternalCacheDir()+"/Json", FILE_NAME);
    }

}