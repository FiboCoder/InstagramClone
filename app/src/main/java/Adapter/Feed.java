package Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import Activity.Comments;
import Helper.FirebaseConfig;
import Helper.FirebaseUser;
import Model.PostsLikes;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class Feed extends RecyclerView.Adapter<Feed.MyViewHolder> {

    private Context context;
    private List<Model.Feed> feedList;

    public Feed(Context c, List<Model.Feed> feed) {
        this.context = c;
        this.feedList = feed;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Model.Feed feed = feedList.get(position);
        User loggedUser = FirebaseUser.getUserData();

        if(feed.getUserImage() != null){

            Uri urlProfile = Uri.parse(feed.getUserImage());
            Glide.with(context).load(urlProfile).into(holder.civProfile);
        }

        if(feed.getImage() != null){

            Uri urlPost = Uri.parse(feed.getImage());
            Glide.with(context).load(urlPost).into(holder.ivPost);

        }

        holder.tvName.setText(feed.getName());
        holder.tvDescription.setText(feed.getDescription());

        holder.ivViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Comments.class);
                intent.putExtra("feedId", feed.getId());
                context.startActivity(intent);
            }
        });

        DatabaseReference likesRef = FirebaseConfig.getReference()
                .child("Posts-Likes")
                .child(feed.getId());

        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int qttLikes = 0;
                if(snapshot.hasChild("qttLikes")){

                    PostsLikes postsLikes = snapshot.getValue(PostsLikes.class);
                    qttLikes = postsLikes.getQttLikes();

                }

                //Verify is liked
                if(snapshot.hasChild(loggedUser.getId())){

                    holder.btLike.setLiked(true);

                }else{

                    holder.btLike.setLiked(false);


                }

                //Config object to save Posts-Likes
                PostsLikes like = new PostsLikes();
                like.setFeed(feed);
                like.setUser(loggedUser);
                like.setQttLikes(qttLikes);

                //Config Like Button and set likes quantity
                holder.btLike.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {

                        like.save();
                        holder.tvLikesQtt.setText(like.getQttLikes() + " curtidas");

                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {

                        like.removeLike();
                        holder.tvLikesQtt.setText(like.getQttLikes() + " curtidas");


                    }
                });

                holder.tvLikesQtt.setText(like.getQttLikes() + " curtidas");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView civProfile;
        private AppCompatImageView ivPost;
        private LikeButton btLike;
        private AppCompatImageView ivViewComments;
        private AppCompatTextView tvName, tvDescription, tvLikesQtt;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civProfile = itemView.findViewById(R.id.civProfileVP);
            ivPost = itemView.findViewById(R.id.ivPostVP);
            btLike = itemView.findViewById(R.id.btLike);
            ivViewComments = itemView.findViewById(R.id.ivViewComments);
            tvName = itemView.findViewById(R.id.tvNameVP);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLikesQtt = itemView.findViewById(R.id.tvLikesVP);
        }
    }
}
