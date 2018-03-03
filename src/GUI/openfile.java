package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import DAO.Files;
import javaBean.Dir_Control_Block;
import javaBean.File_Control_Block;
import GUI.Main;

public class openfile  extends JFrame{
	private JPanel jp;
	private JLabel name_label;
	private JLabel text_label;
	private JTextField name;
	private JTextArea text;
	private JButton okButton;
	private JScrollPane scroller;
	
	public openfile(File_Control_Block file,Dir_Control_Block curr_dir){
		this.setTitle("打开文件");
		this.setBounds(300, 250, 438, 322);
		jp=new JPanel();
		jp.setLayout(null);
		name_label=new JLabel("文件名：");
		name_label.setBounds(5, 3, 60, 20);
		text_label=new JLabel("文件内容：");
		text_label.setBounds(5, 30, 100, 20);
		name=new JTextField(15);
		name.setBounds(70, 3, 100, 20);
		text=new JTextArea();
		scroller=new JScrollPane(text);
		scroller.setBounds(30, 55, 330, 150);
		okButton=new JButton("保存");
		okButton.setBounds(150, 220, 80, 30);
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String newname=name.getText();
				String newtext=text.getText();
				if(newname==file.name){
					try {
						Files.delete_file(file.name,curr_dir,Main.free_stack,Main.freeinode_stack,Main.disk,Main.iNodes,Main.disk_remain);
						Files.create_file(newname, newtext.length(),curr_dir,Main.free_stack,Main.freeinode_stack, Main.disk,Main.iNodes, Main.disk_remain,newtext.toCharArray());
						JOptionPane.showMessageDialog(null, "修改成功！");
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(null, "保存文件失败!");			
					}
				}
				else {
					boolean flag=false;
					for(int i=0;i<curr_dir.Sonfile_num;i++)	{
						if(curr_dir.Son_file[i].name.equals(newname) && file!=curr_dir.Son_file[i]){
							JOptionPane.showMessageDialog(null, "存在同名文件！");
							flag=true;
							break;
						}
					}
					if(!flag){
						try {
							Files.delete_file(file.name,curr_dir,Main.free_stack,Main.freeinode_stack,Main.disk,Main.iNodes,Main.disk_remain);
							Files.create_file(newname, newtext.length(),curr_dir,Main.free_stack,Main.freeinode_stack, Main.disk,Main.iNodes, Main.disk_remain,newtext.toCharArray());
							JOptionPane.showMessageDialog(null, "修改成功！");
						} catch (Exception e2) {
							JOptionPane.showMessageDialog(null, "保存文件失败!");			
						}							}
					}
				close();
			}
		});
		
		jp.add(name_label);
		jp.add(name);
		jp.add(text_label);
		jp.add(scroller);
		jp.add(okButton);
		getContentPane().add(jp);
		
		name.setText(file.name);
		text.setText(Files.open_file(file, Main.disk,Main.iNodes));
		
		this.setVisible(true);
	}
	private void close() {
		this.dispose();
	}

}
