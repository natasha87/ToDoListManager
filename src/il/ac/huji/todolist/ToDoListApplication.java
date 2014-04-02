package il.ac.huji.todolist;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import android.app.Application;

public class ToDoListApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, ToDoListConstants.APP_ID_PARSE, ToDoListConstants.APP_CLIENTKEY_PARSE);
		ParseUser.enableAutomaticUser(); 

		ParseACL defaultACL = new ParseACL(ParseUser.getCurrentUser());
		ParseACL.setDefaultACL(defaultACL, true);
	}
}
