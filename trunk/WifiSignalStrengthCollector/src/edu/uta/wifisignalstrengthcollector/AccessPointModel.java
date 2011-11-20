package edu.uta.wifisignalstrengthcollector;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class AccessPointModel extends Model {
	public static final String TABLE_NAME = "AccessPoint";
	public static final String COLUMN_MAC_ADDRESS = "macAddress";
	public static final String COLUMN_SIGNAL_LEVEL = "signalLevel";
	public static final String COLUMN_DISTANCE = "distance";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_TIMESTAMP = "timestamp";

	public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_MAC_ADDRESS + " CHAR(17) NOT NULL, " + COLUMN_SIGNAL_LEVEL
			+ " INTEGER NOT NULL DEFAULT 0, " + COLUMN_DISTANCE
			+ " INTEGER NULL, " + COLUMN_TIMESTAMP + " DATETIME NULL" + ")";
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS AccessPoint";

	public AccessPointModel(Context context) {
		super(context);
	}

	public int create(AccessPoint ap) {
		ContentValues initialValues = rowToContentValues(ap);
		return (int) database.insert(TABLE_NAME, null, initialValues);
	}

	public static String[] getColumns() {
		return new String[] { COLUMN_ID, COLUMN_MAC_ADDRESS,
				COLUMN_SIGNAL_LEVEL, COLUMN_DISTANCE, COLUMN_TIMESTAMP };
	}

	public static ContentValues rowToContentValues(AccessPoint ap) {
		ContentValues values = new ContentValues();
		if( ap.getId() != 0 ) {
			values.put(COLUMN_ID, ap.getId());
		}
		values.put(COLUMN_MAC_ADDRESS, ap.getMacAddress());
		values.put(COLUMN_SIGNAL_LEVEL, ap.getSignalLevel());
		values.put(COLUMN_DISTANCE, ap.getDistance());
		values.put(COLUMN_TIMESTAMP, ap.getTimestamp().toString());
		return values;
	}

	public ArrayList<AccessPoint> fetchAll() {
		ArrayList<AccessPoint> rows = new ArrayList<AccessPoint>();
		Cursor cursor = database.query(false, TABLE_NAME, getColumns(), null,
				null, null, null, COLUMN_TIMESTAMP + " DESC", null);

		if (cursor == null || !cursor.moveToFirst()) {
			return null;
		}

		do {
			rows.add(cursorToRow(cursor));
		} while (cursor.moveToNext());

		cursor.close();

		return rows;

	}

	public static AccessPoint cursorToRow(Cursor cursor) {
		AccessPoint ap = new AccessPoint();
		ap.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
		ap.setDistance(cursor.getInt(cursor.getColumnIndex(COLUMN_DISTANCE)));
		ap.setMacAddress(cursor.getString(cursor
				.getColumnIndex(COLUMN_MAC_ADDRESS)));
		ap.setSignalLevel(cursor.getInt(cursor
				.getColumnIndex(COLUMN_SIGNAL_LEVEL)));
		ap.setDistance(cursor.getInt(cursor.getColumnIndex(COLUMN_DISTANCE)));
		String dateString = cursor.getString(cursor
				.getColumnIndex(COLUMN_TIMESTAMP));
		ap.setTimestamp(new Date(Date.parse(dateString)));

		return ap;

	}

}
