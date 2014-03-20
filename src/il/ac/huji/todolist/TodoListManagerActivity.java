package il.ac.huji.todolist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TodoListManagerActivity extends Activity {

	private ArrayList<TodoItem> items_array_list;
	private ListView listView;
	private ToDoListAdapter<TodoItem> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		items_array_list = new ArrayList<TodoItem>();
		listView = (ListView)findViewById(R.id.lstToDoItems);
		adapter = new ToDoListAdapter<TodoItem>(getApplicationContext(), R.layout.row_layout, items_array_list);
		listView.setAdapter(adapter);
		adapter.setNotifyOnChange(true);
		registerForContextMenu(listView);
		Log.i(Constants._COUNT, "done on create");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		switch(id){
		case R.id.menuItemAdd:
			Intent intent = new Intent(getApplicationContext(), AddNewTodoItemActivity.class);
			startActivityForResult(intent, 1);
			Log.i(Constants._COUNT, "Add menu item selected");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null){
			Log.i(Constants._COUNT, "Getting result");
			String title = (String) data.getExtras().get("title");
			if (title.equals(""))
				return;
			Date date = (Date) data.getExtras().get("date");
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			String formattedDate = format.format(date);
			items_array_list.add(new TodoItem(title, formattedDate));
			adapter.notifyDataSetChanged();	
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
		inflater.inflate(R.menu.context_menu, menu);
		menu.findItem(R.id.menuItemCall).setVisible(false);
		menu.setHeaderTitle(items_array_list.get(info.position).getTitle());

		if (items_array_list.get(info.position).getTitle().startsWith("Call ")){
			menu.findItem(R.id.menuItemCall).setVisible(true);
			MenuItem mi = (MenuItem) menu.findItem(R.id.menuItemCall);
			mi.setTitle(items_array_list.get(info.position).getTitle());
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()){
		case R.id.menuItemDelete:
			int pos = (int) info.id;
			items_array_list.remove(pos);
			adapter.notifyDataSetChanged();
			return true;
		case R.id.menuItemCall:
			String tel = items_array_list.get(info.position).getTitle().replace("Call ", "");
			Intent intentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:".concat(tel)));
			startActivity(intentDial);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	class ToDoListAdapter<T> extends ArrayAdapter<T>{
		Context context;

		public ToDoListAdapter(Context context, int resource, List<T> items) {
			super(context, resource, items);
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = convertView;
			LayoutInflater inflater = getLayoutInflater();
			view = inflater.inflate(R.layout.row_layout, null);
			((TextView)view.findViewById(R.id.txtTodoTitle)).setText(items_array_list.get(position).getTitle());
			((TextView)view.findViewById(R.id.txtTodoDueDate)).setText(items_array_list.get(position).getDueDate().toString());

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date usersdate = null;
			try {
				usersdate = sdf.parse(items_array_list.get(position).getDueDate());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (new Date().after(usersdate)) {
				((TextView)view.findViewById(R.id.txtTodoTitle)).setTextColor(Color.RED);
				((TextView)view.findViewById(R.id.txtTodoDueDate)).setTextColor(Color.RED);
			}
			return view;
		}
	}
}