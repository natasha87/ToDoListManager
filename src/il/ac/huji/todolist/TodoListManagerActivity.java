package il.ac.huji.todolist;

import java.util.Date;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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
	private LoadListFromDB loadDB;
	private AddToDB addToDB;
	private RemoveFromDB removeFromDB;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);

		listView = (ListView)findViewById(R.id.lstToDoItems);
		todoListModel = new ToDoListModel(getApplicationContext());
		
		setDialogProgress();

		loadDB = new LoadListFromDB();
		loadDB.execute();

		registerForContextMenu(listView);	
	}
	
	private void setDialogProgress(){
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Loading ...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
			addToDB = new AddToDB();
			addToDB.execute(data);
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
		switch (item.getItemId()){
		case R.id.menuItemDelete:
			removeFromDB = new RemoveFromDB();
			removeFromDB.execute(position);
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

	private class LoadListFromDB extends AsyncTask<Void, Void, Cursor>
	{
		@Override
		protected void onPreExecute() {
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Cursor doInBackground(Void... params) {
			todoListModel.getReadable();
			return todoListModel.getCursor();
		}

		@Override
		protected void onPostExecute(Cursor c) {
			adapter = new ToDoListCursorAdapter(getApplicationContext(), c);
			listView.setAdapter(adapter);
			progressDialog.dismiss();
			super.onPostExecute(c);
		}
	}

	private class AddToDB extends AsyncTask<Intent, Void, Cursor>
	{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Cursor doInBackground(Intent... params) {
			todoListModel.getWritable();
			String title = (String) params[0].getExtras().get(ToDoListConstants.TITLE_COL);
			Date date = (Date) params[0].getExtras().get(ToDoListConstants.DUE_DATE_COL);
			todoListModel.addItem(title, date);
			return todoListModel.getCursor();
		}

		@Override
		protected void onPostExecute(Cursor c) {
			adapter.changeCursor(c);
			super.onPostExecute(c);
		}
	}

	private class RemoveFromDB extends AsyncTask<Integer, Void, Void>
	{

		@Override
		protected Void doInBackground(Integer... params) {
			todoListModel.getWritable();
			todoListModel.deleteItem(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			adapter.changeCursor(todoListModel.getCursor());
			super.onPostExecute(v);
		}
	}	
}