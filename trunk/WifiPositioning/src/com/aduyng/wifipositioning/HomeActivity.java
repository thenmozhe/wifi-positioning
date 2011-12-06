package com.aduyng.wifipositioning;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class HomeActivity extends Activity {
	private AccessPoint[] knownAPs;
	private int refreshRate = 5000; // ms second

	private void setUpKnownAPs() {
		ArrayList<AccessPoint> aps = new ArrayList<AccessPoint>();

		Random random = new Random();
		AccessPoint ap = new AccessPoint();
		ap.setMacAddress("00:26:b8:c1:a8:41");
		ap.setSignalStrengthToDistanceRatio(1);
		ap.getCoordinates().setX(random.nextInt(300));
		ap.getCoordinates().setY(random.nextInt(480));
		aps.add(ap);

		ap = new AccessPoint();
		ap.setMacAddress("c4:17:fe:7b:11:8f");
		ap.setSignalStrengthToDistanceRatio(1);
		ap.getCoordinates().setX(random.nextInt(300));
		ap.getCoordinates().setY(random.nextInt(480));
		aps.add(ap);

		ap = new AccessPoint();
		ap.setMacAddress("00:13:10:a0:c7:30");
		ap.setSignalStrengthToDistanceRatio(1);
		ap.getCoordinates().setX(random.nextInt(300));
		ap.getCoordinates().setY(random.nextInt(480));
		aps.add(ap);

		ap = new AccessPoint();
		ap.setMacAddress("cc:af:78:28:86:ae");
		ap.setSignalStrengthToDistanceRatio(1);
		ap.getCoordinates().setX(random.nextInt(300));
		ap.getCoordinates().setY(random.nextInt(480));
		aps.add(ap);

		ap = new AccessPoint();
		ap.setMacAddress("00:1e:c7:53:b8:a9");
		ap.setSignalStrengthToDistanceRatio(1);
		ap.getCoordinates().setX(random.nextInt(300));
		ap.getCoordinates().setY(random.nextInt(480));
		aps.add(ap);

		ap = new AccessPoint();
		ap.setMacAddress("78:e4:00:b0:d5:65");
		ap.setSignalStrengthToDistanceRatio(1);
		ap.getCoordinates().setX(random.nextInt(300));
		ap.getCoordinates().setY(random.nextInt(480));
		aps.add(ap);

		ap = new AccessPoint();
		ap.setMacAddress("00:24:56:2f:75:19");
		ap.setSignalStrengthToDistanceRatio(1);
		ap.getCoordinates().setX(random.nextInt(300));
		ap.getCoordinates().setY(random.nextInt(480));
		aps.add(ap);

		ap = new AccessPoint();
		ap.setMacAddress("00:23:51:d3:8c:b1");
		ap.setSignalStrengthToDistanceRatio(1);
		ap.getCoordinates().setX(random.nextInt(300));
		ap.getCoordinates().setY(random.nextInt(480));
		aps.add(ap);

		knownAPs = aps.toArray(new AccessPoint[aps.size()]);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setUpKnownAPs();
		
		accessPoints[0] = (ImageView) findViewById(R.id.apImageView1);
		accessPoints[1] = (ImageView) findViewById(R.id.apImageView2);
		accessPoints[2] = (ImageView) findViewById(R.id.apImageView3);
		
		device = (ImageView) findViewById(R.id.pinImageView);
		
		
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		new WifiScanningAsyncTask().execute((Void[]) null);
	}

	private boolean isStopScanning = false;

	private class WifiScanningAsyncTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			while (!HomeActivity.this.isStopScanning) {
				WifiManager.WifiLock wifiLock = wifiManager.createWifiLock(
						WifiManager.WIFI_MODE_SCAN_ONLY,
						"WifiSignalStrengthCollector");
				if (!wifiManager.isWifiEnabled()) {
					wifiManager.setWifiEnabled(true);
				}
				wifiLock.acquire();

				publishProgress("Scanning...");

				wifiManager.startScan();

				List<ScanResult> scanResults = wifiManager.getScanResults();

				if (scanResults != null) {
					publishProgress("Scanning found " + scanResults.size()
							+ " access points.");

					// update signal strength level
					for (int i = 0; i < knownAPs.length; i++) {
						knownAPs[i]
								.setSignalLevel(AccessPoint.MIN_SIGNAL_LEVEL);
						for (ScanResult scanResult : scanResults) {

							if (knownAPs[i].getMacAddress().toLowerCase()
									.compareTo(scanResult.BSSID.toLowerCase()) == 0) {
								
								knownAPs[i].setSignalLevel(scanResult.level);
								knownAPs[i].setSSID(scanResult.SSID);
								knownAPs[i].setTimestamp(new Date());
								
								publishProgress("found AP Mac Address = "
										+ scanResult.BSSID + " at "
										+ knownAPs[i].getCoordinates() + " with distance = " + knownAPs[i].getDistance());
								
								break;
							}
						}
					}

					// sort known access points with signal strength descending
					for (int i = 0; i < knownAPs.length - 1; i++) {
						for (int j = i + 1; j < knownAPs.length; j++) {
							if (knownAPs[i].getSignalLevel() < knownAPs[j]
									.getSignalLevel()) {
								AccessPoint tmp = knownAPs[i];
								knownAPs[i] = knownAPs[j];
								knownAPs[j] = tmp;
							}
						}
					}
					// if there are less than 3 access points discovered, show
					// the message
					if (knownAPs[2].getSignalLevel() == AccessPoint.MIN_SIGNAL_LEVEL) {
						publishProgress("There are not enough known access points to calculate the position.");
					} else {
						final Point2D position = Triangulator.triangulate(
								knownAPs[0], knownAPs[1], knownAPs[2]);
						publishProgress("Device position: " + position);
						
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								for(int i = 0; i < 3; i++) {
									RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(32,32);
									params.leftMargin =  (int)knownAPs[i].getCoordinates().getX();
									params.topMargin = (int)knownAPs[i].getCoordinates().getY();
									accessPoints[i].setLayoutParams(params);
									Log.d("ACCESS_POINT", "left: " + accessPoints[i].getLeft() + ", top: " + accessPoints[i].getTop() );
								}

								RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(32,32);
								params.leftMargin = (int)position.getX();
								params.topMargin = (int)position.getY();
								device.setLayoutParams(params);
								Log.d("POSITION", "left: " + device.getLeft() + ", top: " + device.getTop() );
							}
						});
						
					}

				}

				wifiLock.release();
				try {
					Thread.sleep(refreshRate);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return null;

		}

		protected void onProgressUpdate(String... progress) {
			Log.d("SCANNER", progress[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}

	ImageView[] accessPoints = new ImageView[3];
	ImageView device; 
	private WifiManager wifiManager;
}