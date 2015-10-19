package com.example.contentprovidersample;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void onClickAddName(View view) {
	      // Add a new student record
	      ContentValues values = new ContentValues();

	      values.put(StudentDetailsProvider.NAME, 
	      ((EditText)findViewById(R.id.name)).getText().toString());

	      Uri uri = getContentResolver().insert(
	      StudentDetailsProvider.CONTENT_URI, values);
	      
	      Toast.makeText(getBaseContext(), 
	      uri.toString(), Toast.LENGTH_LONG).show();
	   }

	   public void onClickRetrieveStudents(View view) {
	      
	      // Retrieve student records
	      String URL = "content://com.example.contentprovidersample.StudentDetailsProvider/student";
	      
	      Uri students = Uri.parse(URL);
	      Cursor c = getContentResolver().query(students, null, null, null, "name");
	      
	      if (c.moveToFirst()) {
	         do{
	            Toast.makeText(this, 
	            c.getString(c.getColumnIndex(StudentDetailsProvider.ID)) +  ", " + c.getString(c.getColumnIndex( StudentDetailsProvider.NAME)) + 
	      ", " , 
	            Toast.LENGTH_SHORT).show();
	         } while (c.moveToNext());
	      }
	   }
}
