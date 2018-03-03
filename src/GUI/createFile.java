package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Closeable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.text.JTextComponent;

import DAO.Files;
public class createFile extends JFrame {

	private JPanel contentPane;
	private JTextField name;
	public createFile() {
		this.setTitle("新文件");
		setBounds(100, 100, 511, 477);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(14, 13, 479, 404);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("\u6587\u4EF6\u540D:");
		lblNewLabel.setBounds(14, 35, 53, 18);
		panel.add(lblNewLabel);
		
		name = new JTextField();
		name.setBounds(93, 32, 218, 24);
		panel.add(name);
		name.setColumns(10);
		
		JLabel label = new JLabel("\u6587\u4EF6\u5185\u5BB9\uFF1A");
		label.setBounds(14, 96, 95, 18);
		panel.add(label);
		
		JTextArea content = new JTextArea();
		content.setBounds(24, 127, 403, 160);
		panel.add(content);
		
		JButton button = new JButton("\u786E\u5B9A");
		button.setBounds(161, 321, 113, 27);
		panel.add(button);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String filename = name.getText();
				String filecontent = content.getText();
				if(filename == null || filename.length() == 0){
					JOptionPane.showMessageDialog(panel, "文件名不能为空");
				}else{
					Main.mesg_str = filecontent.toCharArray();
					try {
						Files.create_file(filename, Main.mesg_str.length, Main.curr_dir, Main.free_stack,Main.freeinode_stack, Main.disk,Main.iNodes, Main.disk_remain, Main.mesg_str);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				Main.flush();
				close();
			}
		});
		this.setVisible(true);
	}
	private void close() {
		this.setVisible(false);
	}
}
