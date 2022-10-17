package com.waycool.videos.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.waycool.data.Network.NetworkModels.AdBannerImage;
import com.waycool.uicomponents.R;

import java.util.List;


public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private final Context context;
    List<AdBannerImage> bannerList;

    public BannerAdapter(Context context, List<AdBannerImage> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestOptions requestOptions=new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(bannerList.get(position).getUrl())
                .apply(requestOptions)
                .into(holder.banneriv);
        holder.banneriv.setOnClickListener(view1 -> {
            if (bannerList.get(position).getUrl() != null && !bannerList.get(position).getUrl().isEmpty()) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(bannerList.get(position).getUrl()));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView banneriv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            banneriv = itemView.findViewById(R.id.vh_banner_iv);
        }
    }
}
