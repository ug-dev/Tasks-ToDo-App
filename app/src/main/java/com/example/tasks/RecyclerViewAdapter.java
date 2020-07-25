package com.example.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<ListModel> listModels;
    private Context context;
    private OnListListener mOnListListener;

    public RecyclerViewAdapter(Context context, OnListListener onListListener, List<ListModel> listModels) {
        this.context = context;
        this.mOnListListener = onListListener;
        this.listModels = listModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_card, parent, false);

        return new ViewHolder(view, mOnListListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListModel listModel = listModels.get(position);

        holder.list_name.setText(listModel.getList_name());

        if (!listModel.getList_total().equals("0")) {
            holder.list_total.setText(listModel.getList_total());
        }

        switch (listModel.getList_color()) {
            case "Theme Color 1":
                holder.list_logo.setColorFilter(context.getResources()
                        .getColor(R.color.theme_color_1));
                break;
            case "Theme Color 2":
                holder.list_logo.setColorFilter(context.getResources()
                        .getColor(R.color.theme_color_2));
                break;
            case "Theme Color 3":
                holder.list_logo.setColorFilter(context.getResources()
                        .getColor(R.color.theme_color_3));
                break;
            case "Theme Color 4":
                holder.list_logo.setColorFilter(context.getResources()
                        .getColor(R.color.theme_color_4));
                break;
            case "Theme Color 5":
                holder.list_logo.setColorFilter(context.getResources()
                        .getColor(R.color.theme_color_5));
                break;
            case "Theme Color 6":
                holder.list_logo.setColorFilter(context.getResources()
                        .getColor(R.color.theme_color_6));
                break;
            case "Theme Color 7":
                holder.list_logo.setColorFilter(context.getResources()
                        .getColor(R.color.theme_color_7));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView list_name;
        TextView list_total;
        ImageView list_logo;
        OnListListener onListListener;

        ViewHolder(@NonNull View itemView, OnListListener onListListener) {
            super(itemView);

            this.onListListener = onListListener;
            list_name = itemView.findViewById(R.id.list_row_title);
            list_total = itemView.findViewById(R.id.list_row_total);
            list_logo = itemView.findViewById(R.id.list_row_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListListener.onListClick(getAdapterPosition());
        }
    }

    public interface OnListListener {
        void onListClick(int position);
    }
}
