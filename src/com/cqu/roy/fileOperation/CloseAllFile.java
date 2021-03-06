package com.cqu.roy.fileOperation;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.cqu.roy.attribute.TextAtrr;
import com.cqu.roy.mainframe.MainFrame;
import com.cqu.roy.mywdiget.JpathButton;
import com.cqu.roy.mywdiget.MainJpanel;

public class CloseAllFile implements FileOperation{
	
	CloseFile cf = new CloseFile();
	MainFrame mainFrame = MainFrame.getInstance();
	@Override
	public void use(JPanel jp, JScrollPane jsp, JPanel northjp, Vector<Integer> close_id, Vector<Integer> untitled_vc,
			Vector<String> sequece_name, String currentAreaName, JpathButton currentButton,
			HashMap<String, MainJpanel> hmTextArea, HashMap<String, TextAtrr> hm_name_atrr,
			HashMap<String, JpathButton> hm_name_btn) {
		// TODO Auto-generated method stub
		
		Vector<String> seq_clone = (Vector<String>) sequece_name.clone();
		for(String s:seq_clone){
			currentAreaName = s;
			currentButton = hm_name_btn.get(s);
			mainFrame.setCurrentAreaName(s);
			mainFrame.setCurrentButton(currentButton);
			cf.use(jp, jsp, northjp, close_id, untitled_vc, sequece_name
					, currentAreaName, currentButton
					, hmTextArea, hm_name_atrr, hm_name_btn);
			
		}
	}


}
