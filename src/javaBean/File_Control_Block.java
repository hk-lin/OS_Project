package javaBean;
/*
 * 文件数据结构
 * name:文件名称
 * owner:文件创建者
 * type:类型
 * time:修改日期
 * inode:文件索引节点
 */
import java.util.*;

import GUI.Main;

import java.text.SimpleDateFormat;
public class File_Control_Block {
	public String name;
	public String owner;
	public String type;
	public String time;
	public int inodenum;
	public File_Control_Block() throws Exception{
		inodenum = Main.freeinode_stack.pop();
		owner = "admin";
		type = "文件";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		time = df.format(new Date());
	}
}
