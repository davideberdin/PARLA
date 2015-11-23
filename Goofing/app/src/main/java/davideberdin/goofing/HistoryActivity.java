package davideberdin.goofing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;

import davideberdin.goofing.controllers.CardTuple;
import davideberdin.goofing.controllers.HistoryCard;
import davideberdin.goofing.controllers.HistoryCardsAdapter;
import davideberdin.goofing.controllers.HistoryTrendAdapter;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.UserLocalStore;

public class HistoryActivity extends AppCompatActivity {

    // vowels history
    private ArrayList<HistoryCard> historyCardsAdapterList;
    private HistoryCardsAdapter historyCardsAdapter;
    private RecyclerView recCardsList;

    // vowels trend
    private ArrayList<HistoryCard> historyTrendAdapterList;
    private HistoryTrendAdapter historyTrendAdapter;
    private RecyclerView recTrendList;

    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        UserLocalStore localStore = new UserLocalStore(this);
        this.loggedUser = localStore.getLoggedUser();

        // vowels history
        this.recCardsList = (RecyclerView) findViewById(R.id.cardList);
        this.recCardsList.setHasFixedSize(true);

        this.historyCardsAdapterList = new ArrayList<>();
        historyCardsAdapter = new HistoryCardsAdapter(this.historyCardsAdapterList);

        LinearLayoutManager cardsLlm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.recCardsList.setLayoutManager(cardsLlm);
        this.recCardsList.setAdapter(historyCardsAdapter);

        // vowels trend
        this.recTrendList = (RecyclerView) findViewById(R.id.cardListTrend);
        this.recTrendList.setHasFixedSize(true);

        this.historyTrendAdapterList = new ArrayList<>();
        historyTrendAdapter = new HistoryTrendAdapter(this.historyTrendAdapterList);

        LinearLayoutManager trendLlm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.recTrendList.setLayoutManager(trendLlm);
        this.recTrendList.setAdapter(historyTrendAdapter);

        // request for fetching history data
        int indexSentence = Arrays.asList(Constants.nativeSentences).indexOf(this.loggedUser.GetCurrentSentence());
        String vowels = Constants.nativeStressPhonemes[indexSentence];

        ServerRequest historyRequest = new ServerRequest(this, Constants.FETCHING_HISTORY_TITLE, Constants.FETCHING_HISTORY);
        historyRequest.fetchHistoryDataInBackground(this.loggedUser.GetUsername(),
                this.loggedUser.GetCurrentSentence(), vowels,
                new GetCallback() {
                    @Override
                    public void done(Object... params) {
                        //region vowels history here
                        ArrayList<CardTuple> tempHistory = (ArrayList<CardTuple>) params[0];
                        ArrayList<CardTuple> historyCards = new ArrayList<CardTuple>();
                        for (CardTuple ct : tempHistory)
                            historyCards.add(ct);

                        // create listener for cards history
                        createList(historyCards, false);
                        historyCardsAdapter.notifyDataSetChanged();
                        //endregion
                        //region vowels trend here
                        ArrayList<CardTuple> tempTrend = (ArrayList<CardTuple>) params[1];
                        ArrayList<CardTuple> historyTrendCards = new ArrayList<CardTuple>();
                        for (CardTuple ct : tempTrend)
                            historyTrendCards.add(ct);

                        // create listener for cards trend
                        createList(historyTrendCards, true);
                        historyTrendAdapter.notifyDataSetChanged();
                        //endregion
                    }
                });

        // back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("deprecation")
    private void createList(ArrayList<CardTuple> historyData, boolean isTrend) {

        for (CardTuple data : historyData) {
            HistoryCard hc = new HistoryCard();

            hc.setCardId(data.getId());
            hc.setCardDate(data.getDate());
            hc.setImageByteArray(data.getImage());

            if (!isTrend)
                historyCardsAdapterList.add(hc);
            else
                historyTrendAdapterList.add(hc);
        }
    }
}