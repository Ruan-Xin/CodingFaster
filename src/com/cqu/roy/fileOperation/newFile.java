package com.cqu.roy.fileOperation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.StyledDocument;

import com.cqu.roy.attribute.TextAtrr;
import com.cqu.roy.constant.LenthAll;
import com.cqu.roy.highlighting.SyntaxHighlighter;
import com.cqu.roy.historyStorage.Node;
import com.cqu.roy.historyStorage.TextInfo;
import com.cqu.roy.historyStorage.VersionTree;
import com.cqu.roy.mainframe.MainFrame;
import com.cqu.roy.mywdiget.JpathButton;
import com.cqu.roy.mywdiget.MainLayout;
import com.cqu.roy.mywdiget.MyFontStyle;
import com.cqu.roy.mywdiget.MyJTextPane;
import com.cqu.roy.mywdiget.MyLabel;
import com.cqu.roy.mywdiget.MainJpanel;

//newFile
public class newFile implements FileOperation{
	MainFrame mainFrame = MainFrame.getInstance();
	private HashMap<String,VersionTree> hm_name_versiontree;
	@Override
	public void use(JPanel jp,JScrollPane jsp,JPanel northjp,Vector<Integer> close_id,Vector<Integer> untitled_vc
			,Vector<String> sequece_name,String currentAreaName,JpathButton currentButton,HashMap<String, MainJpanel> hmTextArea 
			,HashMap<String, TextAtrr> hm_name_atrr,HashMap<String, JpathButton> hm_name_btn) {
		// TODO Auto-generated method stub
		int id;
		if (close_id.size() == 0) {
			id = untitled_vc.size() + 1;
			untitled_vc.add(id);
		}else {
			id = close_id.get(0);//最先被关闭的id
			close_id.remove((Integer)id);//该id已经被使用，移除！
			untitled_vc.add(id);
		}
		TextAtrr textAtrr = new TextAtrr(false, id, "untitled" + id, null);
		//存在前一个页面，要对前一个页面解锁
		
		if (currentAreaName != null) {
			hmTextArea.get(currentAreaName).getTextPane().setEditable(true);
			jsp.remove(hmTextArea.get(currentAreaName));//同时移除掉，前面一个的页面
		}
		currentAreaName = "untitled" + id;
		mainFrame.setCurrentAreaName(currentAreaName);//修改当前文件路径
		
		hm_name_atrr.put(currentAreaName, textAtrr);
		sequece_name.add(currentAreaName);

		//获取版本树集合
		hm_name_versiontree = mainFrame.getVersionTree();
		VersionTree vst = new VersionTree();
		/*Node的参数：
		 * 1:存储的具体内容
		 * 2：存储的行号位置
		 * 3：下一个操作的行号，若下一个节点不存在，则为-1
		 * 4：上一个操作的行号，若上一个节点为根节点，则为-1
		 * 5：指向父节点的指针
		 * 6：指向子节点的指针*/
		Node firstNode = new Node(new TextInfo(null, 0, 0, 0), 1, -1, -1, null, null);
		vst.InsertNode(0, firstNode);//第一代子节点
		ArrayList<Node> currentNodeSet = vst.getCurrentNodeSet();
		currentNodeSet.add(firstNode);//当前字节点
		hm_name_versiontree.put(currentAreaName, vst);//将其置入版本树的集合
		
		MainJpanel mainjp = new MainJpanel();
		//文本域
		MyJTextPane jtp = new MyJTextPane();
		//背景色的设置
		jtp.setBackground(new Color(50, 50, 50));
		//设置文本监听，当文本改变时候，进行保留字的高亮渲染
		jtp.getDocument().addDocumentListener(new SyntaxHighlighter(jtp));
		jtp.setBorder(null);
		//前景色为白色
		jtp.setCaretColor(Color.WHITE);
		jtp.setBorder(null);
		textPaneStyle(jtp,"Style06");
		JpathButton switchbtn = new JpathButton(currentAreaName,currentAreaName);
		
		switchbtn.setSize(100, LenthAll.BUTTON_HEIGHT+ 40);
		northjp.add(switchbtn);
		
		mainFrame.setCurrentButton(switchbtn);//修改当前按钮
		
		hm_name_btn.put(switchbtn.getText(), switchbtn);
		hmTextArea.put(currentAreaName, mainjp);
		mainFrame.getCurrentButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (mainFrame.getCurrentAreaName() != switchbtn.getMapFilePath()) {
					jsp.remove(hmTextArea.get(mainFrame.getCurrentAreaName()));
					mainFrame.setCurrentAreaName(switchbtn.getMapFilePath());
					mainFrame.setCurrentButton(switchbtn);
					jsp.add(hmTextArea.get(mainFrame.getCurrentAreaName()));
					jsp.setViewportView(hmTextArea.get(mainFrame.getCurrentAreaName()));
					jsp.updateUI();
				}
			}
		});
		
		
		MainLayout mainLayout = new MainLayout();
		mainjp.setLayout(mainLayout);
		JPanel linePanel = new JPanel();
		BoxLayout bLayout = new BoxLayout(linePanel, BoxLayout.Y_AXIS);
		linePanel.setBackground(new Color(50, 50, 50));
		linePanel.setLayout(bLayout);
		linePanel.add(new MyLabel(" 1    "));
		
		//设置约束位置
		mainjp.add(jtp,BorderLayout.CENTER);
		mainjp.add(linePanel,BorderLayout.WEST);
		
		mainjp.setTextPane(jtp);
		mainjp.setLinePanel(linePanel);
		
		jsp.add(mainjp);
		jsp.setViewportView(mainjp);
		jsp.updateUI();
		jp.add(jsp,BorderLayout.CENTER);
		
		jp.updateUI();
		northjp.updateUI();
	}
	private void textPaneStyle(MyJTextPane jtp,String StyleName){
		StyledDocument styledDocument = jtp.getStyledDocument();
		MyFontStyle myFontStyle = new MyFontStyle(styledDocument);
		styledDocument = myFontStyle.getStyleDoc();
		styledDocument.setLogicalStyle(3, styledDocument.getStyle(StyleName));
		jtp.setStyledDocument(styledDocument);
	}
}
