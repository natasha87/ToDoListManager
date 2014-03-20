package il.ac.huji.todolist;

import java.util.Calendar;
import java.util.GregorianCalendar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddNewTodoItemActivity extends Activity  implements View.OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(Constants._COUNT, "onCreate add activity 1");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_new_item_layout);

		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		Button btnOk = (Button) findViewById(R.id.btnOK);
		Log.i(Constants._COUNT, "onCreate add activity 2");
		btnCancel.setOnClickListener((OnClickListener) this);
		btnOk.setOnClickListener((OnClickListener)this);
		Log.i(Constants._COUNT, "onCreate add activity 3");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_item_menu, menu);
		return true;
	}


	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnOK:
			Intent intent = getIntent();
			DatePicker dp = (DatePicker) findViewById(R.id.datePicker);
			Calendar cal = new GregorianCalendar(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
			intent.putExtra("date", cal.getTime());
			EditText edtNewItem = (EditText) findViewById(R.id.edtNewItem);
			intent.putExtra("title", edtNewItem.getText().toString());
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.btnCancel:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}
	}
}
