package com.kcirqueit.playandearn.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.activity.ResultActivity;
import com.kcirqueit.playandearn.model.Participant;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.utility.DateUtility;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyResultAdapter extends RecyclerView.Adapter<MyResultAdapter.ViewHolder> {

    private Context context;
    private List<Quiz> quizList;

    public MyResultAdapter(Context context, List<Quiz> quizList) {
        this.context = context;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_quiz_item, parent,
                false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bookmarkIv.setVisibility(View.GONE);
        holder.creatorTv.setVisibility(View.GONE);

        Quiz quiz = quizList.get(position);

        holder.quizName.setText(quiz.getQuizName());
        holder.totalTimeTv.setText(DateUtility.milliToHour(Long.parseLong(quiz.getTimeLimit())) +" hours");
        holder.totalMarksTv.setText("Total Marks: " + quiz.getTotalMarks() + "");

    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.quiz_name_tv)
        TextView quizName;

        @BindView(R.id.creator_tv)
        public TextView creatorTv;

        @BindView(R.id.total_marks_tv)
        TextView totalMarksTv;

        @BindView(R.id.total_time_tv)
        TextView totalTimeTv;

        @BindView(R.id.start_quiz_iv)
        public ImageView bookmarkIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent resultIntent = new Intent(context, ResultActivity.class);
                    resultIntent.putExtra("quiz", quizList.get(getAdapterPosition()));
                    context.startActivity(resultIntent);
                }
            });

        }
    }
}
