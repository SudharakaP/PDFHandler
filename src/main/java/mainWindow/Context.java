package mainWindow;

public class Context {
	public static final Context context = new Context();
	
	private Context(){}
	
	private MainWindowController mainWindow;

	public static Context getContext(){
		return context;
	}
	
	public void setMainWindow(MainWindowController mainWindow) {
		this.mainWindow = mainWindow;		
	}
	
	public MainWindowController getMainWindow() {
		return mainWindow;		
	}
}
