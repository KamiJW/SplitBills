package com.google.cloud.solutions.flexenv;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.cloud.solutions.flexenv.common.Friend;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    private RecyclerView rc;
    private FriendAdapter adapter;

    private List<String> Emails;
    private List<String> Debts;
    private List<Uri> Photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);

        Emails = getIntent().getStringArrayListExtra("Emails");
        Debts = getIntent().getStringArrayListExtra("Debts");
        List<String> photoUris = getIntent().getStringArrayListExtra("Photos");
        Photos = new ArrayList<>();
        for(int i = 0; i<Emails.size();i++){
            Photos.add(Uri.parse(photoUris.get(i)));
        }

        rc = (RecyclerView) findViewById(R.id.friend_list);
        LinearLayoutManager lm = new LinearLayoutManager(FriendActivity.this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        rc.setLayoutManager(lm);
        adapter = new FriendAdapter(Emails,Debts,Photos);
        rc.setAdapter(adapter);
    }




    private String filterEmail(String Email){
        String path = "";
        for(int i = 0;i<Email.length();i++){
            if(Email.charAt(i) != '.'){
                path += Email.charAt(i);
            }
        }
        return path;
    }

    public void addFriend(View v){
        PlayActivity.instance.addFriend(((EditText)findViewById(R.id.friend_add_name)).getText().toString());
    }


    public void backButton(View v){
        finish();
    }

    private class FriendAdapter extends RecyclerView.Adapter<FriendHolder> {

        private List<String> friends;
        private List<String> debts;
        private List<Uri> photos;

        public FriendAdapter(List<String> friendlist,List<String> debtlist, List<Uri> photolist){
            friends = friendlist;
            debts = debtlist;
            photos = photolist;
        }

        @Override
        public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            return new FriendHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FriendHolder holder, int position) {
            String friend = friends.get(position);
            String debt = debts.get(position);
            Uri photo = photos.get(position);
            holder.bind(friend, debt, photo);
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        public void deleteItem(int pos){
            friends.remove(pos);
            debts.remove(pos);
            photos.remove(pos);
            this.notifyItemRemoved(pos);
        }
    }

    class FriendHolder extends RecyclerView.ViewHolder{

        private Uri photouri;
        private String email;
        private String debt;

        private ImageView photoView;
        private TextView emailView;
        private TextView debtView;

        public FriendHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.friend_item, parent, false));
            //itemView.setOnClickListener(this);
            photoView = (ImageView) itemView.findViewById(R.id.friend_photo);
            emailView = (TextView) itemView.findViewById(R.id.friend_name);
            debtView = (TextView) itemView.findViewById(R.id.money);
        }

        public void bind(String aemail, String adebt, Uri image) {
            email = aemail;
            debt = adebt;
            photouri = image;
            if(image != null) {
                Picasso.with(FriendActivity.this).load(photouri).into(photoView);
            }
            emailView.setText(email);
            debtView.setText(debt);
        }
    }
}
