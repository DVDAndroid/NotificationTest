package com.dvd.android.example.notificationtest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int NOTIFICATION_ID = 99;
	EditText title_et;
	EditText message_et;
	Switch heads_up;
	Switch auto_off;
	CheckBox sound_ck;
	CheckBox vibrate_ck;
	CheckBox led_ck;
	DevicePolicyManager deviceManager;
	ActivityManager activityManager;
	ComponentName compName;

	int priority;
	int sound;
	int vibrate;
	int led;
	int seconds = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		title_et = (EditText) findViewById(R.id.title);
		message_et = (EditText) findViewById(R.id.message);
		sound_ck = (CheckBox) findViewById(R.id.sound);
		vibrate_ck = (CheckBox) findViewById(R.id.vibrate);
		led_ck = (CheckBox) findViewById(R.id.led);
		heads_up = (Switch) findViewById(R.id.heads_up);
		auto_off = (Switch) findViewById(R.id.power_off);

		if (!(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP))
			heads_up.setEnabled(false);
		else
			heads_up.setChecked(true);

		sound_ck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!vibrate_ck.isChecked() && !sound_ck.isChecked()) {
					heads_up.setChecked(false);
					Toast.makeText(getApplicationContext(),
							getString(R.string.warn_heads), Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		vibrate_ck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!vibrate_ck.isChecked() && !sound_ck.isChecked()) {
					heads_up.setChecked(false);
					Toast.makeText(getApplicationContext(),
							getString(R.string.warn_heads), Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		heads_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!vibrate_ck.isChecked() && !sound_ck.isChecked()) {
					heads_up.setChecked(false);
					Toast.makeText(getApplicationContext(),
							getString(R.string.warn_heads), Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		auto_off.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!auto_off.isChecked() && led_ck.isChecked()) {
					led_ck.setChecked(false);
				}
			}
		});
		led_ck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!auto_off.isChecked() && led_ck.isChecked()) {
					led_ck.setChecked(false);
				}
			}
		});

	}

	public void start(View v) {
		deviceManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		compName = new ComponentName(this, MyAdmin.class);

		boolean active = deviceManager.isAdminActive(compName);
		if (auto_off.isChecked()) {
			if (active) {
				alertDialog();
			} else {

				Intent intent = new Intent(
						DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
						compName);
				intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
						(getString(R.string.explanation)));
				startActivityForResult(intent, 1);
				alertDialog();
			}
		} else {
			startNotification();
		}
	}

	public void startNotification() {
		// Intent intent = new Intent(getApplicationContext(),
		// MainActivity.class);
		// PendingIntent pending =
		// PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_ID,
		// intent, 0);

		Notification notification;
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (heads_up.isChecked())
			priority = 2;
		else
			priority = 0;

		if (sound_ck.isChecked())
			sound = 1;
		else
			sound = 0;

		if (vibrate_ck.isChecked())
			vibrate = 2;
		else
			vibrate = 0;

		if (led_ck.isChecked())
			led = 4;
		else
			led = 0;

		notification = new Notification.Builder(getApplicationContext())
				.setStyle(
						new Notification.BigTextStyle().bigText(message_et
								.getText().toString()))
				.setContentTitle(title_et.getText().toString())
				.setContentText(message_et.getText().toString())
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis()).setPriority(priority)
				.setDefaults(sound | vibrate | led).setAutoCancel(true).build();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(NOTIFICATION_ID, notification);

	}

	public void alertDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

		alert.setTitle(getString(R.string.set_ms));
		final NumberPicker np = new NumberPicker(MainActivity.this);
		np.setValue(seconds);
		np.setMinValue(1);
		np.setMaxValue(20);
		np.setWrapSelectorWheel(false);
		alert.setView(np);
		alert.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						seconds = np.getValue() * 1000;
						try {
							dialog.dismiss();
							deviceManager.lockNow();
							Thread.sleep(seconds);
							startNotification();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
				});

		alert.show();
	}

	public void remove(View v) {
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.uninstall) {
			try {
				if (deviceManager.isAdminActive(compName)) {
					deviceManager.removeActiveAdmin(compName);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				finish();
				Intent intent = new Intent(Intent.ACTION_DELETE);
				intent.setData(Uri.parse("package:" + this.getPackageName()));
				startActivity(intent);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}