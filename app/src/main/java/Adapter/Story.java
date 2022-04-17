package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Activity.AddStoryActivity;
import Activity.StoryActivity;
import Helper.FirebaseConfig;
import Helper.FirebaseUser;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class Story extends RecyclerView.Adapter<Story.MyViewHolder> {

    private List<Model.Story> storiesList;
    private Context context;

    public Story(List<Model.Story> storiesL, Context c) {
        this.storiesList = storiesL;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == 0){

            View view = LayoutInflater.from(context).inflate(R.layout.add_story_item, parent, false);
            return new MyViewHolder(view);
        }else{

            View view = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Model.Story story = storiesList.get(position);

        userInfo(holder, story.getUserId(), position);

        if(holder.getAdapterPosition() != 0){

            seenStory(holder, story.getUserId());
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.getAdapterPosition() == 0){

                    myStory(holder.tvAddStory, holder.civPlusStory, true);
                }else{

                    Intent intent = new Intent(context, StoryActivity.class);
                    intent.putExtra("userId", story.getUserId());
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return storiesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        //Stories
        private CircleImageView civStories, civStoriesSeen;
        private AppCompatTextView tvUsername;

        //My Story
        private CircleImageView civMyStory, civPlusStory;
        private AppCompatTextView tvAddStory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //Stories
            civStories = itemView.findViewById(R.id.civStories);
            civStoriesSeen = itemView.findViewById(R.id.civStoriesSeen);
            tvUsername = itemView.findViewById(R.id.tvUsernameStories);

            //MyStory
            civMyStory = itemView.findViewById(R.id.civMyStory);
            civPlusStory = itemView.findViewById(R.id.civPlusStory);
            tvAddStory = itemView.findViewById(R.id.tvAddStory);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if(position == 0){

            return 0;
        }
        return 1;
    }

    private void userInfo(MyViewHolder viewHolder, String userId, int pos){

        DatabaseReference reference = FirebaseConfig.getReference()
                .child("Users")
                .child(userId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if(user.getUrlImage() != null){

                    //Glide.with(context).load(user.getUrlImage()).into(viewHolder.civStoriesSeen);

                }
                if(pos != 0){

                    if(user.getUrlImage() != null){

                        Glide.with(context).load(user.getUrlImage()).into(viewHolder.civStories);
                    }
                    viewHolder.tvUsername.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myStory(AppCompatTextView tvAddStory, CircleImageView civPlusStory, final boolean click){


        String userId = FirebaseUser.getUserID();
        DatabaseReference reference = FirebaseConfig.getReference()
                .child("Story")
                .child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int count = 0;
                long timeCurrent = System.currentTimeMillis();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Model.Story story = ds.getValue(Model.Story.class);
                    if(timeCurrent > story.getTimeStart() && timeCurrent < story.getTimeEnd()){

                        count++;
                    }
                }

                if(click){
                    if(count > 0){

                        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Ver Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(context, StoryActivity.class);
                                intent.putExtra("userId", FirebaseUser.getUserID());
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Adicionar Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(context, AddStoryActivity.class);
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });

                        alertDialog.show();
                    }else{

                        Intent intent = new Intent(context, AddStoryActivity.class);
                        context.startActivity(intent);
                    }


                }else{

                    if(count > 0){

                        tvAddStory.setText("Meu Story");
                        civPlusStory.setVisibility(View.GONE);

                    }else{

                        tvAddStory.setText("Novo Story");
                        civPlusStory.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void seenStory(MyViewHolder viewHolder, String userId){

        String currentUserId = FirebaseUser.getUserID();

        DatabaseReference reference = FirebaseConfig.getReference()
                .child("Story")
                .child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int i = 0;
                for(DataSnapshot ds : snapshot.getChildren()){

                    if(!snapshot.child("views")
                    .child(currentUserId)
                    .exists() && System.currentTimeMillis() < ds.getValue(Model.Story.class).getTimeEnd()){

                        i++;
                    }
                }

                if(i > 0){

                    viewHolder.civStories.setVisibility(View.VISIBLE);
                    viewHolder.civStoriesSeen.setVisibility(View.GONE);
                }else{

                    viewHolder.civStories.setVisibility(View.GONE);
                    viewHolder.civStoriesSeen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
