package in.iceup.iceup;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.util.Objects;

public class iceUpService extends Service {
	private WindowManager windowManager;
	private View inviView, removeView, chatheadView;
	private ImageView removeImg;
	private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
	private Point szWindow = new Point();
	private boolean locked = false;
	private final BroadcastReceiver InviStopped = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent i) {
			if (locked) {
				Log.d("invi Stopped", "onReceive: ");
				try {
					Intent intent = new Intent(getApplicationContext(), InviActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK |
							Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					pendingIntent.send();
				} catch (PendingIntent.CanceledException e) {
					e.printStackTrace();
				}
			}
		}
	};
	private ImageView image;
	private WindowManager.LayoutParams chatheadParams, paramRemove, inviParams;
	private BroadcastReceiver stopService;
	private int mode_int = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("IceUpService", "iceUpService.onCreate()");

		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		stopService = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					if (Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_OFF)) {
						stopSelf();
					}
				}
			}
		};
		registerReceiver(stopService, filter);

		registerReceiver(InviStopped, new IntentFilter("InviStopped"));
	}

	@SuppressLint("InflateParams")
	private void handleStart() {

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		if (inflater != null) {
			removeView = inflater.inflate(R.layout.remove, null);
			chatheadView = inflater.inflate(R.layout.iceup_chathead, null);
			inviView = inflater.inflate(R.layout.activity_invi, null);
		}

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			paramRemove = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					PixelFormat.TRANSLUCENT);

			chatheadParams = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					PixelFormat.TRANSLUCENT);

			inviParams = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					PixelFormat.TRANSPARENT);

		} else {
			paramRemove = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					PixelFormat.TRANSLUCENT);

			chatheadParams = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					PixelFormat.TRANSLUCENT);

			inviParams = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					PixelFormat.TRANSPARENT);

		}

		paramRemove.gravity = Gravity.TOP | Gravity.START;
		chatheadParams.gravity = Gravity.TOP | Gravity.START;
		chatheadParams.x = 0;
		chatheadParams.y = 100;

		removeView.setVisibility(View.GONE);
		removeImg = removeView.findViewById(R.id.remove_img);
		image = chatheadView.findViewById(R.id.chathead_img);

		if (mode_int == 1) {
			image.setImageResource(R.drawable.ice_age);
		} else {
			image.setImageResource(R.drawable.freeze_icon);
		}

		windowManager.addView(removeView, paramRemove);
		windowManager.addView(chatheadView, chatheadParams);

		windowManager.getDefaultDisplay().getSize(szWindow);

		final Runnable myRunnable = new Runnable() {
			@Override
			public void run() {
				if (locked) {
					chatheadView.setVisibility(View.INVISIBLE);
				}
			}
		};
		final Handler myHandler = new Handler();

		inviView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mode_int == 0) {
					if (!chatheadView.isShown()) {
						chatheadView.setVisibility(View.VISIBLE);
					}
					myHandler.removeCallbacks(myRunnable);
					myHandler.postDelayed(myRunnable, 1500);
				}
			}
		});
		inviView.setSoundEffectsEnabled(false);

		chatheadView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				windowManager.updateViewLayout(chatheadView, chatheadParams);
				sendBroadcast(new Intent("FinishMainActivity"));
				locked = !locked;
				if (mode_int == 1) {
					startActivity(new Intent(getApplicationContext(), InviActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
							.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
							.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
					windowManager.addView(inviView, inviParams);
					chatheadView.setVisibility(View.INVISIBLE);
				} else {
					if (locked) {
						startActivity(new Intent(getApplicationContext(), InviActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
								.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
								.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
						windowManager.addView(inviView, inviParams);
						windowManager.removeView(chatheadView);
						image.setImageResource(R.drawable.freeze_it_locked);
						windowManager.addView(chatheadView, chatheadParams);
						chatheadView.setVisibility(View.INVISIBLE);
					} else {
						sendBroadcast(new Intent("FinishInviActivity"));
						image.setImageResource(R.drawable.freeze_icon);
						windowManager.removeView(inviView);
						chatheadView.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		chatheadView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
				int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

				paramRemove.x = x_cord_remove;
				paramRemove.y = y_cord_remove;

				removeView.setVisibility(View.VISIBLE);
				windowManager.updateViewLayout(removeView, paramRemove);
				return true;
			}
		});

		chatheadView.setOnTouchListener(new View.OnTouchListener() {
			long time_start = 0, time_end = 0;
			boolean isLongclick = false, inBounded = false;
			int remove_img_width = 0, remove_img_height = 0;

			Handler handler_longClick = new Handler();
			Runnable runnable_longClick = new Runnable() {
				@Override
				public void run() {
					isLongclick = true;
					chatheadView.performLongClick();
				}
			};

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x_cord = (int) event.getRawX();
				int y_cord = (int) event.getRawY();

				int x_cord_Destination, y_cord_Destination;

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						time_start = System.currentTimeMillis();
						myHandler.removeCallbacks(myRunnable);
						handler_longClick.postDelayed(runnable_longClick, 150);

						remove_img_width = removeImg.getLayoutParams().width;
						remove_img_height = removeImg.getLayoutParams().height;

						x_init_cord = x_cord;
						y_init_cord = y_cord;

						x_init_margin = chatheadParams.x;
						y_init_margin = chatheadParams.y;
						break;

					case MotionEvent.ACTION_MOVE:
						int x_diff_move = x_cord - x_init_cord;
						int y_diff_move = y_cord - y_init_cord;

						x_cord_Destination = x_init_margin + x_diff_move;
						y_cord_Destination = y_init_margin + y_diff_move;

						if (isLongclick) {
							int x_bound_left = szWindow.x / 2 - (int) (remove_img_width * 1.5);
							int x_bound_right = szWindow.x / 2 + (int) (remove_img_width * 1.5);
							int y_bound_top = szWindow.y - (int) (remove_img_height * 1.5);

							if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
								inBounded = true;

								int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
								int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight()));

								if (removeImg.getLayoutParams().height == remove_img_height) {
									removeImg.getLayoutParams().height = (int) (remove_img_height * 1.5);
									removeImg.getLayoutParams().width = (int) (remove_img_width * 1.5);

									paramRemove.x = x_cord_remove;
									paramRemove.y = y_cord_remove;

									windowManager.updateViewLayout(removeView, paramRemove);
								}
								chatheadParams.x = x_cord_remove + (Math.abs(removeView.getWidth() - chatheadView.getWidth())) / 2;
								chatheadParams.y = y_cord_remove + (Math.abs(removeView.getHeight() - chatheadView.getHeight())) / 2;

								windowManager.updateViewLayout(chatheadView, chatheadParams);
								break;
							} else {
								inBounded = false;
								removeImg.getLayoutParams().height = remove_img_height;
								removeImg.getLayoutParams().width = remove_img_width;

								int x_cord_remove = (szWindow.x - removeView.getWidth()) / 2;
								int y_cord_remove = szWindow.y - (removeView.getHeight() + getStatusBarHeight());

								paramRemove.x = x_cord_remove;
								paramRemove.y = y_cord_remove;

								windowManager.updateViewLayout(removeView, paramRemove);
							}
						}
						chatheadParams.x = x_cord_Destination;
						chatheadParams.y = y_cord_Destination;

						windowManager.updateViewLayout(chatheadView, chatheadParams);
						break;

					case MotionEvent.ACTION_UP:
						time_end = System.currentTimeMillis();
						isLongclick = false;
						myHandler.removeCallbacks(myRunnable);
						myHandler.postDelayed(myRunnable, 1500);
						removeView.setVisibility(View.GONE);
						removeImg.getLayoutParams().height = remove_img_height;
						removeImg.getLayoutParams().width = remove_img_width;
						handler_longClick.removeCallbacks(runnable_longClick);

						if (inBounded) {
							stopService(new Intent(iceUpService.this, iceUpService.class));
							inBounded = false;
							break;
						}

						int x_diff = x_cord - x_init_cord;
						int y_diff = y_cord - y_init_cord;

						if (Math.abs(x_diff) < 10 && Math.abs(y_diff) < 10) {
							if ((time_end - time_start) < 300) {
								v.performClick();
							}
						}

						y_cord_Destination = y_init_margin + y_diff;

						int BarHeight = getStatusBarHeight();
						if (y_cord_Destination < 0) {
							y_cord_Destination = 0;
						} else if (y_cord_Destination + (chatheadView.getHeight() + BarHeight) > szWindow.y) {
							y_cord_Destination = szWindow.y - (chatheadView.getHeight() + BarHeight);
						}
						chatheadParams.y = y_cord_Destination;

						inBounded = false;
						resetPosition(x_cord);
						break;
				}
				return true;
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		windowManager.getDefaultDisplay().getSize(szWindow);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (chatheadParams.y + (chatheadView.getHeight() + getStatusBarHeight()) > szWindow.y) {
				chatheadParams.y = szWindow.y - (chatheadView.getHeight() + getStatusBarHeight());
				windowManager.updateViewLayout(chatheadView, chatheadParams);
			}
			if (chatheadParams.x != 0 && chatheadParams.x < szWindow.x) {
				resetPosition(szWindow.x);
			}
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			if (chatheadParams.x > szWindow.x) {
				resetPosition(szWindow.x);
			}
		}
	}

	private void resetPosition(int x_cord_now) {
		if (x_cord_now <= szWindow.x / 2) {
			moveToLeft();

		} else {
			moveToRight();

		}

	}

	private void moveToLeft() {
		ValueAnimator animator = ValueAnimator.ofInt(chatheadParams.x, 0);
		animator.setInterpolator(new DecelerateInterpolator());
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				chatheadParams.x = (Integer) valueAnimator.getAnimatedValue();
				windowManager.updateViewLayout(chatheadView, chatheadParams);
			}
		});
		Log.d("TAG", "moveToLeft: triggered");
		animator.setDuration(200);
		animator.start();
	}

	private void moveToRight() {
		ValueAnimator animator = ValueAnimator.ofInt(chatheadParams.x, szWindow.x - chatheadView.getWidth());
		animator.setInterpolator(new DecelerateInterpolator());
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				chatheadParams.x = (Integer) valueAnimator.getAnimatedValue();
				windowManager.updateViewLayout(chatheadView, chatheadParams);
			}
		});
		Log.d("TAG", "moveToLeft: triggered");
		animator.setDuration(200);
		animator.start();
	}

	private int getStatusBarHeight() {
		return (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String mode = intent.getStringExtra("mode");
		if (mode != null && mode.equals("lock"))
			mode_int = 1;
		if (startId == Service.START_STICKY) {
			handleStart();
			return super.onStartCommand(intent, flags, startId);
		} else {
			return Service.START_NOT_STICKY;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sendBroadcast(new Intent("FinishInviActivity"));
		sendBroadcast(new Intent("FinishMainActivity"));
		try {
			windowManager.removeView(chatheadView);
			windowManager.removeView(inviView);
			windowManager.removeView(removeView);
		} catch (IllegalArgumentException e) {
			Log.e("ICEUPSERVICE", "onDestroy: " + e.getMessage());
		}
		unregisterReceiver(stopService);
		unregisterReceiver(InviStopped);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("IceUpService", "iceUpService.onBind()");

		return null;
	}
}
