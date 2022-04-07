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

import java.util.ArrayList;
import java.util.List;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUsers extends RecyclerView.Adapter<SearchUsers.MyViewHolder> {

    private ArrayList<User> usersList;
    private Context context;

    public SearchUsers(ArrayList<User> users, Context c) {
        this.usersList = users;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = usersList.get(position);
        holder.tvName.setText(user.getName());


        if(user.getUrlImage() != null){

            Uri url = Uri.parse(user.getUrlImage());
            Glide.with(context).load(url).into(holder.civProfile);
        }else{

            holder.civProfile.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView civProfile;
        private AppCompatTextView tvName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            civProfile = itemView.findViewById(R.id.civProfileUser);
            tvName = itemView.findViewById(R.id.tvNameUser);
        }
    }
}
