package DAO;
import javax.security.auth.kerberos.KerberosKey;
import javax.swing.JOptionPane;
import javax.swing.text.AbstractDocument.Content;

import GUI.Main;
import javaBean.*;

import java.lang.Math;
public class Disk {
	public static int disk_row=10;//磁盘组数
	public static int disk_colume=10;//每组含有的磁盘的数目
	public static void init_disk(Disk_Remain_Record disk_remain,INode[] iNodes,Disk_Block[][] disk,Stack free_stack,Stack freeinode_stack) throws Exception{
		//初始化inode数组
		for(int i = 0;i < Main.inodecount;i++){
			iNodes[i].inode_num = i;
		}
		for(int j=Main.inodecount-1; j>=0; j--)  //初始化空闲盘块号栈
		{
			freeinode_stack.push(iNodes[j].inode_num);
		}
		freeinode_stack.objs[Main.inodecount] = Main.inodecount; 
		//初始化磁盘链表
		disk_remain.value = disk_row*disk_colume;
		for(int i = 0; i < disk_row;i++){
			for(int j = 0; j < disk_colume;j++){
				disk[i][j].disk_num = i*disk_colume + j;
			}
		}
		for(int m=0; m<disk_row-1; m++)
		{
			for(int i=0; i<disk_colume; i++)
			{
				disk[m][disk_colume-1].content[i]=(char)disk[m+1][i].disk_num;
			}
		}
		for(int j=disk_colume-1; j>=0; j--)  //初始化空闲盘块号栈
		{
			free_stack.push(disk[0][j].disk_num);
		}
		free_stack.objs[disk_colume]=disk_colume;
	}
	//请求磁盘块
	public static int request_disk(Stack free_stack,Disk_Block[][] disk,Disk_Remain_Record disk_remain) throws Exception{
		int nowdisk = -1;
		//空闲栈未分配完时
		if(free_stack.objs[disk_colume] > 1){
			nowdisk = free_stack.pop();
			free_stack.objs[disk_colume]--;
		}
		//空闲栈分配完，更新空闲栈
		else{
			int next_disk = free_stack.pop();
			int next_colume = next_disk/disk_colume;
			int next_row = next_disk%disk_colume;
			nowdisk = next_disk;
			//更新空闲栈
			for(int i = disk_colume-1;i >= 0 ;i--){
				free_stack.push((int)disk[next_colume][next_row].content[i]);
				disk[next_colume][next_row].content[i] = (char)-1;
			}
			free_stack.objs[disk_colume]=disk_colume;
		}
		disk_remain.value--;
		return nowdisk;
	}
	//回收磁盘块
	public static void release_disk(int disknum,Stack free_stack,Disk_Block[][] disk,Disk_Remain_Record disk_remain) throws Exception{
		int colume = disknum/disk_colume;
		int row = disknum%disk_colume;
		//空闲栈满
		if(free_stack.objs[disk_colume]>=disk_colume){
			for(int i = 0;i < disk_colume;i++){
				disk[colume][row].content[i] = (char)free_stack.pop();
			}
			free_stack.objs[disk_colume] = 1;
			free_stack.push(disknum);
			disk_remain.value++;
		}
		//空闲栈未满
		else{
			free_stack.objs[disk_colume]++;
			free_stack.push(disknum);
			for(int i = 0;i < disk_colume;i++){
				disk[colume][row].content[i] = (char)-1;
			}
			disk_remain.value++;
		}
	}
	//从文件回收磁盘块
	public static boolean reback_disk(int iNodenum,Stack free_stack,Stack freeinode_stack,Disk_Block[][] disk,INode[] iNodes,Disk_Remain_Record disk_remain) throws Exception{
		for(int i = 0;i < 13;i++){
			if(i <= 9){
				if(iNodes[iNodenum].disk_addr[i]==-1)
					return true;
				release_disk(iNodes[iNodenum].disk_addr[i], free_stack, disk, disk_remain);
				iNodes[iNodenum].disk_addr[i] = -1;
			}else if(i == 10){
				if(iNodes[iNodenum].disk_addr[i]==-1)
					return true;
				int x = iNodes[iNodenum].disk_addr[i]/disk_colume;
				int y = iNodes[iNodenum].disk_addr[i]%disk_colume;
				for(int k = 0; k < Disk_Block.len_disktable;k++){
					if(disk[x][y].content[k]==(char)-1)
						break;
					release_disk(disk[x][y].content[k], free_stack, disk, disk_remain);
					disk[x][y].content[k]= (char)-1;
				}
				release_disk(iNodes[iNodenum].disk_addr[i], free_stack, disk, disk_remain);
				iNodes[iNodenum].disk_addr[i] = -1;
			}else if(i == 11){
				if(iNodes[iNodenum].disk_addr[i]==-1)
					return true;
				int x = iNodes[iNodenum].disk_addr[i]/disk_colume;
				int y = iNodes[iNodenum].disk_addr[i]%disk_colume;
				for(int k = 0;k < Disk_Block.len_disktable;k++){
					if(disk[x][y].content[k]==(char)-1)
						break;
					int x2 = disk[x][y].content[k]/disk_colume;
					int y2 = disk[x][y].content[k]%disk_colume;
					for(int k2 = 0;k2 < Disk_Block.len_disktable;k2++){
						if(disk[x2][y2].content[k2]==(char)-1)
							break;
						release_disk(disk[x2][y2].content[k2], free_stack, disk, disk_remain);
						disk[x2][y2].content[k2] = (char)-1;
					}
					release_disk(disk[x][y].content[k], free_stack, disk, disk_remain);
					disk[x][y].content[k] = (char)-1;
				}
				release_disk(iNodes[iNodenum].disk_addr[i], free_stack, disk, disk_remain);
				iNodes[iNodenum].disk_addr[i] = -1;
			}else if(i == 12){
				if(iNodes[iNodenum].disk_addr[i]==-1)
					return true;
				int x = iNodes[iNodenum].disk_addr[i]/disk_colume;
				int y = iNodes[iNodenum].disk_addr[i]%disk_colume;
				for(int k = 0;k < Disk_Block.len_disktable;k++){
					if(disk[x][y].content[k]==(char)-1)
						break;
					int x2 = disk[x][y].content[k]/disk_colume;
					int y2 = disk[x][y].content[k]%disk_colume;
					for(int k2 = 0;k2 < Disk_Block.len_disktable;k2++){
						if(disk[x2][y2].content[k2]==(char)-1)
							break;
						int x3 = disk[x2][y2].content[k2]/disk_colume;
						int y3 = disk[x2][y2].content[k2]%disk_colume;
						for(int k3 = 0;k3 < Disk_Block.len_disktable;k3++){
							if(disk[x3][y3].content[k3]==(char)-1)
								break;
							release_disk(disk[x3][y3].content[k3], free_stack, disk, disk_remain);
							disk[x3][y3].content[k3] = (char)-1;
						}
						release_disk(disk[x2][y2].content[k2], free_stack, disk, disk_remain);
						disk[x2][y2].content[k2] = (char)-1;
					}
					release_disk(disk[x][y].content[k], free_stack, disk, disk_remain);
					disk[x][y].content[k] = (char)-1;
				}
				release_disk(iNodes[iNodenum].disk_addr[i], free_stack, disk, disk_remain);
				iNodes[iNodenum].disk_addr[i] = -1;
			}
		}
		return false;
				
	}
	//为文件分配磁盘块
	public static boolean alloc_disk(int iNodenum,int num,Disk_Remain_Record disk_remain,Stack free_stack,Disk_Block[][] disk,INode[] iNodes,char[] mesg_str) throws Exception{
		if(num > 1120){
			JOptionPane.showConfirmDialog(null, "文件过大，超出系统范围");
			return false;
		}
		if(num > 120){
			int temp = (int)Math.ceil((num-120)/10);
			num = num + (int)Math.ceil(temp/10 + 1);
		}else if(num > 20){
			int temp = (int)Math.ceil((num - 20)/10);
			num = num + temp + 1;
		}else if(num > 10){
			num = num + 1;
		}
		if(num > disk_remain.value){
			JOptionPane.showConfirmDialog(null, "磁盘空间不足!");
			return false;
		}
		//分配磁盘块
		int count = 1;
		for(int i = 0; i < 13;i++){
			if(i <= 9){
				iNodes[iNodenum].disk_addr[i] = request_disk(free_stack, disk, disk_remain);
				num--;
				int x = iNodes[iNodenum].disk_addr[i]/disk_colume;
				int y = iNodes[iNodenum].disk_addr[i]%disk_colume;
				for(int k = 0;k < Disk_Block.len_disktable;k++){
					disk[x][y].content[k] = mesg_str[(count-1)*Disk_Block.len_disktable+k];
					if((count-1)*Disk_Block.len_disktable+k==mesg_str.length-1)
						return true;
					
				}
				count++;
				if(num<=0)
					return true;
			}else if(i == 10){
				iNodes[iNodenum].disk_addr[i] = request_disk(free_stack, disk, disk_remain);
				num--;
				int x = iNodes[iNodenum].disk_addr[i]/disk_colume;
				int y = iNodes[iNodenum].disk_addr[i]%disk_colume;
				for(int k = 0;k < Disk_Block.len_disktable;k++){
					disk[x][y].content[k] = (char)request_disk(free_stack, disk, disk_remain);
					num--;
					int x2 = disk[x][y].content[k]/disk_colume;
					int y2 = disk[x][y].content[k]%disk_colume;
					for(int k2=0;k2 < Disk_Block.len_disktable;k2++){
						disk[x2][y2].content[k2] = mesg_str[(count-1)*Disk_Block.len_disktable+k2];
						if((count-1)*Disk_Block.len_disktable+k2==mesg_str.length-1)
							return true;
					}
					count++;
					if(num<=0)
						return true;
				}
			}else if(i == 11){
				iNodes[iNodenum].disk_addr[i] = request_disk(free_stack, disk, disk_remain);
				int x = iNodes[iNodenum].disk_addr[i]/disk_colume;
				int y = iNodes[iNodenum].disk_addr[i]%disk_colume;
				for(int k = 0;k < Disk_Block.len_disktable;k++){
					disk[x][y].content[k] = (char)request_disk(free_stack, disk, disk_remain);
					num--;
					int x2 = disk[x][y].content[k]/disk_colume;
					int y2 = disk[x][y].content[k]%disk_colume;
					for(int k2=0;k2 < Disk_Block.len_disktable;k2++){
						disk[x2][y2].content[k2] = (char)request_disk(free_stack, disk, disk_remain);
						num--;
						int x3 = disk[x2][y2].content[k2]/disk_colume;
						int y3 = disk[x2][y2].content[k2]%disk_colume;
						for(int k3=0;k3 < Disk_Block.len_disktable;k3++){
							disk[x3][y3].content[k3] = mesg_str[(count-1)*Disk_Block.len_disktable+k3];
							if((count-1)*Disk_Block.len_disktable+k3==mesg_str.length-1)
								return true;
						}
						count++;
						if(num<=0)
							return true;
					}
				}
			}else if(i == 12){
				iNodes[iNodenum].disk_addr[i] = request_disk(free_stack, disk, disk_remain);
				int x = iNodes[iNodenum].disk_addr[i]/disk_colume;
				int y = iNodes[iNodenum].disk_addr[i]%disk_colume;
				for(int k = 0;k < Disk_Block.len_disktable;k++){
					disk[x][y].content[k] = (char)request_disk(free_stack, disk, disk_remain);
					num--;
					int x2 = disk[x][y].content[k]/disk_colume;
					int y2 = disk[x][y].content[k]%disk_colume;
					for(int k2=0;k2 < Disk_Block.len_disktable;k2++){
						disk[x2][y2].content[k2] = (char)request_disk(free_stack, disk, disk_remain);
						num--;
						int x3 = disk[x2][y2].content[k2]/disk_colume;
						int y3 = disk[x2][y2].content[k2]%disk_colume;
						for(int k3=0;k3 < Disk_Block.len_disktable;k3++){
							disk[x3][y3].content[k3] = (char) request_disk(free_stack, disk, disk_remain);
							num--;
							int x4 = disk[x3][y3].content[k3]/disk_colume;
							int y4 = disk[x3][y3].content[k3]%disk_colume;
							for(int k4 = 0;k4 < Disk_Block.len_disktable;k4++){
								disk[x4][y4].content[k4] = mesg_str[(count-1)*Disk_Block.len_disktable+k4];
								if((count-1)*Disk_Block.len_disktable+k4==mesg_str.length-1)
									return true;
							}
							count++;
							if(num<=0)
								return true;
						}
					}
			   }
		   }
	    }
		return true;
  }
}
