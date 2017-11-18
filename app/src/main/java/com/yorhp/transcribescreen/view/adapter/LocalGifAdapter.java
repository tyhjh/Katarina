package com.yorhp.transcribescreen.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yorhp.transcribescreen.R;
import com.yorhp.transcribescreen.utils.Defined;

import java.util.ArrayList;

/**
 * Created by Tyhj on 2017/10/29.
 */

public class LocalGifAdapter extends RecyclerView.Adapter<LocalGifAdapter.GifHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> gifsList;

    public LocalGifAdapter(Context context, ArrayList<String> gifsList) {
        this.context = context;
        this.gifsList = gifsList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public GifHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_locagif, parent, false);
        return new GifHolder(view);
    }

    @Override
    public void onBindViewHolder(GifHolder holder, int position) {
        Glide.with(context).load(gifsList.get(holder.getPosition())).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.iv_gif);
    }

    @Override
    public int getItemCount() {
        return gifsList.size();
    }

    class GifHolder extends RecyclerView.ViewHolder {
        ImageView iv_gif;

        public GifHolder(View itemView) {
            super(itemView);
            iv_gif = (ImageView) itemView.findViewById(R.id.iv_gif);
            iv_gif.setLayoutParams(new LinearLayout.LayoutParams(Defined.scrWidth / 4, Defined.scrWidth / 4));
        }
    }
}
