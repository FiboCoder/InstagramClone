package Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Activity.UserProfile;
import Adapter.SearchUsers;
import Helper.FirebaseConfig;
import Helper.FirebaseUser;
import Helper.RecyclerItemClickListener;
import Model.User;

public class Search extends Fragment {

    private SearchView svSearchUser;
    private RecyclerView rvSearchUser;
    private SearchUsers adapter;

    private ArrayList<User> users;

    private DatabaseReference usersRef;

    private String loggedUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Initialize components
        svSearchUser = view.findViewById(R.id.svSearchUser);
        rvSearchUser = view.findViewById(R.id.rvSearchUsers);

        users = new ArrayList<>();
        usersRef = FirebaseConfig.getReference().child("Users");

        loggedUserId = FirebaseUser.getUserID();

        //Search View config
        svSearchUser.setQueryHint("Buscar usuários");
        svSearchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                String typedText = newText.toLowerCase();
                searchUsers(typedText);
                return true;
            }
        });



        //Adapter config
        adapter = new SearchUsers(users, getActivity());

        //Recycler View config

        rvSearchUser.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSearchUser.setHasFixedSize(true);
        rvSearchUser.setAdapter(adapter);

        //Click event on Recycler View

        rvSearchUser.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                rvSearchUser,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        User selectedUser = users.get(position);

                        Intent intent = new Intent(getActivity(), UserProfile.class);
                        intent.putExtra("selectedUser", selectedUser);
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

        return view;
    }

    private void searchUsers(String text){

        users.clear();

        if(text.length() > 0){

            Query query = usersRef.orderByChild("nameLowerCase")
                    .startAt(text)
                    .endAt(text + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    users.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){

                        User user = ds.getValue(User.class);
                        if(loggedUserId.equals(user.getId()))
                            continue;

                        users.add(user);

                    }

                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}