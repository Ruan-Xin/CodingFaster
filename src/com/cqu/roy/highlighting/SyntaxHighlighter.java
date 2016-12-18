package com.cqu.roy.highlighting;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.cqu.roy.main.main;
import com.cqu.roy.mainframe.MainFrame;
import com.cqu.roy.mywdiget.MyJTextPane;

public class SyntaxHighlighter implements DocumentListener{
	private MainFrame mainFrame;
	private Style keywordStyle;//
	private Style normalStyle;
	private Style notesStyle;
	private Style StringStyle;
	private Style IntegerStyle;
	private Style typeStyle;
	private HashSet<String> keyWord;
	private HashSet<String> typeWord;
	private RexPlay rPlay;
	private JTextPane jtp;
	private Vector<Token> vc_lan_token;
	private Vector<Token> vc_normal_token;
	//识别注释
	private final static String notes = "//.*";
	private final static String Integer = "[0-9]+";
	
	public SyntaxHighlighter(JTextPane jtp) {
		// TODO Auto-generated constructor stub
		mainFrame = MainFrame.getInstance();
		
		keywordStyle = ((StyledDocument) jtp.getDocument()).addStyle("Keyword_Style", null);
		typeStyle = ((StyledDocument) jtp.getDocument()).addStyle("Type_Style", null);
		notesStyle = ((StyledDocument) jtp.getDocument()).addStyle("NotesStyle", null);
		IntegerStyle = ((StyledDocument) jtp.getDocument()).addStyle("IntegerStyle", null);
		normalStyle = ((StyledDocument) jtp.getDocument()).addStyle("NormalStyle", null);
		//渲染颜色
		StyleConstants.setForeground(keywordStyle, new Color(205, 50, 120));	
		StyleConstants.setForeground(typeStyle, new Color(175, 238, 238));
		StyleConstants.setForeground(notesStyle, Color.GRAY);
		StyleConstants.setForeground(IntegerStyle, new Color(238,221,130));
		StyleConstants.setForeground(normalStyle, Color.WHITE);
		this.jtp = jtp;
		
		try {
			rPlay = new RexPlay(jtp.getDocument().getText(0, jtp.getDocument().getLength()));
			keyWord = rPlay.getKeyWord();
			typeWord = rPlay.getTypeWord();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void colouring(StyledDocument doc,int pos,int len) throws BadLocationException{
		colouringWord(doc, 0);
	}
	
	public void colouringWord(StyledDocument doc, int pos) throws BadLocationException {
		//注释识别
		Pattern pattern = Pattern.compile(notes);
		SwingUtilities.invokeLater(new ColouringTask(doc, 0, doc.getLength(), normalStyle));
		for(int i = 0; i < vc_lan_token.size();i++){
			Token token = vc_lan_token.get(i);
			Matcher matcher = pattern.matcher(token.getValue());
			if (typeWord.contains(token.getValue())) {
				SwingUtilities.invokeLater(new ColouringTask(doc,token.getLocation()
						,token.getLength() ,typeStyle));
			}
			else if (keyWord.contains(token.getValue())) {
				SwingUtilities.invokeLater(new ColouringTask(doc,token.getLocation()
						,token.getLength() ,keywordStyle));
			}
			else if (matcher.matches()) {
				SwingUtilities.invokeLater(new ColouringTask(doc, token.getLocation(),
						token.getLength(),notesStyle));
			}
			else {
				SwingUtilities.invokeLater(new ColouringTask(doc, 0
						, doc.getLength(), normalStyle));
			}
		}
		for(int i = 0; i < vc_normal_token.size();i++){
			Token token = vc_normal_token.get(i);
			Pattern pattern_int = Pattern.compile(Integer);
			Matcher matcher_int = pattern_int.matcher(token.getValue());
			if (matcher_int.matches()) {
				SwingUtilities.invokeLater(new ColouringTask(doc, token.getLocation(), 
						token.getLength(), IntegerStyle));
			}
		}
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		try {
			try {
				RexPlay rPlay = new RexPlay(jtp.getDocument().getText(0
						, jtp.getDocument().getLength()));
				vc_lan_token = rPlay.getLanToken();
				vc_normal_token = rPlay.getNormalToken();
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			colouring((StyledDocument) e.getDocument(), e.getOffset(), e.getLength());
//			System.out.println("insert:" + e.getOffset());
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		try {
			String newLine = e.getDocument().getText(e.getOffset(), 1);
			if (newLine.equals("\n")) {
				HashMap<String, MyJTextPane> hm_textPane = mainFrame.getHashTextPane();
				hm_textPane.get(mainFrame.getCurrentAreaName()).line();
				System.out.println(hm_textPane.get(mainFrame.getCurrentAreaName()).getLine());
			}
			//System.out.println(e.getDocument().getText(e.getOffset(), 1));
		} catch (BadLocationException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		try {
			Robot robot = new Robot();
			try {
				int offset = e.getOffset();
				String Bracket = e.getDocument().getText(offset, 1);
				if (Bracket.equals("{")) {
					robot.keyPress(93);//"{}"
					robot.keyRelease(93);
				}
				else if (Bracket.equals("(")) {
					//jtp.setCaretPosition(e.getOffset() - 1);
					robot.keyPress(48);//"()"
					robot.keyRelease(48);
				}
				else if (Bracket.equals("\"")) {
					robot.keyPress(KeyEvent.VK_QUOTE);
					robot.keyRelease(KeyEvent.VK_QUOTE); //"\""??
				}
//				else if (Bracket.equals("\'")) {
//					robot.keyPress(KeyEvent.VK_QUOTE);
//					robot.keyRelease(KeyEvent.VK_QUOTE);
//				}
				if (Bracket.equals("\t")) {
				}
				//System.out.println(Bracket);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (AWTException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		try {
			try {
				RexPlay rPlay = new RexPlay(jtp.getDocument().getText(0
						, jtp.getDocument().getLength()));
				vc_lan_token = rPlay.getLanToken();
				vc_normal_token = rPlay.getNormalToken();
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			colouring((StyledDocument) e.getDocument(), e.getOffset(), 0);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
