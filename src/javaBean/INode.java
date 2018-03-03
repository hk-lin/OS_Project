package javaBean;
/*
 * 使用索引节点管理
 * INode:
 * disk_addr[13]:UNIX文件系统INode结点存放磁盘块地址，混合索引分配方式
 *          0-9:直接地址
 *          10：一次间接寻址
 *          11：二次间接寻址
 *          12：三次间接寻址
 * file_length:文件字节数
 * use_disknum:所用磁盘块数目
 * open_flag:文件打开状态，1为已打开
 */
public class INode {
	public int[] disk_addr = new int[13];
	public int inode_num;
	public int file_length;
	public int use_disknum;
	public int open_flag;
	public INode(){
		for(int i = 0; i < 13;i++){
			disk_addr[i] = -1;
		}
	}
}
