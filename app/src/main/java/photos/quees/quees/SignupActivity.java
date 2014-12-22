package photos.quees.quees;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import photos.quees.services.FirebaseCommunication;
import photos.quees.services.SharedPreferenceService;


public class SignupActivity extends Activity {
    private FirebaseCommunication fc = new FirebaseCommunication();
    private SharedPreferenceService sps = new SharedPreferenceService();
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Firebase.setAndroidContext(this);

        sps.clearSharedPreferences(this);
        spinner = (ProgressBar)findViewById(R.id.progressBar2);
        spinner.setVisibility(View.GONE);
    }

    public void signUp(View V) {
        final Activity context = this;
        final Firebase ref = fc.getBaseRef();

        final EditText email = (EditText)findViewById(R.id.signup_email);
        final EditText password = (EditText)findViewById(R.id.signup_password);
        final Button btnLogin = (Button)findViewById(R.id.btn_signup);

        spinner.setVisibility(View.VISIBLE);
        btnLogin.setText("Signing you up...");

        ref.createUser(email.getText().toString(), password.getText().toString(), new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                btnLogin.setText("Logging you in...");
                ref.authWithPassword(email.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Map<String, Object> data = new HashMap<String, Object>(authData.getProviderData());
                        String email = data.get("email").toString();
                        String token = authData.getToken();

                        Firebase lsRef = fc.getRef("lastseens").push();

                        lsRef.child("email").setValue(email);
                        lsRef.child("lastSeen").setValue(0);

                        spinner.setVisibility(View.GONE);
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("token", token);

                        startActivity(intent);
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        spinner.setVisibility(View.GONE);
                        btnLogin.setText("Sign Up");

                        Toast toast = Toast.makeText(context, "Log in failed. Please try again.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                spinner.setVisibility(View.GONE);
                btnLogin.setText("Sign Up");

                Toast toast = Toast.makeText(context, "Invalid Email Address or Password format", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
