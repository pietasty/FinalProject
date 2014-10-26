package editPanes;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JEditorPane;
import javax.swing.JButton;

import functionality.subtitles.SubtitlesReader;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the GUI component of the subtitles functionality
 * @author ywu591
 *
 */
public class Subtitles extends JPanel {
	private static Subtitles instance;
	
	private JScrollPane tablePanel;
	private JTable table;
	private DefaultTableModel model;
	
	private JEditorPane textArea;
	
	private JTextField starthh;
	private JTextField startmm;
	private JTextField startss;

	private JTextField endhh;
	private JTextField endmm;
	private JTextField endss;
	
	private List<JTextField> timeFields;
	
	private JButton add;
	private JButton delete;
	private JButton edit;
	private JButton savechanges;
	
	public static Subtitles getInstance(){
		if(instance == null){
			instance = new Subtitles();
		}
		return instance;
	}
	
	private Subtitles(){
		setSize(600,300);
		setLayout(null);
		
		timeFields = new ArrayList<JTextField>();
		
		addTable();
		addLabels();
		addTextArea();
		addTimeFields();
		setupTextFields();
		
		addAddButton();
		addDeleteButton();
		addEditButton();
	}
	
	private void addTable(){
		String[] columnNames = {"Start","End", "Text"};
		int rows = 0;
		
		//Reference: http://www.java2s.com/Code/Java/Swing-JFC/DisablingUserEditsinaJTablewithDefaultTableModel.htm
		model = new DefaultTableModel(rows,columnNames.length){
		      public boolean isCellEditable(int rowIndex, int mColIndex) {
			        return false;
			      }
		};
		model.setColumnIdentifiers(columnNames);
		table = new JTable(model);
		
		//Reference:http://stackoverflow.com/questions/4399975/jtable-no-selected-row
		ListSelectionModel listSelectionModel = table.getSelectionModel();
		listSelectionModel.addListSelectionListener(new ListSelectionListener() {
		        public void valueChanged(ListSelectionEvent e) { 
		            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		            delete.setEnabled(!lsm.isSelectionEmpty());
		            edit.setEnabled(!lsm.isSelectionEmpty());
		        }
		});
		
		//Reference: http://stackoverflow.com/questions/953972/java-jtable-setting-column-width
		table.getColumnModel().getColumn(0).setPreferredWidth(72);
		table.getColumnModel().getColumn(1).setPreferredWidth(72);
		table.getColumnModel().getColumn(2).setPreferredWidth(154);
		
		tablePanel = new JScrollPane(table);
		tablePanel.setBounds(300, 0, 300, 240);
		add(tablePanel);
	}
	
	private void addLabels(){
		JLabel lblSubtitles = new JLabel("Subtitles");
		lblSubtitles.setFont(new Font("Dialog", Font.PLAIN, 20));
		lblSubtitles.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubtitles.setBounds(12, 12, 270, 25);
		add(lblSubtitles);
		
		JLabel lblStartTime = new JLabel("Start Time:");
		lblStartTime.setFont(new Font("Dialog", Font.PLAIN, 15));
		lblStartTime.setBounds(12, 49, 93, 20);
		add(lblStartTime);
		
		JLabel lblEndtime = new JLabel("End Time:");
		lblEndtime.setFont(new Font("Dialog", Font.PLAIN, 15));
		lblEndtime.setBounds(12, 81, 93, 20);
		add(lblEndtime);
		
		JLabel lblEnterTimeIn = new JLabel("Enter time in the format: hh:mm:ss");
		lblEnterTimeIn.setFont(new Font("Dialog", Font.PLAIN, 15));
		lblEnterTimeIn.setBounds(12, 113, 270, 20);
		add(lblEnterTimeIn);
		
		JLabel label = new JLabel("Enter Text (Max Char 40):");
		label.setFont(new Font("Dialog", Font.PLAIN, 15));
		label.setBounds(12, 145, 270, 20);
		add(label);
		
		JLabel colon = new JLabel(":");
		colon.setFont(new Font("Dialog", Font.PLAIN, 15));
		colon.setBounds(157,49,5,25);
		add(colon);
		
		JLabel colon1 = new JLabel(":");
		colon1.setFont(new Font("Dialog", Font.PLAIN, 15));
		colon1.setBounds(198,49,5,25);
		add(colon1);
		
		JLabel colon2 = new JLabel(":");
		colon2.setFont(new Font("Dialog", Font.PLAIN, 15));
		colon2.setBounds(157,82,5,25);
		add(colon2);
		
		JLabel colon3 = new JLabel(":");
		colon3.setFont(new Font("Dialog", Font.PLAIN, 15));
		colon3.setBounds(198,82,5,25);
		add(colon3);
	}
	
	private void addTextArea(){
		textArea = new JEditorPane();
		textArea.setBounds(12, 177, 276, 63);
		Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
		textArea.setBorder(border);
		textArea.setDocument(new JTextFieldLimit(40));
		add(textArea);
	}
	
	private void addTimeFields(){
		starthh = new JTextField();
		starthh.setBounds(123, 49, 30, 25);
		restrictTextField(starthh);
		add(starthh);
		timeFields.add(starthh);
		
		startmm = new JTextField();
		startmm.setBounds(165, 49, 30, 25);
		restrictTextField(startmm);
		add(startmm);
		timeFields.add(startmm);
		
		startss = new JTextField();
		startss.setBounds(207, 49, 30, 25);
		restrictTextField(startss);
		add(startss);
		timeFields.add(startss);
		
		endhh = new JTextField();
		endhh.setBounds(123, 82, 30, 25);
		restrictTextField(endhh);
		add(endhh);
		timeFields.add(endhh);
		
		endmm = new JTextField();
		endmm.setBounds(165, 82, 30, 25);
		restrictTextField(endmm);
		add(endmm);
		timeFields.add(endmm);
		
		endss = new JTextField();
		endss.setBounds(207, 82, 30, 25);
		restrictTextField(endss);
		add(endss);
		timeFields.add(endss);
	}
	
	//TODO
	private void addAddButton(){
		add = new JButton("Add Subtitles");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(addSubtitlesErrorChecking()){
					String start = formatTime(starthh.getText(),startmm.getText(),startss.getText());
					String end = formatTime(endhh.getText(),endmm.getText(),endss.getText());
					model.addRow(new Object[]{start,end,textArea.getText()});
					setupTextFields();
					savechanges.setEnabled(false);
				}
			}
		});
		add.setBounds(12, 252, 130, 25);
		add(add);
	}
	
	private void addDeleteButton(){
		delete = new JButton("Delete Subtitles");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				model.removeRow(row);		
			}
		});
		delete.setBounds(470, 252, 130, 25);
		delete.setEnabled(false);
		add(delete);
	}
	
	private void addEditButton() {
		edit = new JButton("Edit Subtitles");
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				String start = (String) model.getValueAt(row, 0);
				String end = (String) model.getValueAt(row, 1);
				String text = (String) model.getValueAt(row, 2);
				textArea.setText(text);
				String[] startsplit = start.split(":");
				starthh.setText(startsplit[0]);
				startmm.setText(startsplit[1]);
				startss.setText(startsplit[2]);
				String[] endsplit = end.split(":");
				endhh.setText(endsplit[0]);
				endmm.setText(endsplit[1]);
				endss.setText(endsplit[2]);
				savechanges.setEnabled(true);
			}
		});
		edit.setBounds(300, 252, 130, 25);
		edit.setEnabled(false);
		add(edit);

		savechanges = new JButton("Save changes");
		savechanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (addSubtitlesErrorChecking()) {
					int row = table.getSelectedRow();
					String start = formatTime(starthh.getText(),
							startmm.getText(), startss.getText());
					String end = formatTime(endhh.getText(), endmm.getText(),
							endss.getText());
					model.setValueAt(start, row, 0);
					model.setValueAt(end, row, 1);
					model.setValueAt(textArea.getText(), row, 2);
					setupTextFields();
					savechanges.setEnabled(false);
				}
			}
		});
		savechanges.setBounds(158, 252, 130, 25);
		savechanges.setEnabled(false);
		add(savechanges);
	}
	
	private void setupTextFields(){
		starthh.setText("00");
		startmm.setText("00");
		startss.setText("00");
		endhh.setText("00");
		endmm.setText("00");
		endss.setText("00");
		textArea.setText("");
	}
	
	private void restrictTextField(JTextField jtf) {
		jtf.setDocument(new JTextFieldLimit(2));
		jtf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!(Character.isDigit(c)) || (c == KeyEvent.VK_BACK_SLASH)
						|| (c == KeyEvent.VK_DELETE)) {
					e.consume();
				}

			}
		});
	}
	
	private String formatTime(String hh, String mm, String ss){
		String h = String.format("%02d", Integer.parseInt(hh));
		String m = String.format("%02d", Integer.parseInt(mm));
		String s = String.format("%02d", Integer.parseInt(ss));
		return h + ":" + m + ":"+ s;
	}
	
	/**
	 * Checks if the user gave a valid format for time
	 */
	private boolean checkValidTime() {
		for (JTextField jtf : timeFields){
			if (Integer.parseInt(jtf.getText()) > 60){
				return false;
			} 
		}
		return true;
	}

	/**
	 * Checks if the user gave the starting time to be less than the ending time
	 */
	private boolean checkValidMath() {
		int start = 0;
		int end = 0;

		start = Integer.parseInt(starthh.getText()) * 60 * 60
				+ Integer.parseInt(startmm.getText()) * 60
				+ Integer.parseInt(startss.getText());
		end = Integer.parseInt(endhh.getText()) * 60 * 60
				+ Integer.parseInt(endmm.getText()) * 60
				+ Integer.parseInt(endss.getText());
		if (end - start < 0) {
			return false;
		}
		return true;
	}
	
	private boolean addSubtitlesErrorChecking(){
		//Assumes that if there is no input for time inputs, the value is 0
		for(JTextField jtf : timeFields){
			if(jtf.getText().trim().equals("")){
				jtf.setText("00");
			}
		}
		//Checks if time is between 0 and 59
		if (!checkValidTime()){
			JOptionPane.showMessageDialog(
					null,
					"Please enter a valid time between 0 and 60",
					"Error!", JOptionPane.ERROR_MESSAGE);
			return false;
		//Checks that the start time is a earlier time than the end time
		} else if (!checkValidMath()){
			JOptionPane.showMessageDialog(null, "Your math is horrible\n" +
					"The start time can't be a later time than the end time",
					"Error!", JOptionPane.ERROR_MESSAGE);
			return false;
		} else if(textArea.getText().trim().equals("")){
			JOptionPane.showMessageDialog(null, "Please enter subtitles to add",
					"Error!", JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * This method checks if the user has actually added subtitles or not 
	 * (mainly from the fact that if the JTable has a row or not).
	 */
	public boolean hasSubtitles(){
		int rows = model.getRowCount();
		if(rows == 0){
			return false;
		}
		return true;
	}
	
	/**
	 * return the a list of startTimes 
	 */
	public List<String> getStartTimes(){
		List<String> output = new ArrayList<String>();
		for(int i = 0; i < model.getRowCount(); i++) {
			output.add((String) model.getValueAt(i, 0));
		}
		return output;
	}
	
	/**
	 * return the a list of endTimes 
	 */
	public List<String> getEndTimes(){
		List<String> output = new ArrayList<String>();
		for(int i = 0; i < model.getRowCount(); i++) {
			output.add((String) model.getValueAt(i, 1));
		}
		return output;
		
	}
	
	/**
	 * return the a list of text
	 */
	public List<String> getText(){
		List<String> output = new ArrayList<String>();
		for(int i = 0; i < model.getRowCount(); i++) {
			output.add((String) model.getValueAt(i, 2));
		}
		return output;
	}
	
	public void updateGUI(){
		for (int i = model.getRowCount() - 1; i >= 0; i--) {
		    model.removeRow(i);
		}
		
		List<String> startTime = SubtitlesReader.getStartTime();
		List<String> endTime = SubtitlesReader.getEndTime();
		List<String> text = SubtitlesReader.getText();
		
		for(int i = 0; i<startTime.size();i++){
			String[] split = startTime.get(i).split("\\.");
			String start = split[0];
			split = endTime.get(i).split("\\.");
			String end = split[0];
			
			model.addRow(new Object[]{start,end ,text.get(i)});
		}
		
	}
}
