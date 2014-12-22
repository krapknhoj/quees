package photos.quees.models;

import android.graphics.Bitmap;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

import photos.quees.models.Guess;

/**
 * Created by johnpark on 12/16/14.
 */
public class Question {
    public String image;
    public String desc;
    public String email;
    public Map<String, String> timestamp;
    public Long unixTime = null;
    public String id;
    public String bestGuess;
    public String bestGuessEmail;

    public Question() {
    }

    public Question(String desc, String email, String image) {
        this.desc = desc;
        this.email = email;
        this.image = image;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return this.image;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getReadableTime() {
        if (this.unixTime == null) {
            return "";
        }

        Date date = new Date(this.unixTime);
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);
    }
}