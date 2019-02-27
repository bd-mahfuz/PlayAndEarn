package com.kcirqueit.playandearn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.utility.DateUtility;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Quiz> quizzes;
    private List<Quiz> filteredQuizzes;

    private OnItemClickListener itemClickListener;


    public QuizAdapter(Context context, List<Quiz> quizzes) {
        this.context = context;
        this.quizzes = quizzes;
        this.filteredQuizzes = quizzes;
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

        Quiz quiz = filteredQuizzes.get(position);

        holder.quizName.setText(quiz.getQuizName());
        holder.totalTimeTv.setText(DateUtility.milliToHour(Long.parseLong(quiz.getTimeLimit())) +" hours");
        holder.totalMarksTv.setText("Total Marks: " + quiz.getTotalMarks() + "");
        itemClickListener.getCreatorName(quiz.getUserId(), holder.creatorTv);

        itemClickListener.getBookmarkData(quiz, holder);

    }

    @Override
    public int getItemCount() {
        return filteredQuizzes.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();
                List<Quiz> tempList = new ArrayList<>();
                if (query.isEmpty()) {
                    filteredQuizzes = quizzes;
                } else {
                    for (Quiz quiz : quizzes) {
                        if (quiz.getQuizName().toLowerCase().contains(query.toLowerCase())) {
                            tempList.add(quiz);
                        }
                    }
                    filteredQuizzes = tempList;
                }

                FilterResults results = new FilterResults();
                results.values = filteredQuizzes;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                filteredQuizzes = (List<Quiz>) results.values;
                notifyDataSetChanged();

            }
        };
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

                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(quizzes.get(getAdapterPosition()));
                    }
                }
            });

        }

        @OnClick(R.id.start_quiz_iv)
        public void onBookMarkedClick() {
            Quiz quiz = quizzes.get(getAdapterPosition());
            if (itemClickListener != null) {
                itemClickListener.onBookmarkClick(quiz);
            }

        }
    }


    public interface OnItemClickListener{
        void onBookmarkClick(Quiz quiz);

        void onItemClick(Quiz quiz);

        void getBookmarkData(Quiz quiz, ViewHolder viewHolder);

        void getCreatorName(String quizId, TextView creatorTv);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
