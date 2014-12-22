package photos.quees.quees;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import photos.quees.services.FastAnimation;

public class HomeScreen extends Activity {
    FastAnimation fastAnimation;
    private static final int[] IMAGE_RESOURCES = {
        R.drawable.walle000, R.drawable.walle001, R.drawable.walle002, R.drawable.walle003, R.drawable.walle004, R.drawable.walle005, R.drawable.walle006, R.drawable.walle007, R.drawable.walle008, R.drawable.walle009, R.drawable.walle010, R.drawable.walle011, R.drawable.walle012, R.drawable.walle013, R.drawable.walle014, R.drawable.walle015, R.drawable.walle016, R.drawable.walle017, R.drawable.walle018, R.drawable.walle019, R.drawable.walle020, R.drawable.walle021, R.drawable.walle022, R.drawable.walle023, R.drawable.walle024, R.drawable.walle025, R.drawable.walle026, R.drawable.walle027, R.drawable.walle028, R.drawable.walle029, R.drawable.walle030, R.drawable.walle031, R.drawable.walle032, R.drawable.walle033, R.drawable.walle034, R.drawable.walle035, R.drawable.walle036, R.drawable.walle037, R.drawable.walle038, R.drawable.walle039, R.drawable.walle040, R.drawable.walle041, R.drawable.walle042, R.drawable.walle043, R.drawable.walle044, R.drawable.walle045, R.drawable.walle046, R.drawable.walle047, R.drawable.walle048, R.drawable.walle049, R.drawable.walle050, R.drawable.walle051, R.drawable.walle052, R.drawable.walle053, R.drawable.walle054, R.drawable.walle055, R.drawable.walle056, R.drawable.walle057, R.drawable.walle058, R.drawable.walle059, R.drawable.walle060, R.drawable.walle061, R.drawable.walle062, R.drawable.walle063, R.drawable.walle064, R.drawable.walle065, R.drawable.walle066, R.drawable.walle067, R.drawable.walle068, R.drawable.walle069, R.drawable.walle070, R.drawable.walle071, R.drawable.walle072, R.drawable.walle073, R.drawable.walle074, R.drawable.walle075, R.drawable.walle076, R.drawable.walle077, R.drawable.walle078, R.drawable.walle079, R.drawable.walle080, R.drawable.walle081, R.drawable.walle082, R.drawable.walle083, R.drawable.walle084, R.drawable.walle085, R.drawable.walle086, R.drawable.walle087, R.drawable.walle088, R.drawable.walle089, R.drawable.walle090, R.drawable.walle091, R.drawable.walle092, R.drawable.walle093, R.drawable.walle094, R.drawable.walle095, R.drawable.walle096, R.drawable.walle097, R.drawable.walle098, R.drawable.walle099, R.drawable.walle100, R.drawable.walle101, R.drawable.walle102, R.drawable.walle103, R.drawable.walle104, R.drawable.walle105, R.drawable.walle106, R.drawable.walle107, R.drawable.walle108, R.drawable.walle109, R.drawable.walle110, R.drawable.walle111, R.drawable.walle112, R.drawable.walle113, R.drawable.walle114, R.drawable.walle115, R.drawable.walle116, R.drawable.walle117, R.drawable.walle118, R.drawable.walle119, R.drawable.walle120, R.drawable.walle121, R.drawable.walle122, R.drawable.walle123, R.drawable.walle124, R.drawable.walle125, R.drawable.walle126
    };
    private static final int ANIMATION_INTERVAL = 41;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_screen);

        ImageView img = (ImageView) findViewById(R.id.walle);
        img.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        fastAnimation = FastAnimation.getInstance(img);
        fastAnimation.addAllFrames(IMAGE_RESOURCES, ANIMATION_INTERVAL);
        fastAnimation.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        fastAnimation.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        fastAnimation.start();
    }

    public void openLogin(View V) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openSignup(View V) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}