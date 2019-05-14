package com.kcirqueit.playandearn.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.activity.CreateQuizActivity;
import com.kcirqueit.playandearn.activity.MyQuestionActivity;
import com.kcirqueit.playandearn.activity.ViewParticipantActivity;
import com.kcirqueit.playandearn.model.Quiz;
import com.kcirqueit.playandearn.services.FirebaseApi;
import com.kcirqueit.playandearn.services.FirebaseClient;
import com.kcirqueit.playandearn.utility.DateUtility;
import com.kcirqueit.playandearn.utility.InternetConnection;
import com.kcirqueit.playandearn.viewModel.QuestionViewModel;
import com.kcirqueit.playandearn.viewModel.QuizViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyQuizAdapter extends RecyclerView.Adapter<MyQuizAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Quiz> quizzes;
    private List<Quiz> filteredQuizzes;

    private OnCreateQuizListener createQuizListener;

    public MyQuizAdapter(Context context, List<Quiz> quizzes) {
        this.context = context;
        this.quizzes = quizzes;
        this.filteredQuizzes = quizzes;
    }

    public void setCreateQuizListener(OnCreateQuizListener createQuizListener) {
        this.createQuizListener = createQuizListener;
    }

    @NonNull
    @Override
    public MyQuizAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_my_quiz_item,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyQuizAdapter.ViewHolder holder, int position) {

        Quiz quiz = filteredQuizzes.get(position);

        holder.quizName.setText(quiz.getQuizName());
        holder.totalMarksTv.setText("Marks : "+quiz.getTotalMarks()+"");

        //Log.d("onBindViewHolder: ", quiz.getTimeLimit());

        holder.totalTimeTv.setText(DateUtility.milliToHour(Long.parseLong(quiz.getTimeLimit())) +" hours");
        holder.quizStatusTv.setText("Status : "+quiz.getStatus());

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


        @BindView(R.id.total_marks_tv)
        TextView totalMarksTv;

        @BindView(R.id.total_time_tv)
        TextView totalTimeTv;

        @BindView(R.id.status_quiz_tv)
        TextView quizStatusTv;

        @BindView(R.id.popup_iv)
        ImageView popupIv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent questionIntent = new Intent(context, MyQuestionActivity.class);
                    questionIntent.putExtra("quizId", filteredQuizzes.get(getAdapterPosition()).getId());
                    questionIntent.putExtra("isPublished", filteredQuizzes.get(getAdapterPosition()).getStatus()+"");
                    questionIntent.putExtra("totalQuestion", filteredQuizzes.get(getAdapterPosition()).getTotalQuestion()+"");
                    context.startActivity(questionIntent);
                }
            });



        }

        @OnClick(R.id.popup_iv)
        public void onPopupClick() {

            if (createQuizListener != null) {
                createQuizListener.onPopupClick(quizzes.get(getAdapterPosition()), popupIv);
            }

        }

    }



    public interface OnCreateQuizListener {

       void onPopupClick(Quiz quiz, ImageView popUpIv);

    }
}
