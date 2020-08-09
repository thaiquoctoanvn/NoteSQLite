package com.example.notesqlite;

import android.content.Context;
import android.graphics.Color;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class GhiChuAdapter extends RecyclerView.Adapter<GhiChuAdapter.ViewHolder> {

    interface OnItemClickInterface {
        void Click(View v, int position);
    }
    private OnItemClickInterface onItemClickInterface;
    public void SetOnItemClick(OnItemClickInterface onItemClickInterface){
        this.onItemClickInterface = onItemClickInterface;
    }

    private List<GhiChu> mList;
    private MainActivity context;
    private GhiChu recentlyDeletedItem;
    private int recentlyDeletedItemPosition;
    public GhiChuAdapter(MainActivity context, List<GhiChu> mList){
        this.context = context;
        this.mList = mList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ghichu_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GhiChu item = mList.get(position);
        holder.tvtitle.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void deleteItem(int position) {
        recentlyDeletedItem = mList.get(position);
        recentlyDeletedItemPosition = position;
        mList.remove(position);
        notifyItemRemoved(position);
        context.database.QueryData("DELETE FROM GhiChu WHERE Id = '" + recentlyDeletedItem.getId() + "'");
        showUndoSnackbar();//hiện snackbar undo lên
        Log.e("Id", String.valueOf(recentlyDeletedItem.getId()));
        Log.e("position", String.valueOf(recentlyDeletedItemPosition));
    }
    private void showUndoSnackbar() {

        //
        Snackbar snackbar = Snackbar.make( context.findViewById(R.id.forSnackBar),"Đã xóa mục", Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.parseColor("#008577"));
        snackbar.setAction("Hoàn tác", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.add(recentlyDeletedItemPosition, recentlyDeletedItem);
                //context.database.QueryData("UPDATE GhiChu SET Id = Id + 1 WHERE Id >= '"+ recentlyDeletedItemPosition +"'");
                context.database.QueryData("INSERT INTO GhiChu VALUES ('" + recentlyDeletedItem.getId() +"', '" + recentlyDeletedItem.getTitle() + "')");
                notifyItemInserted(recentlyDeletedItemPosition);
            }
        });
        snackbar.show();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvtitle;
        //private ImageButton ibtnedit, ibtnremove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvtitle = (TextView) itemView.findViewById(R.id.tvTitle);
            //ibtnedit = (ImageButton) itemView.findViewById(R.id.ibtnEdit);
            //ibtnremove = (ImageButton) itemView.findViewById(R.id.ibtnRemove);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickInterface.Click(v, getAdapterPosition());
        }
    }
}
