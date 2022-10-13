package com.wayone.farmerregistration.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waycool.data.Network.NetworkModels.ModuleData;
import com.wayone.farmerregistration.R;
import com.wayone.farmerregistration.activity.UserRegistrationActivity;

import java.util.List;

public class UserProfilePremiumAdapter extends RecyclerView.Adapter<UserProfilePremiumAdapter.TilesVh> {
    List<ModuleData> listItem;
    Context context;
    UserRegistrationActivity activity;
    public UserProfilePremiumAdapter(List<ModuleData> listItem, Context context, UserRegistrationActivity activity){
        this.listItem = listItem;
        this.context = context;
        this.activity = activity;
    }
    @NonNull
    @Override
    public TilesVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_service_layout, parent, false);
        return new TilesVh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TilesVh holder, int position) {
        ModuleData data = listItem.get(position);
        holder.itemName.setText(data.getTittle());
        //AppUtil.showImageDrawable(context,data.getImage(),holder.itemImage);
    }
    @Override
    public int getItemCount() {
        return listItem.size();
    }
    public class TilesVh extends RecyclerView.ViewHolder implements View.OnClickListener {

       TextView itemName;
       RelativeLayout transpatrentLayout;
       ImageView itemImage;
        public TilesVh(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            transpatrentLayout = itemView.findViewById(R.id.transparent_layout);
            itemImage = itemView.findViewById(R.id.list_item_image);
            transpatrentLayout.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            activity.replaceFragmentwithoutbackstack(listItem.get(getAdapterPosition()).getTittle(),listItem.get(getAdapterPosition()).getModuleDesc(),listItem.get(getAdapterPosition()).getAudioURl(), String.valueOf(listItem.get(getAdapterPosition()).getPremium()));
        }
    }
}
