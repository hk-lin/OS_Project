package GUI;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import DAO.Disk;
import circleprogressbarorg.CircleProgressBar;
public class CheckDisk extends JFrame {
	private JPanel jp;
	private JLabel now_label;
	private JLabel used_label;
	private JLabel remain_label;
	private CircleProgressBar progressBar;
	public  CheckDisk(){
		this.setTitle("磁盘状态");
		this.setBounds(300, 250, 650, 464);
		jp=new JPanel();
		jp.setLayout(null);
		now_label=new JLabel("磁盘使用情况");
		now_label.setBounds(266, 13,120, 41);
		used_label=new JLabel("已使用：");
		used_label.setBounds(37, 365, 153, 27);
		remain_label=new JLabel("剩   余：");
		remain_label.setBounds(449, 365, 136, 27);
		progressBar=new CircleProgressBar();
		progressBar.setBounds(136,81,342,238);
		progressBar.setMinimumProgress(0); //设置最小进度值
		progressBar.setMaximumProgress(100); //设置最大进度值
		progressBar.setProgress(10); //设置当前进度值
		progressBar.setBackgroundColor(new Color(209, 206, 200)); //设置背景颜色
		progressBar.setForegroundColor(new Color(172, 168, 163)); //设置前景颜色
		progressBar.setDigitalColor(Color.BLACK); //设置数字颜色
		jp.add(now_label);
		jp.add(used_label);
		jp.add(remain_label);
		jp.add(progressBar);
		getContentPane().add(jp);
		int all=Disk.disk_row*Disk.disk_colume;
		int used=all-Main.disk_remain.value;
		int p=(int)(((double)used/all)*100);
		remain_label.setText("剩   余："+Main.disk_remain.value*20+"字节");
		used_label.setText("已使用："+used*20+"字节");
		this.progressBar.setProgress(p);
		this.progressBar.setVisible(true);
		this.setVisible(true);
	}

}
