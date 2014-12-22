package photos.quees.quees;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import photos.quees.models.Question;
import photos.quees.services.BitmapManipulation;

/**
 * Created by johnpark on 12/18/14.
 */
public class ReviewListAdapter extends ArrayAdapter<Question> {
    private final Context context;
    private ArrayList<Question> qs;
    private static LayoutInflater inflater = null;

    public ReviewListAdapter(Context context, int viewResourceId, ArrayList<Question> _qs_) {
        super(context, viewResourceId, _qs_);
        this.context = context;
        this.qs = _qs_;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.listview_item, parent, false);

        TextView qNum = (TextView) rowView.findViewById(R.id.q_num);
        TextView qDesc = (TextView) rowView.findViewById(R.id.q_desc);
        TextView qNoGuess = (TextView) rowView.findViewById(R.id.q_guesses_no_guesses);
        TextView qBestGuess = (TextView) rowView.findViewById(R.id.q_guesses_best_guess);
        TextView qBestGuessEmail = (TextView) rowView.findViewById(R.id.q_guesses_best_guess_email);
        ImageView qImg = (ImageView) rowView.findViewById(R.id.q_img);

        qNum.setText("QUESTION " + Integer.toString(position + 1));
        qDesc.setText(qs.get(position).getDesc());

        String imgString = qs.get(position).getImage();
        String bestGuess = qs.get(position).bestGuess;
        final String bestGuessEmail = qs.get(position).bestGuessEmail;
        BitmapManipulation bm = new BitmapManipulation();

        Bitmap bitmap = bm.decodeBase64(imgString);

        if (bitmap != null) {
            qImg.setImageBitmap(bitmap);
        }

        if (bestGuess != null && bestGuess.length() > 0) {
            qNoGuess.setVisibility(View.GONE);
            qBestGuess.setText(bestGuess);
            qBestGuess.setVisibility(View.VISIBLE);
            qBestGuessEmail.setText(bestGuessEmail);
            qBestGuessEmail.setVisibility(View.VISIBLE);

            qBestGuessEmail.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{bestGuessEmail});
                    email.putExtra(Intent.EXTRA_SUBJECT, "Quees - YGMQR(You Got My Question Right)");
                    email.putExtra(Intent.EXTRA_TEXT, "Wanna hang out?");
                    email.setType("message/rfc822");

                    context.startActivity(Intent.createChooser(email, "Choose an Email client :"));
                }
            });
        }

        return rowView;
    }
}