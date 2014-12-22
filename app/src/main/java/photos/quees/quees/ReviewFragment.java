package photos.quees.quees;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import photos.quees.models.Question;
import photos.quees.services.FirebaseCommunication;
import photos.quees.services.SharedPreferenceService;
import photos.quees.services.StringSimilarity;

public class ReviewFragment extends ListFragment {
    private ArrayList<Question> qs = new ArrayList<Question>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_review, container, false);
        final ProgressBar spinner = (ProgressBar) rootView.findViewById(R.id.progressBarReview);

        FirebaseCommunication fc = new FirebaseCommunication();
        SharedPreferenceService sps = new SharedPreferenceService();

        spinner.setVisibility(View.VISIBLE);
        Firebase.setAndroidContext(this.getActivity());
        Firebase qRef = fc.getRef("questions");
        final Firebase gRef = fc.getRef("guesses");
        String email = sps.getSharedPreferenceAsString(getActivity(), "currentEmail");

        Query queryRef = qRef.orderByChild("email").equalTo(email);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                qs.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, String> value = (Map<String, String>) child.getValue();
                    Map<String, Long> tValue = (Map<String, Long>) child.getValue();

                    final Question q = new Question(value.get("desc"), value.get("email"), value.get("image"));
                    q.unixTime = tValue.get("timestamp");
                    q.id = child.getKey();

                    Query guessRef = gRef.orderByChild("qId").equalTo(q.id);
                    guessRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String bestGuess = "";
                            String bestGuessEmail = "";
                            double bestGuessScore = 0.0;
                            StringSimilarity ss = new StringSimilarity();

                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Map<String, String> value = (Map<String, String>) child.getValue();
                                double guessScore = ss.similarity(q.desc, value.get("desc"));

                                if (guessScore > bestGuessScore) {
                                    bestGuessScore = guessScore;
                                    bestGuess = value.get("desc");
                                    bestGuessEmail = value.get("email");
                                }
                            }

                            q.bestGuess = bestGuess;
                            q.bestGuessEmail = bestGuessEmail;
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                    qs.add(q);
                }

                if (qs.size() == 0) {
                   TextView noQuestions = (TextView)rootView.findViewById(R.id.text_no_questions);
                   noQuestions.setVisibility(View.VISIBLE);
                }

                spinner.setVisibility(View.GONE);
                ListView listview = (ListView) rootView.findViewById(android.R.id.list);
                ReviewListAdapter adapter = new ReviewListAdapter(getActivity(), R.id.list_item, qs);
                listview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        return rootView;
    }
}