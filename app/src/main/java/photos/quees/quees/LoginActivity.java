package photos.quees.quees;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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


public class LoginActivity extends Activity {
    private FirebaseCommunication fc = new FirebaseCommunication();
    private SharedPreferenceService sps = new SharedPreferenceService();
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(this);

        sps.clearSharedPreferences(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
    }

    public void logIn(View view) {
        final Activity context = this;

        final Button btnLogin = (Button)findViewById(R.id.btn_login);
        EditText email = (EditText)findViewById(R.id.login_email);
        EditText password = (EditText)findViewById(R.id.login_password);

        spinner.setVisibility(View.VISIBLE);
        btnLogin.setText("Logging in...");

        Firebase ref = fc.getBaseRef();
        ref.authWithPassword(email.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Map<String, Object> data = new HashMap<String, Object>(authData.getProviderData());
                String email = data.get("email").toString();
                String token = authData.getToken();

                spinner.setVisibility(View.GONE);
                btnLogin.setText("Log In");
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("token", token);

                startActivity(intent);
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                spinner.setVisibility(View.GONE);
                btnLogin.setText("Log In");

                Toast toast = Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}