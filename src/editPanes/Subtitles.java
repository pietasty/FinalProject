package editPanes;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JEditorPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class Subtitles extends JPanel {
	private static Subtitles instance;
	
	private JScrollPane tablePanel;
	private JTable table;
	private DefaultTableModel model;
	private TableModel model1;
	
	private JEditorPane textArea;
	
	private JTextField starthh;
	private JTextField startmm;
	private JTextField startss;

	private JTextField endhh;
	private JTextField endmm;
	private JTextField endss;
	
	private JButton add;
	private JButton delete;
	private JButton edit;
	
	public static Subtitles getInstance(){
		if(instance == null){
			instance = new Subtitles();
		}
		return instance;
	}
	
	private Subtitles(){
		setSize(600,300);
		setLayout(null);
		
		addTable();
		addLabels();
		addTextArea();
		addTimeFields();
		
		addAddButton();
		addDeleteButton();
		addEditButton();
	}
	
	private void addTable(){
		String[] columnNames = {"Start time","End time", "Text"};
		int rows = 0;
		model = new DefaultTableModel(rows,columnNames.length);
		model.setColumnIdentifiers(columnNames);
		model1 = new DefaultTabelModel
		table = new JTable(model);
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
		
		JLabel label = new JLabel("Enter Text:");
		label.setFont(new Font("Dialog", Font.PLAIN, 15));
		label.setBounds(12, 145, 270, 20);
		add(label);
		
	}
	
	private void addTextArea(){
		textArea = new JEditorPane();
		textArea.setBounds(12, 177, 276, 63);
		Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
		textArea.setBorder(border);
		add(textArea);
	}
	
	private void addTimeFields(){
		starthh = new JTextField();
		starthh.setBounds(123, 49, 30, 25);
		restrictTextField(starthh);
		add(starthh);
		
		startmm = new JTextField();
		startmm.setBounds(165, 49, 30, 25);
		restrictTextField(startmm);
		add(startmm);
		
		startss = new JTextField();
		startss.setBounds(207, 49, 30, 25);
		restrictTextField(startss);
		add(startss);
		
		endhh = new JTextField();
		endhh.setBounds(123, 82, 30, 25);
		restrictTextField(endhh);
		add(endhh);
		
		endmm = new JTextField();
		endmm.setBounds(165, 82, 30, 25);
		restrictTextField(endmm);
		add(endmm);
		
		endss = new JTextField();
		endss.setBounds(207, 82, 30, 25);
		restrictTextField(endss);
		add(endss);
	}
	
	private void addAddButton(){
		add = new JButton("Add Subtitles");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.addRow(new Object[]{"Fuckthis","Your mum",textArea.getText()});
			}
		});
		add.setBounds(12, 252, 130, 25);
		add(add);
	}
	
	private void addDeleteButton(){
		delete = new JButton("Delete Subtitles");
		delete.setBounds(470, 252, 130, 25);
		add(delete);
	}
	
	private void addEditButton(){
		edit = new JButton("Edit Subtitles");
		edit.setBounds(300, 252, 130, 25);
		add(edit);
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
}
