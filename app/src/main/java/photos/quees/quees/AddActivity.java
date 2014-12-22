package photos.quees.quees;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import photos.quees.models.Question;
import photos.quees.services.BitmapManipulation;
import photos.quees.services.FirebaseCommunication;
import photos.quees.services.SharedPreferenceService;


public class AddActivity extends Activity {
    private BitmapManipulation bm = new BitmapManipulation();
    private FirebaseCommunication fc = new FirebaseCommunication();
    private SharedPreferenceService sps = new SharedPreferenceService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBarSubmit);
        spinner.setVisibility(View.GONE);

        Bundle extras = this.getIntent().getExtras();

        if (extras != null) {
            String imgPath = extras.getString("imgPath");

            if (imgPath != null) {
                try {
                    File f = new File(imgPath);
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    ImageView imgView = (ImageView) findViewById(R.id.img_view_upload);
                    imgView.setImageBitmap(b);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            getIntent().removeExtra("imgPath");
        }
    }


    public void submitQuestion(View V) {
        final ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBarSubmit);
        spinner.setVisibility(View.VISIBLE);

        ImageView imgView = (ImageView) findViewById(R.id.img_view_upload);
        EditText textView = (EditText) findViewById(R.id.edit_desc);

        Bitmap bitmap = bm.getBitmapFromImgView(imgView);
        bitmap = bm.resizeBitmap(bitmap, 640);

        String imgEncoded = bm.encodeBase64(bitmap);
        String textDesc = textView.getText().toString();

        Firebase qRef = fc.getRef("questions");
        String email = sps.getSharedPreferenceAsString(this, "currentEmail");

        Question q = new Question(textDesc, email, imgEncoded);
        q.setTimestamp(ServerValue.TIMESTAMP);

        qRef.push().setValue(q, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Context context = getApplicationContext();
                CharSequence errorText;

                if (firebaseError != null) {
                    errorText = "Data submission failed!";
                } else {
                    errorText = "Data submission succeeded!";
                    initAddActivity();
                }

                spinner.setVisibility(View.GONE);
                Toast toast = Toast.makeText(context, errorText, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void openCamera(View V) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void goBackToMainActivity(View V) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void initAddActivity() {
        ImageView imgView = (ImageView) findViewById(R.id.img_view_upload);
        EditText textView = (EditText) findViewById(R.id.edit_desc);

        imgView.setImageResource(R.drawable.image_not_available);
        textView.setText("");
    }
}
