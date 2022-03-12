package snmp_protocol;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.ireasoning.protocol.snmp.SnmpSession;

public class SnmpGui extends Frame{
//zaglavlje tabele
	private  String header[] = new String[] { "Origin", "AS-Path", "Next Hop",
            "MED", "Local Preference", "Atomic aggregate","Aggregator AS","Aggregator Address","Best route"};
	
	
	public JTable table;
	//ipAdresa rutera koji pratim
	private Panel basicPanel;
	//ipAdresa koja se unosi
	private String adress;
	
	private SnmpData allData;
	
	private TextField dataPart;//polje za unos ip adrese
	
	public Panel tablePanel;
	
	public JScrollPane scroll;
	
	public SnmpGui() {
		
		setLayout(new FlowLayout(FlowLayout.LEADING));
		setBackground(Color.WHITE);
		setTitle("SNMP PROJECT");
		setBounds(0,0,720,450);
		
		setResizable(false);
		
		createBasicPanel();
		
		tablePanel=new Panel();
		//tablePanel.setSize(1200, 350);
		add(basicPanel);
		add(tablePanel);
		
		//gasenje prozora
		addWindowListener(new WindowAdapter() {
			@Override 
			public void windowClosing(WindowEvent e) {
				
				allData.interrupt();
				
				dispose();
			}
		});
		setVisible(true);
	}
	
	private void createBasicPanel() {
		
		Panel adressPanel=new Panel(new GridLayout(1,2,135,0));
		Panel buttonPanel=new Panel(new GridLayout(1,3));
		
		Label textPart=new Label("Unesite adresu rutera koji pratite");
		textPart.setFont(new Font("Serif",Font.BOLD,15));
		dataPart=new TextField("192.168.10.1");
		
		adressPanel.add(textPart);
		adressPanel.add(dataPart);
		
		Button startButton=new Button("pokreni");
		startButton.setBackground(new Color(255,102,178));
		buttonPanel.add(startButton,BorderLayout.CENTER);
		
		basicPanel=new Panel(new GridLayout(2,1));
		//basicPanel.setSize(700,100);
		basicPanel.add(adressPanel);
		basicPanel.add(buttonPanel);
		basicPanel.setBackground(new Color(127, 255, 0));
		basicPanel.setVisible(true);
		
		//klik na pokreni-krecem da pratim podatke
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				adress=dataPart.getText();//iz polja za ip
				
				allData=new SnmpData(adress,SnmpGui.this);//citac podataka
				
			}
			
		});
		
	}
	
	 public JTable getNewRenderedTable(final JTable table) {
	        table.setDefaultRenderer(Object.class,  new DefaultTableCellRenderer(){
	            @Override
	            public Component getTableCellRendererComponent(JTable table,
	                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {
	                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	               
	                String status = (String)table.getModel().getValueAt(row,8);
	                if ("true".equals(status)) {
	                    setBackground(new Color(255, 235, 205));
	                    //setForeground(Color.WHITE);
	                } else {
	                    setBackground(table.getBackground());
	                    setForeground(table.getForeground());
	                }       
	                return this;
	            }   
	        });
	        return table;
	    }
	 
	public JScrollPane createTablePanel(Object[][] objects) {
		
		table=new JTable(objects,header);
		
		table.setPreferredScrollableViewportSize(new Dimension(700,300));
		table.setFillsViewportHeight(true);
		
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setWidth(50);
		
		table.setRowHeight(50);
		
		table=getNewRenderedTable(table);
		scroll=new JScrollPane(table);
		
		tablePanel.add(scroll);
		return scroll;
	}
	public static void main(String[] args) {
		
		try {
			
			SnmpSession.loadMib2();
			SnmpSession.loadMib("/home/korisnik/Downloads/BGP4-MIB.mib");
		} catch (IOException | ParseException e) {
			System.out.println("Can't find mib in specified path");
		}
		
		new SnmpGui();
	}
}
