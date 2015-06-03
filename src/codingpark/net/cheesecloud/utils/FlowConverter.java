package codingpark.net.cheesecloud.utils;

public class FlowConverter {
	private final static int KB            = 1024;
    private final static int MG            = KB * KB;
    private final static int GB            = MG * KB;
    private static String display_size     = null;
	public static String Convert(long fileSeze){
		if (fileSeze > GB){
            display_size = String.format("%.2f Gb ", (double)fileSeze / GB);
		}else if (fileSeze < GB && fileSeze > MG){
            display_size = String.format("%.2f Mb ", (double)fileSeze / MG);
		}else if (fileSeze < MG && fileSeze > KB){
            display_size = String.format("%.2f Kb ", (double)fileSeze/ KB);
		}else{
            display_size = String.format("%.2f bytes ", (double)fileSeze);
        }
		return display_size;
	}  
}

