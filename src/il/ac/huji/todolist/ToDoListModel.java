package il.ac.huji.todolist;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ToDoListModel {

	DatabaseHelper dbHelper;
	SQLiteDatabase db;

	public ToDoListModel(Context context) {
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
	}

	public Cursor getCursor() {
		return db.query(ToDoListConstants.SQLITE_DB, null, null, null, null, null, null);
	}

	// add item
	@SuppressWarnings("deprecation")
	public String addItem(String title, Date date) {
		ContentValues todoItem = new ContentValues();
		String finalTitle = title;
		int counter = 1;
		while (db.query(ToDoListConstants.SQLITE_DB, new String[]{ToDoListConstants.TITLE_COL}, ToDoListConstants.TITLE_COL+"=?",new String[]{finalTitle}, null, null, null).getCount() > 0 ){
			finalTitle = title+"("+Integer.toString(counter)+")"; 
			counter = counter+1;
		}
		todoItem.put(ToDoListConstants.TITLE_COL, finalTitle);
		todoItem.put(ToDoListConstants.DUE_DATE_COL, Date.parse(date.toString()));
		db.insert(ToDoListConstants.SQLITE_DB, null, todoItem);
		return finalTitle;
	}

	// delete item
	public void deleteItem(int position) {
		db.delete(ToDoListConstants.SQLITE_DB, ToDoListConstants.ID_COL+"="+Integer.toString(position), new String[]{});
	}

	// get title by position
	public String getTitle(int position) {
		Cursor cursor = db.query(ToDoListConstants.SQLITE_DB, new String[] {ToDoListConstants.TITLE_COL}, ToDoListConstants.ID_COL+"="+ Integer.toString(position), null, null, null, null, null);
		cursor.moveToFirst();
		int index = cursor.getColumnIndex(ToDoListConstants.TITLE_COL);
		String title = cursor.getString(index); 
		return title;
	}

	// get date by id
	public Date getDate(int position) {
		Cursor cursor = db.query(ToDoListConstants.SQLITE_DB, new String[] {ToDoListConstants.DUE_DATE_COL}, ToDoListConstants.ID_COL+"="+ Integer.toString(position), null, null, null, null, null);
		cursor.moveToFirst();
		int index = cursor.getColumnIndex(ToDoListConstants.DUE_DATE_COL);
		Date date = new Date(cursor.getLong(index));
		return date;
	}

}
