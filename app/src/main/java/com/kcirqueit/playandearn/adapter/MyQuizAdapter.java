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
import com.kcirqueit.playandearn.utility.DateUtility;
import com.kcirqueit.playandearn.utility.InternetConnection;
import com.kcirqueit.playandearn.viewModel.QuestionViewModel;
import com.kcirqueit.playandearn.viewModel.QuizViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyQuizAdapter extends RecyclerView.Adapter<MyQuizAdapter.ViewHolder> {

    private Context context;
    private List<Quiz> quizzes;
    private QuizViewModel quizViewModel;
    private QuestionViewModel questionViewModel;

    public MyQuizAdapter(Context context, List<Quiz> quizzes, QuizViewModel quizViewModel, QuestionViewModel questionViewModel) {
        this.context = context;
        this.quizzes = quizzes;
        this.quizViewModel = quizViewModel;
        this.questionViewModel = questionViewModel;
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

        Quiz quiz = quizzes.get(position);

        holder.quizName.setText(quiz.getQuizName());
        holder.totalMarksTv.setText("Marks : "+quiz.getTotalMarks()+"");
        holder.totalTimeTv.setText(DateUtility.milliToHour(Long.parseLong(quiz.getTimeLimit())) +" hours");
        holder.quizStatusTv.setText("Status : "+quiz.getStatus());

    }

    @Override
    public int getItemCount() {
        return quizzes.size();
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

        private DatabaseReference mQuestionRef = FirebaseDatabase.getInstance().getReference().child("Questions");

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent questionIntent = new Intent(context, MyQuestionActivity.class);
                    questionIntent.putExtra("quizId", quizzes.get(getAdapterPosition()).getId());
                    questionIntent.putExtra("totalQuestion", quizzes.get(getAdapterPosition()).getTotalQuestion()+"");
                    context.startActivity(questionIntent);
                }
            });



        }

        @OnClick(R.id.popup_iv)
        public void onPopupClick() {
            PopupMenu popupMenu = new PopupMenu(context, popupIv);
            Menu menu = popupMenu.getMenu();
            menu.add("Edit");
            menu.add("Delete");
            menu.add("View Participants");
            menu.add("Publish Quiz");
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getTitle().equals("Edit")) {

                        if (!isPublished()) {
                            if (InternetConnection.checkConnection(context)) {
                                Intent createQuizIntent = new Intent(context, CreateQuizActivity.class);
                                createQuizIntent.putExtra("requestForEdit", "true");
                                createQuizIntent.putExtra("quiz", quizzes.get(getAdapterPosition()));
                                context.startActivity(createQuizIntent);
                            } else {
                                InternetConnection.showNoInternetDialog(context);
                            }


                        } else {
                            Toast.makeText(context, "Quiz is already Publish. You Can't Edit the Quiz", Toast.LENGTH_SHORT).show();
                        }

                    } else if (item.getTitle().equals("Delete")) {

                        if (!isPublished()) {
                            String quizId = quizzes.get(getAdapterPosition()).getId();
                            if (InternetConnection.checkConnection(context)) {
                                showDeleteDialog(quizId);
                            } else {
                                InternetConnection.showNoInternetDialog(context);
                            }


                        }  else {
                            Toast.makeText(context, "Quiz is already Publish. You Can't Delete the Quiz", Toast.LENGTH_SHORT).show();
                        }

                    } else if (item.getTitle().equals("View Participants")){

                        // goto ViewParticipantActivity
                        gotoParticipanteActivity(quizzes.get(getAdapterPosition()));

                    } else if (item.getTitle().equals("Publish Quiz")) {
                        if (isPublished()) {
                            Toast.makeText(context, "Quiz is Already Published.", Toast.LENGTH_SHORT).show();
                        } else {

                            if (InternetConnection.checkConnection(context)) {
                                showPublishedDialog();
                            } else {
                                InternetConnection.showNoInternetDialog(context);
                            }
                        }
                    }

                    return true;
                }
            });

            popupMenu.show();
        }

        public boolean isPublished() {
            if (quizzes.get(getAdapterPosition()).getStatus().equals("un-published")) {
                return false;
            }
            return true;
        }

        public void showDeleteDialog(String quizId) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Warning");
            builder.setIcon(R.drawable.ic_warning_red);
            builder.setMessage("All questions will also be delete with this quiz.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    deleteQuiz(quizId);
                    dialog.dismiss();

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }

        public void showPublishedDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Warning");
            builder.setIcon(R.drawable.ic_warning_red);
            builder.setMessage("Please review your quiz and question information. After publish you can't edit or delete them.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Quiz quiz = quizzes.get(getAdapterPosition());
                    mQuestionRef.child(quiz.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!= null) {
                                int count = 0;
                                for (DataSnapshot questionSnapshot: dataSnapshot.getChildren()) {
                                    count ++;
                                }

                                if (count == quiz.getTotalQuestion()) {
                                    publishQuiz();
                                } else {
                                    Toast.makeText(context, "You added "+count+" question. You have to add total "+quiz.getTotalQuestion()+" Question.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "You did't create any question for this quiz. Plz add all question first.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("MyQuiz Adapter", databaseError.toException().toString());
                        }
                    });

                    dialog.dismiss();

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        public void deleteQuiz(String quizId) {
            // delete the quiz
            quizViewModel.deleteQuiz(quizzes.get(getAdapterPosition()))
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                //checking in question ref the quiz is exist or not
                                mQuestionRef.child(quizId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            // delete all question related to that quiz
                                            questionViewModel.deleteAllQuestion(quizId)
                                                    .addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if(task.isSuccessful()) {
                                                                Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(context, "Question is Not deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("My Quiz Adapter", databaseError.toException().toString());
                                    }
                                });
                            } else {
                                Toast.makeText(context, "Quiz is not deleted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

        public void publishQuiz() {
            Quiz quiz = quizzes.get(getAdapterPosition());
            quiz.setStatus("published");
            quizViewModel.updateQuiz(quiz).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(context, "Successfully published.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "Quiz is not published. Something wrong.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        public void gotoParticipanteActivity(Quiz quiz) {

            Intent participantIntent = new Intent(context, ViewParticipantActivity.class);
            participantIntent.putExtra("quiz", quiz);
            context.startActivity(participantIntent);

        }


    }
}
