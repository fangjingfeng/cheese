package codingpark.net.cheesecloud.view.fragement;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import codingpark.net.cheesecloud.R;

public class FragmentImager extends ListFragment{
	private Context  context;
	/*public FragmentImager(Context  context){
		this.context=context;
	}*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		View view=View.inflate(context, R.layout.fragement_show_imager, null);
		
		super.onCreate(savedInstanceState);
	}
}
