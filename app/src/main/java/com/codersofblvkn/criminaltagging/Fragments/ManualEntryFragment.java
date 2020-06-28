package com.codersofblvkn.criminaltagging.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codersofblvkn.criminaltagging.Activities.TempActivity;
import com.codersofblvkn.criminaltagging.Adapters.DetectionsAdapter;
import com.codersofblvkn.criminaltagging.Adapters.OnItemClickListener;
import com.codersofblvkn.criminaltagging.R;
import com.codersofblvkn.criminaltagging.Utils.Detection;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManualEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManualEntryFragment extends Fragment implements OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManualEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManualEntryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManualEntryFragment newInstance(String param1, String param2) {
        ManualEntryFragment fragment = new ManualEntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manual_entry, container, false);
        RecyclerView rv=view.findViewById(R.id.rv);
        Observable.fromCallable(() -> {
            Request request = new Request.Builder()
                    .url("http://coders-of-blaviken-api.herokuapp.com/api/detections")
                    .build();
            try {
                OkHttpClient sHttpClient=new OkHttpClient();
                Response response = sHttpClient.newCall(request).execute();
                if(response.isSuccessful())
                {
                    return response.body().string();
                }
                else
                {
                    return null;
                }
            } catch (IOException e) {
                Log.e("Network request", "Failure", e);
            }

            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {

                    if(result!=null)
                    {
                        List<Detection> detections=new ArrayList<Detection>();
                        JSONObject jsonObject=new JSONObject(result);
                        JSONArray jsonArray=jsonObject.getJSONArray("detections");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            double lat=0,lon=0;
                            JSONObject tDetect=jsonArray.getJSONObject(i);
                            int id=tDetect.getInt("id");
                            int cid=tDetect.getInt("cid");
                            String[] location =tDetect.getString("location").replace("dot",".").split(",");
                            if(location.length!=2)
                            {
                                lat=0;
                                lon=0;
                            }
                            else {
                                lat=Double.parseDouble(location[0].substring(0,7));
                                lon=Double.parseDouble(location[1].substring(1,8));
                            }
                            Log.d("Detections",lat+" "+lon+" "+i);
//                            double lat=Double.parseDouble(location[0]);
//                            double lon=Double.parseDouble(location[1]);
                            String img=tDetect.getString("rsrc");
                            String myDate = tDetect.getString("time_stamp");
                            Date date = sdf.parse(myDate);
                            long time=date.getTime();
                            Detection detection=new Detection(lat,lon,time,img,id,cid);
                            detections.add(detection);
                        }
                        Collections.sort(detections, new Comparator<Detection>() {
                            @Override
                            public int compare(Detection t1, Detection t2) {
                                return t1.getId()-t2.getId();
                            }
                        });
                        DetectionsAdapter detectionsAdapter=new DetectionsAdapter(detections, this::onItemClick);
                        rv.setAdapter(detectionsAdapter);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        rv.setLayoutManager(layoutManager);
                        rv.setItemAnimator(new SlideInUpAnimator());
                        Log.d("Detections",jsonArray.length()+" ");
                    }
//                    DetectionsAdapter detectionsAdapter=new DetectionsAdapter();

                });
        return view;
    }

    @Override
    public void onItemClick(Detection item) {
//        Toast.makeText(getContext(),"Hello",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getActivity(), TempActivity.class);
        intent.putExtra("detection",item);
        startActivity(intent);
    }
}
