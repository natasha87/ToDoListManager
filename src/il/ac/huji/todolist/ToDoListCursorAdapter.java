package il.ac.huji.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ToDoListCursorAdapter extends CursorAdapter{
	

	@SuppressWarnings("deprecation")
	public ToDoListCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	// Bind an existing view to the data pointed to by cursor:
	// view - The view in which the elements we set up here will be displayed.
	// context - The running context where this ListView adapter will be active.
	// cursor - The Cursor containing the query results we will display.
	public void bindView(View view, Context context, Cursor cursor) {

		view.setId(cursor.getInt(cursor.getColumnIndex(ToDoListConstants.ID_COL)));
		TextView elemtextView = (TextView) view.findViewById(R.id.txtTodoTitle);
		TextView dateTextView = (TextView) view.findViewById(R.id.txtTodoDueDate);
		elemtextView.setText(cursor.getString(cursor.getColumnIndex(ToDoListConstants.TITLE_COL)));

		long milliSeconds = cursor.getLong(cursor.getColumnIndex(ToDoListConstants.DUE_DATE_COL));
		Date eventDate = new Date(milliSeconds);	

		SimpleDateFormat format = new SimpleDateFormat(ToDoListConstants.DATE_FORMAT);
		String formattedDate = format.format(eventDate);		
		dateTextView.setText(formattedDate);
		int color;
		if (eventDate.after(new Date())) 
			color = ToDoListConstants.REG_COLOR;
		else
			color = ToDoListConstants.DELAY_COLOR;
		((TextView)view.findViewById(R.id.txtTodoTitle)).setTextColor(color);
		((TextView)view.findViewById(R.id.txtTodoDueDate)).setTextColor(color);
	}

	@Override
	// Makes a new view to hold the data pointed to by cursor.
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater adLayoutInflater = LayoutInflater.from(context);
		View v = adLayoutInflater.inflate(R.layout.row_layout, parent, false);
		return v;
	}

}
