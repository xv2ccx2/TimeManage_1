package com.example.timemanage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatlistAdapter extends RecyclerView.Adapter
        <ChatlistAdapter.MyViewHolder> {
    private List<Chatlist> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    public ChatlistAdapter(Context context, List<Chatlist> datas){
        this. mContext=context;
        this. mDatas=datas;
        inflater=LayoutInflater. from(mContext);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView speakername;
        TextView speakcontent;
        View v;
        public MyViewHolder(View view) {
            super(view);
            speakername=(TextView) view.findViewById(R.id.rc_tv_speakername);
            speakcontent=(TextView) view.findViewById(R.id.rc_tv_speakcontent);
            v=view;
        }

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Chatlist da=mDatas.get(position);
        holder.speakername.setText(da.getSpeakerName());
        holder.speakcontent.setText(da.getSpeakContent());
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    //这里边还可以对RecycleView里的独立Item进行操作(简单起见这次没有这样弄)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rc_chatlist_layout,parent,false);
        MyViewHolder holder= new MyViewHolder(view);


        return holder;
    }

    public void ResetChatlistAdapter(List list){
        this.mDatas = list;
    }
}