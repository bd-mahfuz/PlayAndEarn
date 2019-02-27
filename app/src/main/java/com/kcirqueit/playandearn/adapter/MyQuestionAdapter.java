package com.kcirqueit.playandearn.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kcirqueit.playandearn.R;
import com.kcirqueit.playandearn.activity.AddQuestionActivity;
import com.kcirqueit.playandearn.model.Question;
import com.kcirqueit.playandearn.viewModel.QuestionViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyQuestionAdapter extends RecyclerView.Adapter<MyQuestionAdapter.ViweHolder> {

    private Context context;
    private List<Question> questionList;
    private QuestionViewModel questionViewModel;

    public MyQuestionAdapter(Context context, List<Question> questionList, QuestionViewModel questionViewModel) {
        this.context = context;
        this.questionList = questionList;
        this.questionViewModel = questionViewModel;
    }

    @NonNull
    @Override
    public ViweHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_question_item,
                parent, false);

        return new ViweHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViweHolder holder, int position) {

        Question question = questionList.get(position);

        holder.questionTv.setText((position+1)+". "+question.getQuestionTitle());

        if (question.getCorrectAns() == 0) {
            holder.option1.setChecked(true);

            holder.option1.setText(question.getOptions().get(0));
            holder.option2.setText(question.getOptions().get(1));
            holder.option3.setText(question.getOptions().get(2));
            holder.option4.setText(question.getOptions().get(3));

            holder.option2.setEnabled(false);
            holder.option3.setEnabled(false);
            holder.option4.setEnabled(false);

        } else if(question.getCorrectAns() == 1) {
            holder.option2.setChecked(true);

            holder.option1.setText(question.getOptions().get(0));
            holder.option2.setText(question.getOptions().get(1));
            holder.option3.setText(question.getOptions().get(2));
            holder.option4.setText(question.getOptions().get(3));

            holder.option1.setEnabled(false);
            holder.option3.setEnabled(false);
            holder.option4.setEnabled(false);

        } else if(question.getCorrectAns() == 2) {
            holder.option3.setChecked(true);

            holder.option1.setText(question.getOptions().get(0));
            holder.option2.setText(question.getOptions().get(1));
            holder.option3.setText(question.getOptions().get(2));
            holder.option4.setText(question.getOptions().get(3));

            holder.option1.setEnabled(false);
            holder.option2.setEnabled(false);
            holder.option4.setEnabled(false);

        } else if(question.getCorrectAns() == 3) {
            holder.option4.setChecked(true);

            holder.option1.setText(question.getOptions().get(0));
            holder.option2.setText(question.getOptions().get(1));
            holder.option3.setText(question.getOptions().get(2));
            holder.option4.setText(question.getOptions().get(3));

            holder.option1.setEnabled(false);
            holder.option2.setEnabled(false);
            holder.option3.setEnabled(false);
        }


    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class ViweHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.questionTv)
        TextView questionTv;

        @BindView(R.id.showOption1)
        RadioButton option1;

        @BindView(R.id.showOption2)
        RadioButton option2;

        @BindView(R.id.showOption3)
        RadioButton option3;

        @BindView(R.id.showOption4)
        RadioButton option4;

        @BindView(R.id.question_popup_menu)
        ImageView popupIv;



        public ViweHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }

        @OnClick(R.id.question_popup_menu)
        public void onPopupClick() {
            PopupMenu popupMenu = new PopupMenu(context, popupIv);
            Menu menu = popupMenu.getMenu();
            menu.add("Edit");
            menu.add("Delete");
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getTitle().equals("Edit")) {

                        Intent addQuestionIntent = new Intent(context, AddQuestionActivity.class);
                        addQuestionIntent.putExtra("requestForEdit", "true");
                        addQuestionIntent.putExtra("question", questionList.get(getAdapterPosition()));
                        context.startActivity(addQuestionIntent);

                    } else if (item.getTitle().equals("Delete")) {
                        showAlertDialog(); // and perform delete operation
                    }
                    return true;
                }
            });

            popupMenu.show();
        }


        public void showAlertDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Warning");
            builder.setIcon(R.drawable.ic_warning_red);
            builder.setMessage("Are are sure to delete this question?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    deleteQuestion();

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

        public void deleteQuestion() {
            questionViewModel.deleteQeustion(questionList.get(getAdapterPosition()))
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Question is deleted successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Question is not deleted! Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }


}
