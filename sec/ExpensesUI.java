/*
 * This UI has not been done.
 * Only shows the left tree selection.
 */

package sec;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class ExpensesUI extends JPanel {
	public ExpensesUI() {
		
	}

	Component initExpensesTab() {
		
		JPanel panel2 = new JPanel();
		JLabel label2 = new JLabel();
		panel2.add(label2);
		
		setLayout(new BorderLayout());
		
		// Now split the pane and add it to the panel too
		JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane1.setOneTouchExpandable(true);
		JPanel left = new JPanel();
		
		left.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		left.add(new JScrollPane(createList1()),BorderLayout.WEST);
				
		splitPane1.setLeftComponent(left);
		
		JPanel right = new JPanel();
		right.add(new JScrollPane(createList2()));
		right.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		splitPane1.setRightComponent(right);
		
		panel2.add(splitPane1);
		return panel2;
	}
	
	Component createList1()
	  {
		DefaultMutableTreeNode expensesMainNode = new
			      DefaultMutableTreeNode("Select Expense Periods to View");
		
		DefaultMutableTreeNode dailyExpenses = new
			      DefaultMutableTreeNode("Daily");
		
		DefaultMutableTreeNode weeklyExpenses = new
			      DefaultMutableTreeNode("Weekly");
		
		DefaultMutableTreeNode monthlyExpenses = new
			      DefaultMutableTreeNode("Monthly");		
		
		
		expensesMainNode.add(dailyExpenses);
		expensesMainNode.add(weeklyExpenses);
		expensesMainNode.add(monthlyExpenses);
		
		JTree tree = new JTree(expensesMainNode);
		tree.setPreferredSize(new Dimension(200,Common.FRAME_Y));
		tree.setFont(new Font("SansSerif",Font.PLAIN,14));
		JScrollPane treePane = new JScrollPane(tree);
		treePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		treePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			    
		add(treePane);
		return treePane;
	  }
	
	public Component createList2() 
	  {
		JPanel panel = new JPanel();
	    JLabel toDoLbl = new JLabel("STALLED THIS PAGE FOR NOW");
	    
	    panel.add(toDoLbl);
	    
	    return panel;
	  }
}
