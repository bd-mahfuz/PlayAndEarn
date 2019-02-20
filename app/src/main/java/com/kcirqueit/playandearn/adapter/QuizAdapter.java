package com.kcirqueit.playandearn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.model.Quiz;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Quiz> quizzes;

    public QuizAdapter(Context context, List<Quiz> quizzes) {
        this.context = context;
        this.quizzes = quizzes;
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

        Quiz quiz = quizzes.get(position);

        holder.quizName.setText(quiz.getQuizName());
        holder.totalTimeTv.setText(quiz.getTimeLimit()+"");
        holder.totalMarksTv.setText("Total Marks: "+quiz.getTotalMarks()+"");
        holder.creatorTv.setText("Creator: "+quiz.getUserId());

    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.quiz_name_tv)
        TextView quizName;

        @BindView(R.id.creator_tv)
        TextView creatorTv;

        @BindView(R.id.total_marks_tv)
        TextView totalMarksTv;

        @BindView(R.id.total_time_tv)
        TextView totalTimeTv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }
    }


    public void updateList(List<Quiz> quizzes) {

        this.quizzes = new ArrayList<>();
        this.quizzes.addAll(quizzes);
        notifyDataSetChanged();

    }
}
