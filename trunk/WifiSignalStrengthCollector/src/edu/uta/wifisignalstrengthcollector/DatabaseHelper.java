package edu.uta.wifisignalstrengthcollector;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "TiemNail";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
//	private static final String DATABASE_CREATE = "CREATE TABLE AccessPoint(macAddress CHAR(17) NOT NULL, signalLevel INTEGER NOT NULL DEFAULT 0, distance INTEGER NULL, PRIMARY KEY(macAddress));";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(AccessPointModel.TABLE_CREATE);
	}

	// Method is called during an upgrade of the database, e.g. if you increase
	// the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(DatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL(AccessPointModel.TABLE_DROP);
		onCreate(database);
	}
	
	
}
