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
import com.waycool.data.eventscreentime.EventClickHandling;
import com.waycool.data.repository.domainModels.ModuleMasterDomain;
import com.waycool.featurelogin.R;
import com.waycool.featurelogin.fragment.RegistrationFragment;

import java.util.ArrayList;
import java.util.List;

public class KnowYourPremiumServicesAdapter extends RecyclerView.Adapter<KnowYourPremiumServicesAdapter.TilesVh> {
    List<ModuleMasterDomain> listItem;
    Context context;
    RegistrationFragment fragment;
    public KnowYourPremiumServicesAdapter(Context context, RegistrationFragment fragment){
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
        //set title
        holder.itemName.setText(data.getTitle());
        //set image using Glide
        Glide.with(holder.itemImage).load(data.getModuleIcon())
                .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                .into(holder.itemImage);
    }

    //Return size of list
    @Override
    public int getItemCount() {
        return listItem.size();
    }

    //click listener on item
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

        //on click on any service item we will open the dialog box having the detail about that premium service
        @Override
        public void onClick(View view) {
            fragment.showServiceDialog(listItem.get(getLayoutPosition()).getTitle(),
                    listItem.get(getLayoutPosition()).getModuleDesc(),
                    listItem.get(getLayoutPosition()).getAudioUrl(),
                    String.valueOf(listItem.get(getLayoutPosition()).getSubscription()),
                    listItem.get(getLayoutPosition()).getModuleIcon(),
                    context);
            EventClickHandling.INSTANCE.calculateClickEvent(listItem.get(getLayoutPosition()).getTitle());
        }
    }

    public void update(List<ModuleMasterDomain> tempList){
        listItem.clear();
        listItem.addAll(tempList);
        notifyDataSetChanged();
    }
}
