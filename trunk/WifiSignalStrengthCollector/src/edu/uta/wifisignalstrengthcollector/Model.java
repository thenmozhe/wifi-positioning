package edu.uta.wifisignalstrengthcollector;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Model {

	protected DatabaseHelper databaseHelper = null;
	protected SQLiteDatabase database;
	protected Context context = null;

	public Model(Context context) {
		this.context = context;
	}

	public Model open() throws SQLException {
		databaseHelper = new DatabaseHelper(context);
		database = databaseHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		databaseHelper.close();
	}
	
	public SQLiteDatabase getDatabase() {
		return database;
	}
	
	
}
