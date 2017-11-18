package com.yorhp.transcribescreen.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yorhp.transcribescreen.R;
import com.yorhp.transcribescreen.module.Gif;

import java.util.ArrayList;

/**
 * Created by Tyhj on 2017/6/29.
 */

public class GifAdapter extends RecyclerView.Adapter<GifAdapter.Holder> {

    ArrayList<Gif> arrayList = new ArrayList<Gif>();
    Context context;
    LayoutInflater inflater;


    public GifAdapter(Context context, ArrayList<Gif> gifArrayList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        arrayList = gifArrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_gif, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.tv_name.setText(arrayList.get(holder.getPosition()).getUserName());
        Glide.with(context).load(arrayList.get(holder.getPosition()).getUrl()).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().into(holder.iv_gif);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tv_name;
        ImageView iv_gif;

        public Holder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_gif = (ImageView) itemView.findViewById(R.id.iv_gif);
            //iv_gif.setLayoutParams(new LinearLayout.LayoutParams(Defined.scrWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}
