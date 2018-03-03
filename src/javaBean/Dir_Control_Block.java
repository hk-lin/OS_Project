package javaBean;
import java.util.Date;
import GUI.Main;
import java.text.SimpleDateFormat;
public class Dir_Control_Block {
	public static int MAX_SONDIR = 10;
	public static int MAX_SONFILE = 10;
	public String name;
	public String time;
	public String owner;
	public String type;
	public int Sondir_num = 0;
	public int Sonfile_num = 0;
	public Dir_Control_Block Son_dir[] = new Dir_Control_Block[MAX_SONDIR];
	public File_Control_Block Son_file[] = new File_Control_Block[MAX_SONFILE];
	public Dir_Control_Block father;
	public Dir_Control_Block(){
		owner = Main.user;
		type = "文件夹";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		time = df.format(new Date());
	}
}
