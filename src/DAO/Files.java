package DAO;

import javax.swing.JOptionPane;
import GUI.Main;

import GUI.Main;
import javaBean.*;

import java.awt.HeadlessException;
import java.io.*;
public class Files {
	//创建新文件
	public static boolean create_file(String filename,int length,Dir_Control_Block curr_dir,Stack free_stack,Stack freeinode_stack,Disk_Block[][] disk,INode[] iNodes,Disk_Remain_Record disk_remain,char[] mesg_str) throws  Exception{
		if(find_file(filename, curr_dir)!=null){
			JOptionPane.showMessageDialog(null, "存在同名文件!");
			return false;
		}
		else{
			File_Control_Block fileItem = new File_Control_Block();
			freeinode_stack.objs[Main.inodecount]--;
			fileItem.name = filename;
			iNodes[fileItem.inodenum].file_length = length;
			iNodes[fileItem.inodenum].open_flag = 0;
			int need_disk_num;
			if(length%Disk_Block.len_disktable==0)
				need_disk_num = length/Disk_Block.len_disktable;
			else
				need_disk_num = length/Disk_Block.len_disktable+1;
			iNodes[fileItem.inodenum].use_disknum = need_disk_num;
			if(Disk.alloc_disk(fileItem.inodenum, need_disk_num, disk_remain, free_stack, disk,iNodes, mesg_str))
			{
				curr_dir.Son_file[curr_dir.Sonfile_num] = fileItem;
				curr_dir.Sonfile_num++;
				String string=new String();
				Dir_Control_Block temp=curr_dir;
				while(temp.father!=temp)
				{
					string=temp.name+"/"+string;
					temp=temp.father;
				}
				string=temp.name+"/"+string; 
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
				string=string+fileItem.name+".txt";
				File myFilePath = new File(string);   
				try {   
				    if (!myFilePath.exists()) {   
				        myFilePath.createNewFile();   
				    }   
				    FileWriter resultFile = new FileWriter(myFilePath);   
				    PrintWriter myFile = new PrintWriter(resultFile);   
				    myFile.println(mesg_str);   
				    for(int i=0;i<10;i++)
				    	myFile.println();
				    myFile.println("创建用户："+Main.user);
				    resultFile.close();   
				}   
				catch (Exception e) {   
					JOptionPane.showMessageDialog(null, "创建文件失败!");
				    e.printStackTrace();   
				}    
				return true;
			}else{
				return false;
			}
		}
	}
	//创建磁盘文件（移动文件夹）
	public static boolean createFile(String curr_dir_path,File_Control_Block file){
		String string=curr_dir_path+file.name+".txt";
		File myFilePath = new File(string);   
		try {   
		    if (!myFilePath.exists()) {   
		       myFilePath.createNewFile();   
		    }   
		    FileWriter resultFile = new FileWriter(myFilePath);   
		    PrintWriter myFile = new PrintWriter(resultFile);   
		    myFile.println(open_file(file,Main.disk,Main.iNodes));   
		    for(int i=0;i<10;i++)
		    	myFile.println();
		    myFile.println("创建用户："+Main.user);
		    resultFile.close();   
		}   
		catch (Exception e) {   
			JOptionPane.showMessageDialog(null, "创建文件失败!"); 
		    e.printStackTrace();   
		    return false;
		}		
		return true;
	}
	//删除文件
	public static boolean delete_file(String filename,Dir_Control_Block curr_dir,Stack free_stack,Stack freeinode_stack,Disk_Block[][] disk,INode[] iNodes,Disk_Remain_Record disk_remain) throws Exception{
		File_Control_Block fileItem = find_file(filename, curr_dir);
		Disk.reback_disk(fileItem.inodenum, free_stack,freeinode_stack, disk, iNodes,disk_remain);
		for(int i = 0; i < 13;i++){
			iNodes[fileItem.inodenum].disk_addr[i] = -1;
		}
		freeinode_stack.push(fileItem.inodenum);
		freeinode_stack.objs[Main.inodecount]++;
		for(int i = 0; i < curr_dir.Sonfile_num;i++){
			if(curr_dir.Son_file[i].name.equals(filename)){
				for(int j = i; j < curr_dir.Sonfile_num-1;j++){
					curr_dir.Son_file[j] = curr_dir.Son_file[j+1];
				}
				curr_dir.Son_file[curr_dir.Sonfile_num-1]=null;
				curr_dir.Sonfile_num--;
				break;
			}
		}
		String string=new String();
		Dir_Control_Block temp=curr_dir;
		while(temp.father!=temp)
		{
			string=temp.name+"/"+string;
			temp=temp.father;
		}
		string=temp.name+"/"+string; 
		string=string+fileItem.name+".txt";
		File myDelFile = new File(string);   
		try {   
		    myDelFile.delete();   
		}   
		catch (Exception e) {   
			JOptionPane.showMessageDialog(null, "文件删除失败!");
		    e.printStackTrace();   
		} 
		return true;
	}
	//移动文件
		public static boolean move_file(File_Control_Block file,Dir_Control_Block curr_dir){
			if(find_file(file.name, curr_dir)!=null){
				JOptionPane.showMessageDialog(null, "存在同名文件!");
				return false;
			}
			curr_dir.Son_file[curr_dir.Sonfile_num] = file;
			curr_dir.Sonfile_num++;
			String string=new String();
			Dir_Control_Block temp=curr_dir;
			while(temp.father!=temp)
			{
				string=temp.name+"/"+string;
				temp=temp.father;
			}
			string=temp.name+"/"+string; 
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
			string=string+file.name+".txt";
			File myFilePath = new File(string);   
			try {   
			    if (!myFilePath.exists()) {   
			        myFilePath.createNewFile();   
			    }   
			    FileWriter resultFile = new FileWriter(myFilePath);   
			    PrintWriter myFile = new PrintWriter(resultFile);   
			    myFile.println(open_file(file, Main.disk,Main.iNodes));   
			    for(int i=0;i<10;i++)
			    	myFile.println();
			    myFile.println("创建用户:"+Main.user);
			    resultFile.close();   
			}   
			catch (Exception e) {   
				JOptionPane.showMessageDialog(null, "创建文件失败!");
			    e.printStackTrace();   
			}    
			return true;
		}
	//打开文件
	public static String open_file(File_Control_Block file,Disk_Block[][] disk,INode[] iNodes){
		StringBuilder text = new StringBuilder();
		INode iNode = iNodes[file.inodenum];
		int length = 0;
		for(int i = 0; i < 13;i++){
			if(i <= 9){
				if(iNode.disk_addr[i]==-1)
					return text.toString();
				int x = iNode.disk_addr[i]/Disk.disk_colume;
				int y = iNode.disk_addr[i]%Disk.disk_colume;
				for(int k = 0; k < Disk_Block.len_disktable;k++){
					text.append(disk[x][y].content[k]);
					length++;
						if(length==iNode.file_length)
							return text.toString();
				}
			}else if(i == 10){
				if(iNode.disk_addr[i]==-1)
					return text.toString();
				int x = iNode.disk_addr[i]/Disk.disk_colume;
				int y = iNode.disk_addr[i]%Disk.disk_colume;
				for(int k = 0; k < Disk_Block.len_disktable;k++){
					if(disk[x][y].content[k]==-1)
						return text.toString();
					int x2 = disk[x][y].content[k]/Disk.disk_colume;
					int y2 = disk[x][y].content[k]%Disk.disk_colume;
					for(int k2 = 0; k2 < Disk_Block.len_disktable;k2++){
						text.append(disk[x2][y2].content[k2]);
						length++;
						if(length==iNode.file_length)
							return text.toString();
					}
				}
			}else if(i == 11){
				if(iNode.disk_addr[i]==-1)
					return text.toString();
				int x = iNode.disk_addr[i]/Disk.disk_colume;
				int y = iNode.disk_addr[i]%Disk.disk_colume;
				for(int k = 0;k < Disk_Block.len_disktable;k++){
					if(disk[x][y].content[k]==(char)-1)
						return text.toString();
					int x2 = disk[x][y].content[k]/Disk.disk_colume;
					int y2 = disk[x][y].content[k]%Disk.disk_colume;
					for(int k2 = 0;k2 < Disk_Block.len_disktable;k2++){
						if(disk[x2][y2].content[k2]==(char)-1)
							return text.toString();
						int x3 = disk[x2][y2].content[k2]/Disk.disk_colume;
						int y3 = disk[x2][y2].content[k2]%Disk.disk_colume;
						for(int k3 = 0; k3 < Disk_Block.len_disktable;k3++){
							text.append(disk[x3][y3].content[k3]);
							length++;
							if(length==iNode.file_length)
								return text.toString();
						}
					}
				}
			}else if(i == 12){
				if(iNode.disk_addr[i]==-1)
					return text.toString();
				int x = iNode.disk_addr[i]/Disk.disk_colume;
				int y = iNode.disk_addr[i]%Disk.disk_colume;
				for(int k = 0;k < Disk_Block.len_disktable;k++){
					if(disk[x][y].content[k]==(char)-1)
						return text.toString();
					int x2 = disk[x][y].content[k]/Disk.disk_colume;
					int y2 = disk[x][y].content[k]%Disk.disk_colume;
					for(int k2 = 0;k2 < Disk_Block.len_disktable;k2++){
						if(disk[x2][y2].content[k2]==(char)-1)
							return text.toString();
						int x3 = disk[x2][y2].content[k2]/Disk.disk_colume;
						int y3 = disk[x2][y2].content[k2]%Disk.disk_colume;
						for(int k3 = 0;k3 < Disk_Block.len_disktable;k3++){
							if(disk[x3][y3].content[k3]==(char)-1)
								return text.toString();
							int x4 = disk[x3][y3].content[k3]/Disk.disk_colume;
							int y4 = disk[x3][y3].content[k3]%Disk.disk_colume;
							for(int k4 = 0; k4 < Disk_Block.len_disktable;k4++){
								text.append(disk[x4][y4].content[k4]);
								length++;
								if(length==iNode.file_length)
									return text.toString();
						}
					}
				}
			}
			}
		}
		return text.toString();
	}
	//重命名文件
	public static void rename_file(String oldname,String newname,Dir_Control_Block curr_dir){
		File_Control_Block file = find_file(oldname, curr_dir);
		file.name = newname;
		String string=new String();
		Dir_Control_Block temp=curr_dir;
		while(temp.father!=temp)
		{
			string=temp.name+"/"+string;
			temp=temp.father;
		}
		string=temp.name+"/"+string; 
		File myFolderPath = new File(string+oldname+".txt");   
		myFolderPath.renameTo(new File(string+newname+".txt")); 
	}
	//在当前目录中搜索文件
	public static File_Control_Block find_file(String name,Dir_Control_Block curr_dir){
		for(int i = 0;i < curr_dir.Sonfile_num;i++){
			if(curr_dir.Son_file[i].name.equals(name))
				return curr_dir.Son_file[i];
		}
		return null;
	}
}
