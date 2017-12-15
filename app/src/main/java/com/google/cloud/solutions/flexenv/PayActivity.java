package com.google.cloud.solutions.flexenv;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PayActivity extends AppCompatActivity {

    private RecyclerView rc;
    private FriendAdapter adapter;

    private List<String> Emails;
    private List<String> Debts;
    private List<Uri> Photos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payorrequest);

        Emails = getIntent().getStringArrayListExtra("Emails");
        Debts = getIntent().getStringArrayListExtra("Debts");
        List<String> photoUris = getIntent().getStringArrayListExtra("Photos");
        Photos = new ArrayList<>();
        for(int i = 0; i<Emails.size();i++){
            Photos.add(Uri.parse(photoUris.get(i)));
        }

        rc = (RecyclerView) findViewById(R.id.payandrequestlist);
        LinearLayoutManager lm = new LinearLayoutManager(PayActivity.this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        rc.setLayoutManager(lm);
        adapter = new FriendAdapter(Emails,Debts,Photos);
        rc.setAdapter(adapter);
    }

    public void doneClicked(View v) {
        String amount = ((EditText) findViewById(R.id.amount)).getText().toString();
        String type = ((Spinner) findViewById(R.id.pay_or_request)).getSelectedItem().toString();
        String purpose = ((Spinner) findViewById(R.id.purpose)).getSelectedItem().toString();
        ArrayList<String> purposelist = new ArrayList<String>();

        if (purpose.equals("Others")) {
            PlayActivity.instance.addTransaction(adapter.checklist, type, amount);
        } else {
            purposelist.add(purpose);
            PlayActivity.instance.addTransaction(purposelist, type, amount);
        }
        finish();
    }


    private class FriendAdapter extends RecyclerView.Adapter<FriendHolder> {

        private List<String> friends;
        private List<String> debts;
        private List<Uri> photos;
        public ArrayList<String> checklist;

        public FriendAdapter(List<String> friendlist,List<String> debtlist, List<Uri> photolist){
            friends = friendlist;
            debts = debtlist;
            photos = photolist;

            checklist = new ArrayList<>();
        }

        @Override
        public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            return new FriendHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FriendHolder holder, final int position) {
            String friend = friends.get(position);
            String debt = debts.get(position);
            Uri photo = photos.get(position);
            holder.bind(friend, debt, photo);

            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String checkedFriend = friends.get(position);
                    if(isChecked){
                        if(!checklist.contains(checkedFriend)){
                            checklist.add(checkedFriend);
                        }
                    }else{
                        if(checklist.contains(checkedFriend)){
                            checklist.remove(checkedFriend);
                        }
                    }
                }
            });
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
        public CheckBox cb;

        public FriendHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.payandrequestlist_item, parent, false));
            //itemView.setOnClickListener(this);
            photoView = (ImageView) itemView.findViewById(R.id.friend_photo);
            emailView = (TextView) itemView.findViewById(R.id.friend_name);
            debtView = (TextView) itemView.findViewById(R.id.money);
            cb = (CheckBox) itemView.findViewById(R.id.friend_check);
        }

        public void bind(String aemail, String adebt, Uri image) {
            email = aemail;
            debt = adebt;
            photouri = image;
            if(image != null) {
                Picasso.with(PayActivity.this).load(photouri).into(photoView);
            }
            emailView.setText(email);
            debtView.setText(debt);
        }
    }
}
