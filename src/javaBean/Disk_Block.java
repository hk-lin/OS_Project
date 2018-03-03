package javaBean;

import DAO.Disk;

public class Disk_Block {
	public static int len_disktable=Disk.disk_colume;
	public int disk_num;
	public char[] content = new char[len_disktable+1];
	public Disk_Block(){
		for(int i=0;i<=len_disktable;i++){
			content[i]=(char)-1;
		}
	}
}
