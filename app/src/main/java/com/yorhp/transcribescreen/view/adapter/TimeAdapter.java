package com.yorhp.transcribescreen.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yorhp.transcribescreen.R;

import java.util.ArrayList;

/**
 * Created by Tyhj on 2017/6/29.
 */

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.Holder> {

    ArrayList<Integer> arrayList;
    Context context;
    LayoutInflater inflater;
    OnItemClickListener listener;


    public TimeAdapter(Context context, ArrayList<Integer> gifArrayList, OnItemClickListener listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        arrayList = gifArrayList;
        this.listener = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_time, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        int time = arrayList.get(holder.getPosition());
        holder.tv_time.setText(time + "s");
        holder.tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(Integer.parseInt(holder.tv_time.getText().toString().split("s")[0]));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tv_time;

        public Holder(View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int time);
    }
}
