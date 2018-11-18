package in.iceup.iceup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class FeedbackActivity extends AppCompatActivity {
	boolean bt1 = false, bt2 = false, bt3 = false;
	EditText feedback;
	FirebaseUser user;
	float rating = 0;
	RatingBar ratingBar;
//	FirebaseDatabase database;
//	DatabaseReference myRef;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		feedback = findViewById(R.id.editText);
		ratingBar = findViewById(R.id.rating_bar);

		user = FirebaseAuth.getInstance().getCurrentUser();
//		database = FirebaseDatabase.getInstance();
//		myRef = database.getReference();
	}

	public void send_feedback(View view) {
//		rating = ratingBar.getRating();
//		String feedbackText = feedback.getText().toString();
//		Calendar calendar = Calendar.getInstance();
//		myRef.child("users").child(user.getUid()).child(calendar.getTime().toString()).child("feedback_rating").setValue(Float.toString(rating));
//		myRef.child("users").child(user.getUid()).child(calendar.getTime().toString()).child("feedback").setValue(feedbackText);
		finish();
	}
}
