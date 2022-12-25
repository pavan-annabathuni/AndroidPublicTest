package com.waycool.featurelogin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.waycool.data.repository.domainModels.ModuleMasterDomain;
import com.waycool.featurelogin.R;
import com.waycool.featurelogin.fragment.RegistrationFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserProfilePremiumAdapter extends RecyclerView.Adapter<UserProfilePremiumAdapter.TilesVh> {
    List<ModuleMasterDomain> listItem;
    Context context;
    RegistrationFragment fragment;
    public UserProfilePremiumAdapter( Context context, RegistrationFragment fragment){
        this.listItem = new ArrayList<>();
        this.context = context;
        this.fragment = fragment;
    }
    @NonNull
    @Override
    public TilesVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_service_layout, parent, false);
        return new TilesVh(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TilesVh holder, int position) {
        ModuleMasterDomain data = listItem.get(position);
        holder.itemName.setText(data.getTitle());
        //AppUtil.showImageDrawable(context,data.getImage(),holder.itemImage);
        Glide.with(holder.itemImage).load(data.getModuleIcon())
                .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                .into(holder.itemImage);
    }
    @Override
    public int getItemCount() {
        return listItem.size();
    }
    public class TilesVh extends RecyclerView.ViewHolder implements View.OnClickListener {

       TextView itemName;
       ConstraintLayout transpatrentLayout;
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
            fragment.showServiceDialog(listItem.get(getLayoutPosition()).getTitle(),
                    listItem.get(getLayoutPosition()).getModuleDesc(),
                    listItem.get(getLayoutPosition()).getAudioUrl(),
                    String.valueOf(listItem.get(getLayoutPosition()).getSubscription()),
                    listItem.get(getLayoutPosition()).getModuleIcon(),
                    context);
        }
    }

    public void update(List<ModuleMasterDomain> tempList){
        listItem.clear();
        listItem.addAll(tempList);
        notifyDataSetChanged();
    }
}
