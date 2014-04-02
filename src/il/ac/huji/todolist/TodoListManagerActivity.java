package il.ac.huji.todolist;

import java.util.Date;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class TodoListManagerActivity extends Activity {

	private ListView listView;
	private ToDoListModel todoListModel;
	private ToDoListCursorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		listView = (ListView)findViewById(R.id.lstToDoItems);
		todoListModel = new ToDoListModel(getApplicationContext());
		adapter = new ToDoListCursorAdapter(getApplicationContext(), todoListModel);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		switch(id){
		case R.id.menuItemAdd:
			Intent intent = new Intent(getApplicationContext(), AddNewTodoItemActivity.class);
			startActivityForResult(intent, 1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null){
			String title = (String) data.getExtras().get(ToDoListConstants.TITLE_COL);
			if (title.equals(ToDoListConstants.EMPTY_PREF))
				return;
			Date date = (Date) data.getExtras().get(ToDoListConstants.DUE_DATE_COL);
			String finalTitle = todoListModel.addItem(title, date);
			adapter.changeCursor(todoListModel.getCursor());
			addToParse(finalTitle, date);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.todo_list_manager, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int position = (int) info.id;

		inflater.inflate(R.menu.context_menu, menu);
		menu.findItem(R.id.menuItemCall).setVisible(false);
		menu.setHeaderTitle(todoListModel.getTitle(position));

		if (todoListModel.getTitle(position).startsWith(ToDoListConstants.CALL_PREF)){
			menu.findItem(R.id.menuItemCall).setVisible(true);
			MenuItem mi = (MenuItem) menu.findItem(R.id.menuItemCall);
			mi.setTitle(todoListModel.getTitle(position));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = (int) info.id;
		String title = todoListModel.getTitle(position);
		switch (item.getItemId()){
		case R.id.menuItemDelete:
			todoListModel.deleteItem(position);
			adapter.changeCursor(todoListModel.getCursor());
			deleteFromParse(title);
			return true;
		case R.id.menuItemCall:
			String tel = todoListModel.getTitle(position).replace(ToDoListConstants.CALL_PREF, ToDoListConstants.EMPTY_PREF);
			Intent intentDial = new Intent(Intent.ACTION_DIAL, Uri.parse(ToDoListConstants.TEL_PREF.concat(tel)));
			startActivity(intentDial);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void addToParse(String title, Date date){
		ParseObject obj = new ParseObject(ToDoListConstants.PARSE_TABLE);
		obj.put(ToDoListConstants.TITLE_COL, title);
		obj.put(ToDoListConstants.DUE_DATE_COL, date);
		obj.saveInBackground();
	}

	private void deleteFromParse(String title){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ToDoListConstants.PARSE_TABLE);
		// NOTICE: the names of the toDo item are unique.
		query.whereEqualTo(ToDoListConstants.TITLE_COL, title);			
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if(e == null){
					if(objects.size() > 0){
						objects.get(0).deleteInBackground();
					}
					else{
						Log.i("parseDelete", "Item to delete, not found");
					}
				}
				else{
					Log.i("parseDelete", "An error occured in delete");
				}
			}
		});
	}
}