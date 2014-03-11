package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class TodoListManagerActivity extends Activity {

	private ArrayList<String> items_array_list;
	private ListView listView;
	private ToDoListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		items_array_list = new ArrayList<String>();
		listView = (ListView)findViewById(R.id.lstToDoItems);
		adapter = new ToDoListAdapter(this, android.R.layout.simple_list_item_1, items_array_list);
		listView.setAdapter(adapter);
		adapter.setNotifyOnChange(true);
		registerForContextMenu(listView);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		EditText newItem = (EditText)findViewById(R.id.edtNewItem); 
		final String newItemString = newItem.getText().toString();

		switch(id){
		case R.id.menuItemAdd:
			if (!newItemString.equals("")){
				// what if the input is too long? or invalid??
				if (items_array_list.contains(newItemString)){
					new AlertDialog.Builder(this)
					.setTitle("This task already exists!")
					.setMessage("Do you wand to make another copy of the task?")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { 
							// create a duplicate
							int copyCounter = 1;
							while (items_array_list.contains(newItemString+"("+copyCounter+")")){
								copyCounter++;
							}
							items_array_list.add(newItemString+"("+copyCounter+")");
							adapter.notifyDataSetChanged();
							((EditText) findViewById(R.id.edtNewItem)).setText("");
						}
					})
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { 
							// do nothing
							((EditText) findViewById(R.id.edtNewItem)).setText("");
						}
					})
					.show();
				}
				else {
					items_array_list.add(newItemString);
					adapter.notifyDataSetChanged();
					((EditText) findViewById(R.id.edtNewItem)).setText("");
				}
			}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
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
		menu.setHeaderTitle(items_array_list.get(info.position));
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.menuItemDelete:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			int pos = (int) info.id;
			items_array_list.remove(pos);
			adapter.notifyDataSetChanged();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	class ToDoListAdapter extends ArrayAdapter<String>{

		public ToDoListAdapter(Context context, int resource, List<String> items) {
			super(context, resource, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view =(TextView) super.getView(position, convertView, parent);
			if(position%2 == 0){
				view.setTextColor(Color.RED);
			} else{
				view.setTextColor(Color.BLUE);
			}
			view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.my_point_icon, 0, 0, 0);
			view.setTypeface(null, Typeface.BOLD);
			return view;
		}
	}
}