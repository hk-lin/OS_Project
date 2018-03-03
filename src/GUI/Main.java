package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import DAO.Dir;
import DAO.Disk;
import DAO.Files;
import javaBean.Dir_Control_Block;
import javaBean.Disk_Block;
import javaBean.Disk_Remain_Record;
import javaBean.File_Control_Block;
import javaBean.INode;
import javaBean.Stack;

import javax.swing.UIManager;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;

public class Main extends JFrame {
	public static Dir_Control_Block root_dir = new Dir_Control_Block();	//根目录
	public static Dir_Control_Block curr_dir = new Dir_Control_Block();	//当前目录
	public static Disk_Remain_Record disk_remain = new Disk_Remain_Record();	//剩余盘块数
	public static Stack free_stack;	//空闲盘块号栈
	public static String mesg;
	public static char[] mesg_str;
	public static String user="admin";
	public static Disk_Block[][] disk;//盘块
	public static INode[] iNodes;  //INode节点数组
	public static int inodecount = 20;
	public static Stack freeinode_stack;
	public static Dir_Control_Block cutdir = null;
	public static File_Control_Block cutfile=null;
	public static int FILE_FLAG=1;
	public static int DIR_FLAG=2;
	public static int NULL_FLAG=-1;
	public static int cutart=NULL_FLAG;
	private JPanel contentPane;
	private JTable table;
	public static DefaultTableModel dftm = new DefaultTableModel(){
		  public boolean isCellEditable(int row, int column) {
              return false;
          }
	};
	public static JPopupMenu m_popupMenu = new JPopupMenu(); 
	public static JPopupMenu m_createMenu = new JPopupMenu();
	public static String[] columnNames = {"名称", "类型", "时间", "创建用户"};
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
					UIManager.setLookAndFeel(lookAndFeel);
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Create the frame.
	 * @throws Exception 
	 */
	public Main() throws Exception {
		
		
		iNodes=new INode[inodecount];
		for(int i=0; i < inodecount;i++)
		{
			iNodes[i] = new INode();
		}
		freeinode_stack=new Stack(inodecount+1);
		disk=new Disk_Block[Disk.disk_row][Disk.disk_colume];//盘块
		for(int i=0; i<Disk.disk_row; i++)//初始化所有磁盘块
		{
			for(int j=0; j<Disk.disk_colume; j++)
			{
				disk[i][j]=new Disk_Block() ;
			}
		}
		free_stack=new Stack(Disk.disk_colume+1);	//空闲盘块号栈
		Disk.init_disk(disk_remain,iNodes, disk, free_stack,freeinode_stack);
		Dir.init_dir(root_dir, curr_dir);
		curr_dir = root_dir;
		setBackground(new Color(0, 0, 0));
		setTitle("Unix\u6587\u4EF6\u7CFB\u7EDF");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 655, 572);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBounds(14, 24, 609, 488);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(28, 26, 105, 26);
		panel.add(menuBar);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 74, 595, 324);
		panel.add(scrollPane);
		
		table = new JTable();
		table.setModel(dftm);
		scrollPane.setViewportView(table);
		
		createPopupMenu();
		createMenu();
		menuBar.add(getDiskMenu());
		table.addMouseListener(new java.awt.event.MouseAdapter() {  
	        public void mouseClicked(java.awt.event.MouseEvent evt) {  
	            tableMouseClicked(evt);  
	        }  
		});
		scrollPane.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(java.awt.event.MouseEvent evt) {  
		            PaneMouseClicked(evt);  
		        }  
		});
		flush();
	}
	// 获取磁盘操作菜单
	private JMenu getDiskMenu() { 
		JMenu menu = new JMenu();
		menu.setText("查看磁盘空间");
		addDiskAction(menu);	
		return menu;
	}
	private void addDiskAction(JMenu menu) {
		JMenuItem new_item = new JMenuItem("磁盘状态");
		new_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				new CheckDisk();
			}
		});
		menu.add(new_item);
	}
	public static  void flush(){
		dftm.setDataVector(null, columnNames);	
		String[] data = new String[4];
		data[0]="当前目录："+curr_dir.name;
		data[1]=curr_dir.type;
		data[2]=curr_dir.time;
		data[3]=user;
		dftm.addRow(data);
		if(curr_dir.Sondir_num != 0){
		for(int i=0;i<curr_dir.Sondir_num;i++){
			data[0]=curr_dir.Son_dir[i].name;
			data[1]=curr_dir.Son_dir[i].type;
			data[2]=curr_dir.Son_dir[i].time;
			data[3]=user;
			dftm.addRow(data);
		}
		}
		
		for(int i=0;i<curr_dir.Sonfile_num;i++){
			data[0]=curr_dir.Son_file[i].name;
			data[1]=curr_dir.Son_file[i].type;
			data[2]=curr_dir.Son_file[i].time;
			data[3]=user;
			dftm.addRow(data);
		}
		
	}
	private void createPopupMenu() {  
         
          
        JMenuItem delMenItem = new JMenuItem();  
        JMenuItem renameItem = new JMenuItem("重命名");
        JMenuItem cutItem = new JMenuItem("剪切");
        
        delMenItem.setText("  删除  ");  
        cutItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int row = table.getSelectedRow();
				if(row >= 1)
				{
					if(cutart==NULL_FLAG){
						if(((String) table.getValueAt(row, 1)).equals("文件")){
							String filename = (String)table.getValueAt(row, 0);
							cutfile =Files.find_file(filename, curr_dir);
							cutart=FILE_FLAG;
							//删除文件在父目录中的记录
							for(int i=0;i<curr_dir.Sonfile_num;i++){
								if(curr_dir.Son_file[i].name.equals(filename)){
									for(int j=i;j<curr_dir.Sonfile_num-1;j++){
										curr_dir.Son_file[j]=curr_dir.Son_file[j+1];
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
							string=string+filename+".txt";
							File myDelFile = new File(string);   
							try {   
							    myDelFile.delete();   
							}   
							catch (Exception ex) {   
								JOptionPane.showMessageDialog(null, "文件移动失败!");
							    ex.printStackTrace();   
							} 
						}
						else if(((String) table.getValueAt(row, 1)).equals("文件夹")){
							String dirname = (String)table.getValueAt(row, 0);
							cutdir =Dir.find_dir(curr_dir,dirname);
							cutart=DIR_FLAG;
							//删除文件夹在父目录中的记录
							for(int i=0;i<curr_dir.Sondir_num;i++){
								if(curr_dir.Son_dir[i].name.equals(dirname)){
									for(int j=i;j<curr_dir.Sondir_num-1;j++){
										curr_dir.Son_dir[j]=curr_dir.Son_dir[j+1];
									}
									curr_dir.Son_dir[curr_dir.Sondir_num-1]=null;
									curr_dir.Sondir_num--;
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
							string=string+dirname+"/";
							Dir.deleteDir(string);
						}
					  }
					else {
						JOptionPane.showMessageDialog(table, "已有对象在剪贴板！");						
					  }
					}else{
						JOptionPane.showMessageDialog(table, "请选择文件或文件夹！");
					}
					flush();
			}
		});
       
        delMenItem.addActionListener(new java.awt.event.ActionListener() {  
            public void actionPerformed(java.awt.event.ActionEvent evt) {  
                //该操作需要做的事  
            	int row = table.getSelectedRow();
				if (row >= 1) {
					if( ((String) table.getValueAt(row, 1)).equals("文件夹")){
						String old = (String) table.getValueAt(row, 0);
						try {
							Dir.delete_dir(curr_dir, old);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						String name = (String) table.getValueAt(row, 0);
						try {
							Files.delete_file(name, curr_dir, free_stack,freeinode_stack, disk,iNodes, disk_remain);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(curr_dir.Sonfile_num);
					}
				}else{
					JOptionPane.showMessageDialog(table, "请选择子目录！");
				}
				flush();
            }  
        });  
        renameItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int row = table.getSelectedRow();
				if(row >= 1){
					if(((String) table.getValueAt(row, 1)).equals("文件夹")){
						String name = JOptionPane.showInputDialog("文件夹名：");
						String old = (String)table.getValueAt(row, 0);
						if(name.equals("")){
							JOptionPane.showMessageDialog(null, "文件夹名不能为空！");  
						}
						else if(name!=null){
							boolean flag=false;
							for(int i=0;i<curr_dir.Sondir_num;i++){
								if(curr_dir.Son_dir[i].name.equals(name)){
									JOptionPane.showMessageDialog(null, "存在同名文件夹！");
									flag=true;
									break;
								}
							}
							if(!flag)
								Dir.rename_dir(curr_dir, old, name);
						}
					}else{
						String newname=JOptionPane.showInputDialog("新文件名：");
						String old = (String) table.getValueAt(row, 0);
							Files.rename_file(old, newname, curr_dir); 
					}
				}else{
					JOptionPane.showMessageDialog(table, "请选择子目录！");
				}
				flush();
			}
		});
        m_popupMenu.add(delMenItem);  
        m_popupMenu.add(renameItem);
        m_popupMenu.add(cutItem);
    } 
	private void createMenu(){
		JMenu createItem = new JMenu("新建");
		JMenuItem createDirItem = new JMenuItem();
		JMenuItem createFileItem = new JMenuItem();
		JMenuItem putItem = new JMenuItem("粘贴");
		createItem.add(createDirItem);
		createItem.add(createFileItem);
		createDirItem.setText("文件夹");
		createDirItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String s=JOptionPane.showInputDialog("文件夹名：");
				if(s.equals("")){
					JOptionPane.showMessageDialog(null, "文件夹名不能为空！");  
				}
				else if(s!=null){
					boolean flag=false;
					for(int i=0;i<curr_dir.Sondir_num;i++){
						if(curr_dir.Son_dir[i].name.equals(s)){
							JOptionPane.showMessageDialog(null, "存在同名文件夹！");
							flag=true;
							break;
						}
					}
					if(!flag)
						Dir.create_dir(curr_dir,s);
				}
				flush();
			}
		});
		createFileItem.setText("文件");
		createFileItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new createFile();
				flush();
			}
		});
		 putItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(cutart==NULL_FLAG)
					{					
						JOptionPane.showMessageDialog(table, "剪贴板无对象！");
					}
					else if(cutart==FILE_FLAG){
						if(Files.move_file(cutfile, curr_dir)){
							cutart=NULL_FLAG;
							cutfile=null;
						}
						flush();
					}
					else if(cutart==DIR_FLAG){
						if(Dir.move_dir(cutdir,curr_dir)){
							cutart=NULL_FLAG;
							cutdir=null;
						}
						flush();
					}else {
						JOptionPane.showMessageDialog(table, "粘贴失败！");						
					}
				}
			});
		m_createMenu.add(createItem);
		m_createMenu.add(putItem);
	}
	private void PaneMouseClicked(MouseEvent evt){
		//判断是否为鼠标的BUTTON3按钮，BUTTON3为鼠标右键  
	       if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {  
	           //弹出菜单  
	           m_createMenu.show(table, evt.getX(), evt.getY());  
	       }  
	  
	}
	private void tableMouseClicked(java.awt.event.MouseEvent evt) {  
		  
	       mouseRightButtonClick(evt);  
	       mouseDoubleClick(evt);
	}  
	//双击
	private void mouseDoubleClick(MouseEvent evt){
		if((evt.getClickCount())==2){
			int row = table.getSelectedRow();
			if(row>=1){
				if(((String)table.getValueAt(row, 1)).equals("文件夹")){
					String name = (String)table.getValueAt(row, 0);
					for(int i = 0; i < curr_dir.Sondir_num;i++){
						if(curr_dir.Son_dir[i].name.equals(name)){
							curr_dir = curr_dir.Son_dir[i];
							break;
						}
					}
				}else{
					String name = (String) table.getValueAt(row, 0);
					File_Control_Block file=Files.find_file(name, curr_dir);
					new openfile(file,curr_dir);
				}
			}else{
				curr_dir=curr_dir.father;
			}
			flush();
		}

	}
	   private void mouseRightButtonClick(java.awt.event.MouseEvent evt) {  
	       //判断是否为鼠标的BUTTON3按钮，BUTTON3为鼠标右键  
	       if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {  
	           //通过点击位置找到点击为表格中的行  
	           int focusedRowIndex = table.rowAtPoint(evt.getPoint());  
	           if (focusedRowIndex == -1) {  
	               return;  
	           }  
	           //将表格所选项设为当前右键点击的行  
	           table.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);  
	           //弹出菜单  
	           m_popupMenu.show(table,evt.getX(), evt.getY());  
	       }  
	  
	   }  
}
