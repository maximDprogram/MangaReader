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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fragment2 extends Fragment {

    RecyclerView recyclerView;
    ViewMangaAdapter viewMangaAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        List<ViewManga> mangaList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(CheckAndDownload.JsonDataFromAsset(new File(requireActivity().getExternalCacheDir()+"/Json", "completed.json"))));
            JSONArray jsonArray = jsonObject.getJSONArray("completed");
            for (int i=0;i< jsonArray.length();i++){
                JSONObject userData=jsonArray.getJSONObject(i);

                File pic400 = new File(requireActivity().getExternalCacheDir() + "/Pictures400/"+userData.getString("img"));
                File pic800 = new File(requireActivity().getExternalCacheDir() + "/Pictures800/"+userData.getString("img"));
                if(pic400.exists() & pic800.exists()){
                    mangaList.add(new ViewManga(userData.getInt("id"),userData.getString("img"),userData.getString("title"),userData.getString("titleOrig"),userData.getString("description"),userData.getString("chapter1")));
                }else{startErrorActivity();}
            }
        } catch (Exception e) {startErrorActivity();}

        viewMangaAdapter = new ViewMangaAdapter(view.getContext(), mangaList);
        recyclerView.setAdapter(viewMangaAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));

        return view;
    }

    private void startErrorActivity() {
        Intent intent = new Intent(getActivity(), ErrorActivity.class);
        startActivity(intent);
    }
}