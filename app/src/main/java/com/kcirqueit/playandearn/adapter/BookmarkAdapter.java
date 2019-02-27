package com.kcirqueit.playandearn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kcirqueit.playandearn.R;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private Context context;
    private List<Map<String, String>> bookmarkList;

    private OnClickListener onClickListener;

    public BookmarkAdapter(Context context, List<Map<String, String>> bookmarkList) {
        this.context = context;
        this.bookmarkList = bookmarkList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_bookmark_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Map<String, String> map = bookmarkList.get(position);
        String quizId = map.get("quizId");

        holder.quizFirstLatterTv.setText(map.get("quizName").charAt(0)+"");
        holder.quizNameTv.setText(map.get("quizName"));

    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.quiz_first_latter_tv)
        TextView quizFirstLatterTv;

        @BindView(R.id.b_quiz_name_tv)
        TextView quizNameTv;

        @BindView(R.id.delete_bookmark_iv)
        ImageView bookmarkIv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onItemClick(bookmarkList.get(getAdapterPosition()).get("quizId"));
                }
            });

        }

        @OnClick(R.id.delete_bookmark_iv)
        public void onBookmarkClick() {

            onClickListener.deleteBookmarkd(bookmarkList.get(getAdapterPosition()).get("quizId"));

        }
    }

    public interface OnClickListener{

        void onItemClick(String quizId);

        void deleteBookmarkd(String quiId);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
