package com.example.tasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class UserTasksRecyclerViewAdapter extends RecyclerView.Adapter<UserTasksRecyclerViewAdapter.ViewHolder> {
    private List<TaskModel> taskModels;
    private Context context;
    private OnTaskListener mOnTaskListener;
    private String themeColor;

    public UserTasksRecyclerViewAdapter(List<TaskModel> taskModels, Context context, OnTaskListener onTaskListener,
                                        String themeColor) {
        this.taskModels = taskModels;
        this.context = context;
        this.mOnTaskListener = onTaskListener;
        this.themeColor = themeColor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.create_task_list_popup, parent, false);
        return new ViewHolder(view, mOnTaskListener);
    }

    @SuppressLint({"SimpleDateFormat", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseHandler handler = new DatabaseHandler(context);
        TaskModel tModels = taskModels.get(position);

        boolean isLayout = false;

        if (tModels.getTask_is_completed().equals("true")) {
            holder.task_name.setTextColor(Color.parseColor("#777777"));
            holder.checkbox_uncheck.setVisibility(View.GONE);
            holder.checkbox_check.setVisibility(View.VISIBLE);

            switch (themeColor) {
                case "Theme Color 1":
                    holder.checkbox_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_1));
                    break;
                case "Theme Color 2":
                    holder.checkbox_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_2));
                    break;
                case "Theme Color 3":
                    holder.checkbox_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_3));
                    break;
                case "Theme Color 4":
                    holder.checkbox_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_4));
                    break;
                case "Theme Color 5":
                    holder.checkbox_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_5));
                    break;
                case "Theme Color 6":
                    holder.checkbox_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_6));
                    break;
                case "Theme Color 7":
                    holder.checkbox_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_7));
                    break;
            }
        }

        if (tModels.getTask_is_important().equals("true")) {
            holder.important_uncheck.setVisibility(View.GONE);
            holder.important_check.setVisibility(View.VISIBLE);

            if (Util.IS_IMPORTANT) {
                isLayout = true;
                List<String> items = handler.getImportantTaskList();
                holder.list_name_tag.setText(items.get(position));
                holder.list_name_tag.setVisibility(View.VISIBLE);
            } else {
                holder.list_name_tag.setVisibility(View.GONE);
            }

            switch (themeColor) {
                case "Theme Color 1":
                    holder.important_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_1));
                    break;
                case "Theme Color 2":
                    holder.important_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_2));
                    break;
                case "Theme Color 3":
                    holder.important_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_3));
                    break;
                case "Theme Color 4":
                    holder.important_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_4));
                    break;
                case "Theme Color 5":
                    holder.important_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_5));
                    break;
                case "Theme Color 6":
                    holder.important_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_6));
                    break;
                case "Theme Color 7":
                    holder.important_check.setColorFilter(context.getResources()
                            .getColor(R.color.theme_color_7));
                    break;
            }
        }

        if (!tModels.getTask_is_my_day().equals("false")) {
            isLayout = true;
            holder.myDay_tag.setVisibility(View.VISIBLE);
            holder.myDay_text_tag.setVisibility(View.VISIBLE);

            if (Util.IS_MY_DAY) {
                List<String> items = handler.getMyDayTaskList();
                holder.list_name_tag.setText(items.get(position));
                holder.list_name_tag.setVisibility(View.VISIBLE);
            } else if (Util.IS_IMPORTANT) {
                List<String> items = handler.getImportantTaskList();
                holder.list_name_tag.setText(items.get(position));
                holder.list_name_tag.setVisibility(View.VISIBLE);
            } else {
                holder.list_name_tag.setVisibility(View.GONE);
            }
        } else {
            holder.myDay_tag.setVisibility(View.GONE);
            holder.myDay_text_tag.setVisibility(View.GONE);
        }

        if (!tModels.getTask_due_date().equals("")) {
            isLayout = true;
            Calendar calendar;
            SimpleDateFormat simpleDateFormat;

            calendar = Calendar.getInstance();

            simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
            String Today = simpleDateFormat.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
            String Tomorrow = simpleDateFormat.format(calendar.getTime());
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

            if (tModels.getTask_due_date().equals(Today)) {
                holder.dueDate_text_tag.setText("Today");
            } else if (tModels.getTask_due_date().equals(Tomorrow)) {
                holder.dueDate_text_tag.setText("Tomorrow");
            } else {
                holder.dueDate_text_tag.setText(tModels.getTask_due_date());
            }

            holder.dueDate_tag.setVisibility(View.VISIBLE);
            holder.dueDate_text_tag.setVisibility(View.VISIBLE);
        } else {
            holder.dueDate_text_tag.setText("");
            holder.dueDate_tag.setVisibility(View.GONE);
            holder.dueDate_text_tag.setVisibility(View.GONE);
        }

        if (!tModels.getTask_repeat().equals("")) {
            isLayout = true;
            holder.repeat_tag.setVisibility(View.VISIBLE);
        } else {
            holder.repeat_tag.setVisibility(View.GONE);
        }

        if (!tModels.getTask_reminder().equals("")) {
            isLayout = true;
            holder.reminder_tag.setVisibility(View.VISIBLE);
        } else {
            holder.reminder_tag.setVisibility(View.GONE);
        }

        if (Util.SEARCH_LIST_NAMES.size() != 0) {
            holder.list_name_tag.setText(Util.SEARCH_LIST_NAMES.get(position));
            holder.list_name_tag.setVisibility(View.VISIBLE);
            isLayout = true;
        }

        if (isLayout) {
            holder.linearLayout.setVisibility(View.VISIBLE);
        } else {
            holder.linearLayout.setVisibility(View.GONE);
        }

        holder.task_name.setText(tModels.getTask_title());
    }

    @Override
    public int getItemCount() {
        return taskModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        OnTaskListener onTaskListener;

        ImageButton checkbox_uncheck, checkbox_check;
        TextView task_name;
        ImageButton important_check, important_uncheck;
        LinearLayout linearLayout;

        ImageView myDay_tag, dueDate_tag, repeat_tag, reminder_tag;
        TextView myDay_text_tag, dueDate_text_tag, list_name_tag;

        public ViewHolder(@NonNull final View itemView, final OnTaskListener onTaskListener) {
            super(itemView);

            this.onTaskListener = onTaskListener;

            task_name = itemView.findViewById(R.id.row_task_title);
            checkbox_uncheck = itemView.findViewById(R.id.checkbox_uncheck);
            checkbox_check = itemView.findViewById(R.id.checkbox_check);
            important_check = itemView.findViewById(R.id.task_important_check);
            important_uncheck = itemView.findViewById(R.id.task_important_uncheck);

            myDay_tag = itemView.findViewById(R.id.task_myDay_tag);
            myDay_text_tag = itemView.findViewById(R.id.task_myDay_text_tag);
            list_name_tag = itemView.findViewById(R.id.task_list_name_text_tag);
            dueDate_tag = itemView.findViewById(R.id.task_dueDate_tag);
            dueDate_text_tag = itemView.findViewById(R.id.task_dueDate_text_tag);
            repeat_tag = itemView.findViewById(R.id.task_repeat_tag);
            reminder_tag = itemView.findViewById(R.id.task_reminder_tag);
            linearLayout = itemView.findViewById(R.id.layout_tags);

            checkbox_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTaskListener.onCheckboxCheck(getAdapterPosition(), task_name,
                            checkbox_check, checkbox_uncheck);
                }
            });

            checkbox_uncheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTaskListener.onCheckboxUnCheck(getAdapterPosition(),
                            task_name, checkbox_uncheck, checkbox_check);
                }
            });

            important_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTaskListener.onImportantCheck(getAdapterPosition(), important_check,
                            important_uncheck);
                }
            });

            important_uncheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTaskListener.onImportantUnCheck(getAdapterPosition(), important_check,
                            important_uncheck);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTaskListener.onTaskClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnTaskListener{
        void onTaskClick(int position);

        void onCheckboxUnCheck(int position, TextView task_name, ImageButton checkbox_uncheck,
                               ImageButton checkbox_check);

        void onCheckboxCheck(int position, TextView task_name, ImageButton checkbox_check,
                             ImageButton checkbox_uncheck);

        void onImportantUnCheck(int position, ImageButton important_check, ImageButton important_uncheck);

        void onImportantCheck(int position, ImageButton important_check, ImageButton important_uncheck);
    }
}
