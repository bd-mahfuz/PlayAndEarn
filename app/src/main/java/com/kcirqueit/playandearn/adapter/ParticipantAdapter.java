package com.kcirqueit.playandearn.adapter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.model.Participant;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private Context context;
    private List<Participant> participantList;

    private ParticipantListener participantListener;

    public ParticipantAdapter(Context context, List<Participant> participantList) {
        this.context = context;
        this.participantList = participantList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_participant,
                parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Participant participant = participantList.get(position);

        holder.scoreTv.setText("Score: "+participant.getScore()+"");
        holder.participantNameTv.setText(participant.getUserName());
        participantListener.getParticipator(holder.participantNameTv, participant.getId());

        holder.percentCPB.setProgressFormatter(new CircleProgressBar.ProgressFormatter() {
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

        @BindView(R.id.p_participant_name)
        TextView participantNameTv;

        @BindView(R.id.p_score_tv)
        TextView scoreTv;

        @BindView(R.id.p_percent_tv)
        CircleProgressBar percentCPB;


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
                    percentCPB.setProgress(progress);
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

    public interface ParticipantListener{

        void getParticipator(TextView textView, String userId);

    }


    public void setParticipantListener(ParticipantListener participantListener) {
        this.participantListener = participantListener;
    }
}
