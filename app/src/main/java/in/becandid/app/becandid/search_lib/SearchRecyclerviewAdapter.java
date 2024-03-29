package in.becandid.app.becandid.search_lib;

import android.content.Context;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.becandid.app.becandid.ui.group.CommunityGroupPojo;
import in.becandid.app.becandid.ui.group.UserGroupDetails;
import in.becandid.app.becandid.R;
import in.becandid.app.becandid.ui.base.Constants;

/**
 * Created by Harish on 9/1/2016.
 */
public class SearchRecyclerviewAdapter extends RecyclerView.Adapter<SearchRecyclerviewAdapter.PersonViewHolder> {

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        private TextView username;
        private ImageView list_item_groups_avatar;
        private TextView category;
        private TextView timeStamp;
        private TextView postMessage;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            username = (TextView) itemView.findViewById(R.id.list_item_groups_userNickName);
            list_item_groups_avatar = (ImageView) itemView.findViewById(R.id.list_item_groups_avatar);
            category = (TextView) itemView.findViewById(R.id.list_item_posts_category);
            timeStamp = (TextView) itemView.findViewById(R.id.group_members_search);
            postMessage = (TextView) itemView.findViewById(R.id.group_members_in_group);
        }
    }

    List<CommunityGroupPojo> persons;
    Context context;

    SearchRecyclerviewAdapter(List<CommunityGroupPojo> persons, Context context){
        this.persons = persons;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.username.setText(persons.get(i).getGroupName());

        Glide.with(context).asDrawable()
                .load(persons.get(i).getGroupImageUrl())
                .error(R.drawable.user)
                .placeholder(R.drawable.user)
                .into(personViewHolder.list_item_groups_avatar);


       // personViewHolder.list_item_groups_avatar.setImageURI(persons.get(i).getGroupImageUrl());
        personViewHolder.category.setText(persons.get(i).getCategoryName());
        personViewHolder.timeStamp.setText(String.valueOf(persons.get(i).getUsersInGroup() + " Members" + " " +
                persons.get(i).getPostsInsideGroups() + " Posts" ));
        personViewHolder.postMessage.setText(persons.get(i).getGroupDescription());

        personViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UserGroupDetails.class);
                intent.putExtra(Constants.GROUPID, persons.get(i).getGroupId());
                view.getContext().startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return persons.size();
    }
}
