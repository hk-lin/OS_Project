package DAO;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import GUI.*;
import javaBean.*;
public class Dir {
	//初始化根目录
	public static void init_dir(Dir_Control_Block root_dir,Dir_Control_Block curr_dir){
		root_dir.name = "root";
		File myFolderPath = new File(root_dir.name+"/");   
		try {   
		    if (!myFolderPath.exists()) {   
		       myFolderPath.mkdir();   
		    }   
		    else {
		    	deleteDir(root_dir.name+"/");
			    myFolderPath.mkdir(); 		    	
			}
		}   
		catch (Exception e) {   
			JOptionPane.showMessageDialog( null, "创建文件夹失败!"); 
		    e.printStackTrace();   
		}    
		root_dir.father = root_dir;
	}
	//依据路径清空磁盘文件夹，用于初始化目录时清空root文件夹及移动时清空原文件夹
    public static boolean deleteDir(String path){  
        File file = new File(path);  
        if(!file.exists()){//判断是否待删除目录是否存在  
			JOptionPane.showMessageDialog(null, "目录不存在!"); 
            return false;  
        }  
          
        String[] content = file.list();//取得当前目录下所有文件和文件夹  
        for(String name : content){  
            File temp = new File(path, name);  
            if(temp.isDirectory()){//判断是否是目录  
                deleteDir(temp.getAbsolutePath());//递归调用，删除目录里的内容  
                temp.delete();//删除空目录  
            }else{  
                if(!temp.delete()){//直接删除文件  
        			JOptionPane.showMessageDialog(null,"删除失败： " + name);  
                }  
            }  
        }  
        return true;  
    }  
	//创建目录
	public static boolean create_dir(Dir_Control_Block curr_dir,String dirname){
		if(find_dir(curr_dir, dirname)!=null){
			JOptionPane.showMessageDialog(null, "文件夹已存在");
			return false;
		}
		else{
			Dir_Control_Block new_dir = new Dir_Control_Block();
			new_dir.name = dirname;
			new_dir.father = curr_dir;
			curr_dir.Son_dir[curr_dir.Sondir_num] = new_dir;
			curr_dir.Sondir_num++;
			String string=new String();
			Dir_Control_Block temp=curr_dir;
			while(temp.father!=temp)
			{
				string=temp.name+"/"+string;
				temp=temp.father;
			}
			string=temp.name+"/"+string; 
			string=string+dirname+"/";
			File myFolderPath = new File(string);   
			try {   
			    if (!myFolderPath.exists()) {   
			       myFolderPath.mkdir();   
			    }   
			}   
			catch (Exception e) {   
				JOptionPane.showMessageDialog(null, "创建文件夹失败!"); 
			    e.printStackTrace();   
			}    
			return true;
		}
	}
	//创建磁盘目录(移动文件夹）
	public static boolean createDir(String curr_dir_path,Dir_Control_Block dir){
			String string=curr_dir_path+dir.name+"/";
			File myFolderPath = new File(string);   
			try {   
			    if (!myFolderPath.exists()) {   
			       myFolderPath.mkdir();   
			    }   
			}   
			catch (Exception e) {   
				JOptionPane.showMessageDialog(null, "创建文件夹失败!"); 
			    e.printStackTrace();   
			    return false;
			}
			Dir_Control_Block curr_dir=dir;
			for(int i = 0; i < curr_dir.Sondir_num;i++){
				createDir(string, curr_dir.Son_dir[i]);
			}
			for(int i = 0; i < curr_dir.Sonfile_num;i++){
				Files.createFile(string,curr_dir.Son_file[i]);
			}
			return true;
	}	
	//重命名目录
	public static boolean rename_dir(Dir_Control_Block curr_dir,String oldname,String newname){
		Dir_Control_Block dir = find_dir(curr_dir,oldname);
		dir.name = newname;
		String string=new String();
		Dir_Control_Block temp=curr_dir;
		while(temp.father!=temp)
		{
			string=temp.name+"/"+string;
			temp=temp.father;
		}
		string=temp.name+"/"+string; 
		File myFolderPath = new File(string+"/"+oldname+"/");   
		myFolderPath.renameTo(new File(string+"/"+newname+"/")); 
		return true;
	}
	//删除目录
	public static boolean delete_dir(Dir_Control_Block curr_dir,String name) throws Exception{
		Dir_Control_Block temp = new Dir_Control_Block();
		for(int i = 0; i < curr_dir.Sondir_num;i++){
			if(curr_dir.Son_dir[i].name.equals(name)){
				temp = curr_dir.Son_dir[i];
				break;
			}
		}
		for(int i = 0; i < temp.Sondir_num;i++){
			delete_dir(temp,temp.Son_dir[i].name);
		}
		while(temp.Sonfile_num>0){
			Files.delete_file(temp.Son_file[0].name,temp,Main.free_stack,Main.freeinode_stack, Main.disk ,Main.iNodes,Main.disk_remain);
		}		
		for(int i = 0; i < curr_dir.Sondir_num;i++){
			if(curr_dir.Son_dir[i].name.equals(temp.name)){
				for(int j = i;j < curr_dir.Sondir_num;j++){
					curr_dir.Son_dir[j] = curr_dir.Son_dir[j+1];
				}
				curr_dir.Son_dir[curr_dir.Sondir_num-1] = null;
				curr_dir.Sondir_num--;
				break;
			}
		}
		String string=new String();
		Dir_Control_Block temp2=curr_dir;
		while(temp2.father!=temp2)
		{
			string=temp2.name+"/"+string;
			temp2=temp2.father;
		}
		string=temp2.name+"/"+string; 
		string=string+name+"/";
		File myDelFolderPath = new File(string);   		
		try {   
			myDelFolderPath.delete();   
		}   
		catch (Exception e) {   
			JOptionPane.showMessageDialog(null, "文件夹删除失败!");
		    e.printStackTrace();   
		} 
		return true;
	}
	//移动目录
	public static boolean move_dir(Dir_Control_Block dir,Dir_Control_Block curr_dir){
		if(find_dir(curr_dir,dir.name)!=null){
			JOptionPane.showMessageDialog(null, "存在同名文件夹!");
			return false;
		}
		curr_dir.Son_dir[curr_dir.Sondir_num] = dir;
		curr_dir.Sondir_num++;
		String string=new String();
		Dir_Control_Block temp=curr_dir;
		while(temp.father!=temp)
		{
			string=temp.name+"/"+string;
			temp=temp.father;
		}
		string=temp.name+"/"+string; 
		createDir(string,dir);
		return true;
	}
	//在当前目录中搜索目录
	public static Dir_Control_Block find_dir(Dir_Control_Block curr_dir,String name){
		for(int i = 0; i < curr_dir.Sondir_num;i++){
			if(curr_dir.Son_dir[i].name.equals(name)){
				return curr_dir.Son_dir[i];
			}
		}
		return null;
	}
}
