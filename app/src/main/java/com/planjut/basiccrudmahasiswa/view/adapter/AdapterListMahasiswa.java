package com.planjut.basiccrudmahasiswa.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.planjut.basiccrudmahasiswa.R;
import com.planjut.basiccrudmahasiswa.model.Mahasiswa;

import java.util.List;

public class AdapterListMahasiswa extends RecyclerView.Adapter<AdapterListMahasiswa.HolderItem> {

    List<Mahasiswa> mListMahasiswa;
    Context context;

    public AdapterListMahasiswa(List<Mahasiswa> mListMahasiswa, Context context){
        this.mListMahasiswa = mListMahasiswa;
        this.context = context;
    }

    @NonNull
    @Override
    public HolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(context).inflate(R.layout.layout_rows, parent, false);
        HolderItem holder = new HolderItem(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderItem holder, int position) {
        Mahasiswa mList = mListMahasiswa.get(position);

        holder.tv_nama.setText(mList.getNama());
        holder.tv_nrp.setText(mList.getNrp());

        // Loading Image
        Glide.with(context).load("http://192.168.43.147/clientserver/image/" + mList.getImageName()).thumbnail(0.5f).transition(new DrawableTransitionOptions().crossFade()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return mListMahasiswa.size();
    }

    public class HolderItem extends RecyclerView.ViewHolder{
        ImageView thumbnail;
        TextView tv_nama, tv_nrp;

        public HolderItem(View v){
            super(v);
            thumbnail = (ImageView) v.findViewById(R.id.img_cover);
            tv_nama = (TextView) v.findViewById(R.id.tv_nama);
            tv_nrp = (TextView) v.findViewById(R.id.tv_nrp);
        }
    }

}
