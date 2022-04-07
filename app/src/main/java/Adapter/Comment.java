package Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comment extends RecyclerView.Adapter<Comment.MyViewHolder> {

    private Context context;
    private List<Model.Comment> commentList;

    public Comment(Context c, List<Model.Comment> comments) {
        this.context = c;
        this.commentList = comments;
    }

    @NonNull
    @Override
    public Comment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Model.Comment comment = commentList.get(position);

        if(comment.getUrlImage() != null && !comment.getUrlImage().isEmpty()){

            Uri url = Uri.parse(comment.getUrlImage());
            Glide.with(context).load(url).into(holder.civProfile);

        }else{

            holder.civProfile.setImageResource(R.drawable.avatar);

        }

        holder.tvName.setText(comment.getName());
        holder.tvComment.setText(comment.getComment());

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView civProfile;
        private AppCompatTextView tvName;
        private AppCompatTextView tvComment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civProfile = itemView.findViewById(R.id.civProfileC);
            tvName = itemView.findViewById(R.id.tvNameC);
            tvComment = itemView.findViewById(R.id.tvComment);
        }
    }
}
