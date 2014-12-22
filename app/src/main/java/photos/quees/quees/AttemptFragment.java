package photos.quees.quees;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import photos.quees.models.Guess;
import photos.quees.services.BitmapManipulation;
import photos.quees.services.FirebaseCommunication;
import photos.quees.services.SharedPreferenceService;

public class AttemptFragment extends Fragment {
    private BitmapManipulation bm = new BitmapManipulation();
    private FirebaseCommunication fc = new FirebaseCommunication();
    private SharedPreferenceService sps = new SharedPreferenceService();
    private String currentUserEmail = "";
    private Activity activity;
    private View view;
    private ProgressBar spinner;
    private Guess currentGuess;
    public Long currentLastSeen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attempt, container, false);
        spinner = (ProgressBar) rootView.findViewById(R.id.progressBarAttempt);
        activity = this.getActivity();
        view = rootView;

        currentUserEmail = sps.getSharedPreferenceAsString(activity, "currentEmail");
        Query lsQuery = fc.getRef("lastseens").orderByChild("email").equalTo(currentUserEmail).limitToLast(1);
        lsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, Long> tValue = (Map<String, Long>) child.getValue();
                    Long lastSeen = tValue.get("lastSeen");
                    currentLastSeen = lastSeen;
                    getQuestion(currentLastSeen);
                }

                if (!dataSnapshot.hasChildren()) {
                    getQuestion(new Long(1));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                getQuestion(new Long(1));
            }
        });

        return rootView;
    }

    public void getQuestion(Long lastSeen) {
        Firebase gRef = fc.getRef("questions");
        Query gQuery = gRef.orderByChild("timestamp").startAt(lastSeen).limitToFirst(1);
        final RelativeLayout guessContainer = (RelativeLayout)view.findViewById(R.id.guess_container);
        final TextView noMore = (TextView)view.findViewById(R.id.text_no_more);

        spinner.setVisibility(View.VISIBLE);
        guessContainer.setVisibility(View.GONE);
        noMore.setVisibility(View.GONE);

        gQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, String> value = (Map<String, String>) child.getValue();
                    Map<String, Long> tValue = (Map<String, Long>) child.getValue();

                    Long timestamp = tValue.get("timestamp");
                    String email = sps.getSharedPreferenceAsString(activity, "currentEmail");

                    if (value.get("email").equals(email)) {
                        getQuestion(timestamp + 1);
                        return;
                    } else {
                        currentGuess = new Guess();
                        currentGuess.qId = child.getKey();
                        currentLastSeen = tValue.get("timestamp");

                        ImageView qImg = (ImageView)view.findViewById(R.id.q_img);
                        Bitmap bitmap = bm.decodeBase64(value.get("image"));

                        if (bitmap != null) {
                            qImg.setImageBitmap(bitmap);
                        }

                        spinner.setVisibility(View.GONE);
                        guessContainer.setVisibility(View.VISIBLE);
                    }
                }

                if (!dataSnapshot.hasChildren()) {
                    spinner.setVisibility(View.GONE);
                    noMore.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }


    public void submitGuess() {
        Firebase gRef = fc.getRef("guesses");

        spinner.setVisibility(View.VISIBLE);

        final EditText guessView = (EditText) view.findViewById(R.id.q_answer);
        currentGuess.desc = guessView.getText().toString();
        currentGuess.timestamp = ServerValue.TIMESTAMP;
        currentGuess.email = currentUserEmail;

        gRef.push().setValue(currentGuess, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                CharSequence toastText;

                if (firebaseError != null) {
                    toastText = "Your guess could not be submitted at this time.";
                } else {
                    toastText = "Guess submitted!";
                    guessView.setText("");
                    getQuestion(currentLastSeen + 1);
                }

                spinner.setVisibility(View.GONE);
                Toast toast = Toast.makeText(activity, toastText, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}