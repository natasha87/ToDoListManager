package il.ac.huji.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String table_create_command = "CREATE TABLE " + ToDoListConstants.SQLITE_DB + " ("+ ToDoListConstants.ID_COL+
			" integer PRIMARY KEY autoincrement, " + ToDoListConstants.TITLE_COL + " text, " + ToDoListConstants.DUE_DATE_COL+ " long)";
    DatabaseHelper(Context context) {
        super(context, ToDoListConstants.SQLITE_DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(table_create_command);
    }

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}