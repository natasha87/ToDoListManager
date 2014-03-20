package il.ac.huji.todolist;

public class TodoItem {
	private String _title;
	private String _date;
	
	TodoItem(String title, String date){
		_title = title;
		_date = date;
		
	}

	public String getTitle() {
		return _title;
	}
	
	public String getDueDate(){
		return _date;
	}
	

}
