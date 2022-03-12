package snmp_protocol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;

import javax.swing.JTextArea;

import com.ireasoning.protocol.snmp.SnmpConst;
import com.ireasoning.protocol.snmp.SnmpSession;
import com.ireasoning.protocol.snmp.SnmpTableModel;
import com.ireasoning.protocol.snmp.SnmpTarget;

public class SnmpData extends Thread{
 
	private SnmpSession session;
	private SnmpTarget target;
	//za gui
	private Object[][] tableData;
	
	private SnmpGui gui;
	//gnsa
	private SnmpTableModel tableWithData;
	
	private static String tr="true";
	private static String fa="false";
	
	public SnmpData(String routerIp,SnmpGui g) {
		gui=g;
		//1-ipAdresa rutera,2-broj porta,3,4-comunity vrednost,5-verzija snmp protokola
		target=new SnmpTarget(routerIp,161,"si2019","si2019",SnmpConst.SNMPV2);
		try {
			//kreiram sesiju targetu(ruteru)
			session=new SnmpSession(target);
		} catch (IOException e) {
			System.out.println("Problem with session estabilishing");
		}
		start();
	}
	
	public Object[][] getTableData(){
		return tableData;
	}
	
	@Override
	public void run() {
		try {
			
			while(!Thread.interrupted()) {
				
			tableWithData=session.snmpGetTable(".1.3.6.1.2.1.15.6");
			
			tableData=new Object[tableWithData.getRowCount()][9];
			
			for(int i=0;i<tableWithData.getRowCount();i++) {
				tableData[i][0]=tableWithData.getValueAt(i, 3);//origin
				tableData[i][1]=tableWithData.getValueAt(i, 4);//as-path
				tableData[i][2]=tableWithData.getValueAt(i, 5);//next-hop
				tableData[i][3]=tableWithData.getValueAt(i, 11);//med
				tableData[i][4]=tableWithData.getValueAt(i, 7);//local preference
				tableData[i][5]=tableWithData.getValueAt(i, 8);//atomic aggregate
				tableData[i][6]=tableWithData.getValueAt(i, 9);//aggregator as
				tableData[i][7]=tableWithData.getValueAt(i, 10);//aggregator addr
				//najbolja ruta
				if(tableWithData.getValueAt(i, 12).equals("1"))
					tableData[i][8]=fa;//the best
				else tableData[i][8]=tr;
				//System.out.println(tableData[i][8])
			}
			
			gui.tablePanel.add(gui.createTablePanel(tableData));
			gui.add(gui.tablePanel);
			gui.tablePanel.revalidate();
			gui.repaint();
			
			Thread.sleep(10000);
			}
		} catch (IOException e) {
			System.out.println("Can't get table");
		} catch (InterruptedException e) {
			//System.out.println("Can't sleep");
		}
		
	}
	
	
}
