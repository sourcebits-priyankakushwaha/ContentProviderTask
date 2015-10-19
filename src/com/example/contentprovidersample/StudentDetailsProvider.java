package com.example.contentprovidersample;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class StudentDetailsProvider extends ContentProvider {

	// authority in manifest

	static final String PROVIDER_NAME = "com.example.contentprovidersample.StudentDetailsProvider";
	static final String URL = "content://" + PROVIDER_NAME + "/student";
	static final Uri CONTENT_URI = Uri.parse(URL);

	//
	static final String ID = "id";
	static final String NAME = "name";

	static final int STUDENTS = 1;
	static final int STUDENT_ID = 2;
	private static HashMap<String, String> STUDENT_MAP;
	static final UriMatcher uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "student", STUDENTS);
		uriMatcher.addURI(PROVIDER_NAME, "student/#", STUDENT_ID);
	}

	// Database creation
	DBHelper dbHelper;
	private SQLiteDatabase database;
	static final String DATABASE_NAME = "db";
	static final String TABLE_NAME = "student";
	static final int DATABASE_VERSION = 1;

	// declaring a constant var for creating a student table with primary key
	// and not null constrain

	static final String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ " name TEXT NOT NULL);";

	// 1. we are implementing all the methods of content provider
	@Override
	public boolean onCreate() {

		Context context = getContext();
		dbHelper = new DBHelper(context);

		// WE CAN UPDATE THE RECORDS
		database = dbHelper.getWritableDatabase();

		if (database == null)

			return false;
		else
			return true;

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long row = database.insert(TABLE_NAME, "", values);
		if (row > 0) {
			Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}
		throw new SQLException("Fail to add a new record into " + uri);

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		int count = 0;

		switch (uriMatcher.match(uri)) {

		case STUDENTS:

			count = database.update(TABLE_NAME, values, selection, selectionArgs);

			break;

		case STUDENT_ID:

			count = database.update(TABLE_NAME, values, ID +

			" = " + uri.getLastPathSegment() +

			(!TextUtils.isEmpty(selection) ? " AND (" +

			selection + ')' : ""), selectionArgs);

			break;

		default:

			throw new IllegalArgumentException("Unsupported URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int count = 0;

		switch (uriMatcher.match(uri)) {

		case STUDENTS:

			count = database.delete(TABLE_NAME, selection, selectionArgs);

			break;

		case STUDENT_ID:

			count = database.delete(TABLE_NAME, ID +

			" = " + uri.getLastPathSegment() +

			(!TextUtils.isEmpty(selection) ? " AND (" +

			selection + ')' : ""), selectionArgs);
			break;

		default:

			throw new IllegalArgumentException("Unsupported URI " + uri);

		}

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public String getType(Uri uri) {

		return null;
	}

	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// the TABLE_NAME to query on

		queryBuilder.setTables(TABLE_NAME);

		switch (uriMatcher.match(uri)) {
		// maps all database column names
		case STUDENTS:
			queryBuilder.setProjectionMap(STUDENT_MAP);
			break;
		case STUDENT_ID:
			queryBuilder.appendWhere(ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		if (sortOrder == null || sortOrder == "") {

			// No sorting-> sort on names by default

			sortOrder = NAME;

		}

		Cursor cursor = queryBuilder.query(database, projection, selection,

		selectionArgs, null, null, sortOrder);

		// register to watch a content URI for changes

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	// DBHelper is the class to handle all the operations of database
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE); // execSQL is method to create a table
										// inside the database. this oncreate
										// belongs to SQLiteOpenHelper class
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}

	}

}
