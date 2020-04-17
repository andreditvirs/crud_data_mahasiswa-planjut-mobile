package com.planjut.basiccrudmahasiswa.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.planjut.basiccrudmahasiswa.R;
import com.planjut.basiccrudmahasiswa.model.Mahasiswa;
import com.planjut.basiccrudmahasiswa.view.adapter.AdapterListMahasiswa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.planjut.basiccrudmahasiswa.api.ApiMahasiswa.URL_READ;

public class ListMahasiwaFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    RequestQueue mRequest;
    List<Mahasiswa> mList = new ArrayList<Mahasiswa>();

    public ListMahasiwaFragment() {
        // Required empty public constructor
    }

    public static ListMahasiwaFragment newInstance(String param1, String param2) {
        ListMahasiwaFragment fragment = new ListMahasiwaFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_mahasiwa, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerTemp);
        mManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        request();
        mRecyclerView.setLayoutManager(mManager);
        mAdapter =  new AdapterListMahasiswa(mList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    private void request(){
        mList.clear();
        JsonObjectRequest requestImage = new JsonObjectRequest(Request.Method.POST, URL_READ, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
//                        Log.d("JSONObject", object.toString());
                        try{
                            if(!object.getBoolean("error")){
                                JSONArray mahasiswa = object.getJSONArray("mahasiswa");
                                for (int i = 0; i < mahasiswa.length(); i++) {
                                    JSONObject obj = mahasiswa.getJSONObject(i);
                                    mList.add(new Mahasiswa(
                                            obj.getInt("id"),
                                            obj.getString("nrp"),
                                            obj.getString("nama"),
                                            obj.getString("alamat"),
                                            obj.getString("tmpLahir"),
                                            obj.getString("tglLahir"),
                                            obj.getString("imageName")
                                    ));
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERRORRequest", "Error : " + error.getMessage());
            }
        });
        mRequest = Volley.newRequestQueue(getActivity());
        mRequest.add(requestImage);
    }
}
