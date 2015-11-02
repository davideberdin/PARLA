package davideberdin.goofing.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import davideberdin.goofing.Listening;
import davideberdin.goofing.R;
import davideberdin.goofing.utilities.Constants;


public class ListenUser extends Fragment
{
    private View listenUserFragment;
    private ListView userListView = null;
    private View previouslyUserSelectedItem = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.listenUserFragment = inflater.inflate(R.layout.fragment_listen_user, container, false);

        this.userListView = (ListView) this.listenUserFragment.findViewById(R.id.userListView);

        // Fill list view user sentences
        ArrayList<String> userSentences = fillUserList();
        ArrayAdapter userAdapter = new ArrayAdapter(this.listenUserFragment.getContext(), android.R.layout.simple_list_item_1, userSentences);
        this.userListView.setAdapter(userAdapter);
        this.userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previouslyUserSelectedItem != null)
                    previouslyUserSelectedItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                view.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                previouslyUserSelectedItem = view;
                Listening.selectedSentence = (String)parent.getItemAtPosition(position);
                Listening.selectedPhonetic = Constants.nativePhonetics[position];
            }
        });

        return this.listenUserFragment;
    }

    private ArrayList<String> fillUserList()
    {
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < Constants.userSentences.length; ++i) {
            list.add(Constants.nativeSentences[i]);
        }

        return list;
    }
}