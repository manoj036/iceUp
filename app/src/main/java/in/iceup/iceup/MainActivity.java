package in.iceup.iceup;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;

public class MainActivity extends AppCompatActivity {
	public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1;
	public static boolean opening = true;
	private final BroadcastReceiver FinishThisActivity = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
			overridePendingTransition(0, R.anim.fade_out);
		}
	};
	TextView footerText;
	private DrawerLayout mDrawerLayout;
	private String mode = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		registerReceiver(FinishThisActivity, new IntentFilter("FinishMainActivity"));

//		startActivity(new Intent(getApplicationContext(), InviActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//				.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));

		Toolbar toolbar = findViewById(R.id.include);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
		}

		footerText = findViewById(R.id.footer_text);
		footerText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://iceup.in"));
				startActivity(intent);
			}
		});
		mDrawerLayout = findViewById(R.id.drawer_layout);
		NavigationView navigationView = findViewById(R.id.navdrawer);
		navigationView.getMenu().getItem(0).setChecked(true);

		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.nav_mode:
						startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
								.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
						break;
//					case R.id.nav_settings:
//						break;
					case R.id.nav_demo:
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=59oF0uIzIA8"));
						startActivity(browserIntent);
						break;
//					case R.id.nav_feedback:
//						startActivity(new Intent(getApplicationContext(), FeedbackActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
//						break;
					case R.id.nav_log_out:
						AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								startActivity(new Intent(getApplicationContext(), SignInActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
								finish();
							}
						});
//						break;
				}
				item.setChecked(true);
				mDrawerLayout.closeDrawers();
				return true;
			}
		});

		// ATTENTION: This was auto-generated to handle app links.
		Intent appLinkIntent = getIntent();
		String appLinkAction = appLinkIntent.getAction();
		Uri appLinkData = appLinkIntent.getData();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				mDrawerLayout.openDrawer(GravityCompat.START);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void requestPermission(int requestCode) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
			intent.setData(Uri.parse("package:" + getPackageName()));
			startActivityForResult(intent, requestCode);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD) {
			if (Utils.canDrawOverlays(MainActivity.this)) {
				StartIceUpService();
			} else {
				needPermissionDialog(requestCode);
			}
		}
	}

	private void needPermissionDialog(final int requestCode) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage("Allow permissions to draw over other apps");
		builder.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						requestPermission(requestCode);
					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		builder.setCancelable(false);
		builder.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(FinishThisActivity);
	}

	public void FreezeServiceClick(View view) {
		mode = "freeze";
		StartIceUpService();
	}

	public void IceAgeServiceClick(View view) {
		mode = "lock";
		StartIceUpService();
	}

	private void StartIceUpService() {
		if (Utils.canDrawOverlays(this)) {
			Intent serviceIntent = new Intent(MainActivity.this, iceUpService.class);
			stopService(serviceIntent);
			serviceIntent.putExtra("mode", mode);
			startService(serviceIntent);
		} else {
			needPermissionDialog(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD);
		}
	}

	public void OpenGallery(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setType("image/*");
		startActivity(intent);
	}

	public void OpenYoutube(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse("https://www.youtube.com/"));
		intent.setPackage("com.google.android.youtube");
		startActivity(intent);
	}

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	public Action getIndexApiAction() {
		return Actions.newView("Main", "http://www.iceup.in");
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		FirebaseAppIndex.getInstance().update(new Indexable.Builder().setName("Main").setUrl("http://www.iceup.in").build());
		FirebaseUserActions.getInstance().start(getIndexApiAction());
	}

	@Override
	public void onStop() {

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		FirebaseUserActions.getInstance().end(getIndexApiAction());
		super.onStop();
	}
}