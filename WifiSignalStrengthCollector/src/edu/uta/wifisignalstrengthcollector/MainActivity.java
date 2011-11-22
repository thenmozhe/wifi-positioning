package edu.uta.wifisignalstrengthcollector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity implements OnClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView lv = getListView();
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup) inflater.inflate(
				R.layout.access_point_list_header, lv, false);
		lv.addHeaderView(header, null, false);

		saveButton = (Button) findViewById(R.id.saveButton);
		startButton = (Button) findViewById(R.id.startButton);
		reportButton = (Button) findViewById(R.id.reportButton);

		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

		saveButton.setOnClickListener(this);
		startButton.setOnClickListener(this);
		reportButton.setOnClickListener(this);

		onClick(startButton);

	}

	private class WifiScanningAsyncTask extends
			AsyncTask<Void, String, ArrayList<AccessPoint>> {

		@Override
		protected ArrayList<AccessPoint> doInBackground(Void... arg0) {

			// TODO: perform scanning of wifi over here
			ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();

			// do real work here
			WifiManager.WifiLock wifiLock = wifiManager.createWifiLock(
					WifiManager.WIFI_MODE_SCAN_ONLY,
					"WifiSignalStrengthCollector");
			wifiLock.acquire();
			wifiManager.setWifiEnabled(true);
			publishProgress("Scanning...");

			wifiManager.startScan();

			List<ScanResult> scanResults = wifiManager.getScanResults();

			publishProgress("Done!");

			if (scanResults != null) {
				Log.d("TIMER", "Results count " + scanResults.size());
				for (ScanResult scanResult : scanResults) {
					AccessPoint ap = new AccessPoint();
					ap.setMacAddress(scanResult.BSSID);
					ap.setSSID(scanResult.SSID);
					ap.setSignalLevel(scanResult.level);
					ap.setTimestamp(new Date());
					accessPoints.add(ap);

				}
			}
			wifiLock.release();

			return accessPoints;
		}

		protected void onProgressUpdate(String... progress) {
			startButton.setText(progress[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<AccessPoint> result) {
			accessPoints = result;
			AccessPointArrayAdapter adapter = new AccessPointArrayAdapter(
					MainActivity.this, accessPoints);
			setListAdapter(adapter);

			startButton.setText(getResources().getText(R.string.scan));

			super.onPostExecute(result);
		}

	}

	private WifiManager wifiManager;

	private class AccessPointArrayAdapter extends ArrayAdapter<AccessPoint> {
		private final Context context;
		private final ArrayList<AccessPoint> accessPoints;

		public AccessPointArrayAdapter(Context context,
				ArrayList<AccessPoint> accessPoints) {
			super(context, R.layout.access_point_list_item, accessPoints);
			this.context = context;
			this.accessPoints = accessPoints;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View rowView = inflater.inflate(
					R.layout.access_point_list_item, parent, false);
			final AccessPoint accessPoint = accessPoints.get(position);

			TextView accessPointNameTextView = (TextView) rowView
					.findViewById(R.id.accessPointNameTextView);
			accessPointNameTextView.setText(accessPoint.getMacAddress() + " ("
					+ accessPoint.getSSID() + ")");

			TextView signalLevelTextView = (TextView) rowView
					.findViewById(R.id.signalLevelTextView);
			int signalLevel = accessPoint.getSignalLevel();

			signalLevelTextView.setText(String.valueOf(signalLevel));

			final TextView distanceTextView = (TextView) rowView
					.findViewById(R.id.distanceTextView);

			rowView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					rowView.setBackgroundColor(Color.YELLOW);

					LayoutInflater factory = LayoutInflater
							.from(MainActivity.this);
					final View textEntryView = factory.inflate(
							R.layout.distance_dialog, null);
					final EditText distanceEditText = (EditText) textEntryView
							.findViewById(R.id.distanceEditText);
					String enteredDistanceText = distanceTextView.getText()
							.toString().trim();
					int distance = 0;
					if (enteredDistanceText.length() > 0
							&& enteredDistanceText.compareTo(getResources()
									.getString(R.string.n_a)) != 0) {
						distance = Integer.parseInt(enteredDistanceText);
					}
					distanceEditText.setText(String.valueOf(distance));

					new AlertDialog.Builder(MainActivity.this)
							.setTitle(
									getResources().getString(
											R.string.distance_to)
											+ " "
											+ accessPoint.getMacAddress()
											+ " ("
											+ accessPoint.getSSID()
											+ ")")
							.setView(textEntryView)
							.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											String enteredDistanceText = distanceEditText
													.getText().toString()
													.trim();
											int distance = 0;
											if (enteredDistanceText.length() > 0) {
												distance = Integer
														.parseInt(enteredDistanceText);
											}
											distanceTextView.setText(String
													.valueOf(distance));
											accessPoint.setDistance(distance);
											Toast.makeText(
													MainActivity.this
															.getApplicationContext(),
													R.string.distance_has_been_updated,
													Toast.LENGTH_LONG);
										}
									})
							.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											rowView.setBackgroundColor(Color.BLACK);

										}
									}).create().show();

				}
			});
			return rowView;
		}
	}

	ArrayList<AccessPoint> accessPoints;

	Button saveButton;
	Button startButton;
	Button reportButton;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.saveButton:
			save();
			// intentionally no break to continue to the next case
		case R.id.startButton:
			new WifiScanningAsyncTask().execute((Void[]) null);

			break;
		case R.id.reportButton:
			Intent intent = new Intent(this, ReportActivity.class);
			startActivity(intent);
			break;
		}

	}

	private int save() {
		AccessPointModel model = new AccessPointModel(this);
		model.open();
		model.getDatabase().beginTransaction();

		int successCount = 0;
		for (AccessPoint ap : accessPoints) {
			if (ap.getDistance() > 0) {
				int id = model.create(ap);
				if (id > 0) {
					successCount++;
				}
			}
		}

		model.getDatabase().setTransactionSuccessful();
		model.getDatabase().endTransaction();
		model.close();

		Toast.makeText(getApplicationContext(), String.valueOf(successCount)
				+ " " + R.string.access_point_has_been_aved, Toast.LENGTH_LONG);

		return successCount;
	}

}