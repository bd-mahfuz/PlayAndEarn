package com.kcirqueit.playandearn.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.model.Participant;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {


    private List<Participant> participantList;
    private Context context;

    public AchievementAdapter(List<Participant> participantList, Context context) {
        this.participantList = participantList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.achivement_layout,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Participant participant = participantList.get(position);
        holder.quizNameTv.setText("Quiz Name: " + participantList.get(position).getQuizName());

        holder.mCustomProgressBar.setProgressFormatter(new CircleProgressBar.ProgressFormatter() {
            @Override
            public CharSequence format(int progress, int max) {
                return progress +" %";
            }
        });

        holder.simulateProgress(holder.getParcentageValue(participant.getScore(), participant.getTotalMarks()));

    }

    @Override
    public int getItemCount() {
        return participantList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.trophy_iv)
        ImageView trophyIv;

        @BindView(R.id.a_quiz_name_tv)
        TextView quizNameTv;

        @BindView(R.id.a_score_tv)
        CircleProgressBar mCustomProgressBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        private void simulateProgress(int max) {
            ValueAnimator animator = ValueAnimator.ofInt(0, max);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int progress = (int) animation.getAnimatedValue();
                    mCustomProgressBar.setProgress(progress);
                }
            });

            animator.setDuration(1000);
            animator.start();
        }

        public int getParcentageValue(int score, int totalMarks) {
            int pa = (score * 100) / totalMarks;
            return pa;

        }
    }
}
