package sec;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jdatepicker.*;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;
import org.jdatepicker.impl.UtilDateModel;


public class StockUI extends JPanel {
	
	JPanel panel1;
	JSplitPane splitPane1;
	JPanel left;
	JPanel right;
	
	JPanel card1;
	JPanel card2;
	JPanel card3;
	JPanel cards;
	JTable table;	
	
	// Add panel fields
	JTextField itemTextBox;
	JTextField qtyTextBox;
	JDatePickerImpl datePicker;
	JComboBox itemCategories;
	JTextField othersCagryTxt;
	
	// Update panel fields
	JTextField qtyUpdateTxt;
	JComboBox itemFromSelection;
	
	String []items;
	
	public StockUI() {
		
	}

	Component initStockTab() {
		
		panel1 = new JPanel();
		JLabel label1 = new JLabel();		
		
		panel1.add(label1);
		
		// Now split the pane and add it to the panel too			
		setLayout(new BorderLayout());
		
		splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPane1.setOneTouchExpandable(true);
		left = new JPanel();
		
		
		left.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
				
	    left.add(new JScrollPane(showStockLeftTree()), BorderLayout.WEST);
	    
	    splitPane1.setLeftComponent(left);
	    
	    
	    right = new JPanel();
	    right.setLayout(new CardLayout());
	    
	    right.add(new JScrollPane(showStockRightPanel()));
	    right.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));	    
	    
	    // setting the size of the right panel pushes the left panel to its left most.
	    right.setPreferredSize(new Dimension(Common.R_PANEL_X,Common.R_PANEL_Y));
	    splitPane1.setRightComponent(right);    
	    
	    panel1.add(splitPane1);    
	   	
	    //set background colour.
	    panel1.setBackground(Color.BLUE);
		
	    return panel1;
	}
	
	/* The left side tree list. */
	Component showStockLeftTree()
	  {
		DefaultMutableTreeNode stockMainNode = new
			      DefaultMutableTreeNode("Stock"); 
		
		DefaultMutableTreeNode viewStockNode = new
			      DefaultMutableTreeNode(Common.VIEW_STOCK_REPORT);
		
				
		DefaultMutableTreeNode stockAdd = new
			      DefaultMutableTreeNode(Common.ADD_NEW_ITEM);
		
		DefaultMutableTreeNode stockUpdate = new
			      DefaultMutableTreeNode(Common.UPDATE_STOCK);					
		
		stockMainNode.add(viewStockNode);
		stockMainNode.add(stockAdd);
		stockMainNode.add(stockUpdate);
		

		JTree tree = new JTree(stockMainNode);
		tree.setPreferredSize(new Dimension(200,Common.FRAME_Y));
		tree.setFont(new Font("SANS_SERIF",Font.PLAIN,14));
		JScrollPane treePane = new JScrollPane(tree);
		treePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		treePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);			
		
		// Keep 'View Stock' selected by default.
		DefaultMutableTreeNode firstLeaf = 
				((DefaultMutableTreeNode)tree.getModel().getRoot()).getFirstLeaf();
		tree.setSelectionPath(new TreePath(firstLeaf.getPath()));
		
		
		// Register tree node selection event
		tree.addTreeSelectionListener(new SelectionListener());
		
		// set fonts.
		Font f = new Font("SANS_SERIF", Font.BOLD, Common.TREE_FONT);
		tree.setFont(f);
		
		// add the tree to the main panel
		tree.setBackground(Color.WHITE);
		add(treePane);
		return treePane;
	  }
	
	/* The right side selection result. */
	public Component showStockRightPanel() {			
		card1 = new JPanel();
		card1.setLayout(new BorderLayout());
		
		card1.add(new JScrollPane(buildStockTable()));	
		
		
		card2 = new JPanel();
		card2.add(buildNewStockInputPane());
		
		card3 = new JPanel();
		card3.add(buildStockUpdatePane());
		
		cards = new JPanel(new CardLayout());
		
		cards.add(card1,"Card1");
		cards.add(card2,"Card2");
		cards.add(card3,"Card3");		
		
		return cards;	    
	}
	
	/* Build the table for showing the report on available stock. */
	Component buildStockTable() {	
		
		//get the available stock items.
		SecDBManager dbData = new SecDBManager();
		ArrayList<String> itemsAvlble = dbData.getAvailableItems();
		int tableRows = itemsAvlble.size();		
				
		String[] colNames = new String[]{"Item", "Quantity", "Updated On"};
		Object[][] data = new Object[tableRows][Common.TABLE_COLS];			
		
		int row = 0;
		for(String itemName: itemsAvlble) {			
			ResultSet rs = dbData.getStockItemsTableFromName(itemName);	
			try {
				rs.next();
				/* Bad programming - must have gone wrong somewhere to add the 
				 * subsripts directly.
				*/
				data[row][0] = rs.getString("itemname");			
				data[row][1] = rs.getDouble("availableqty");
				data[row][2] = rs.getDate("lastupdated");
				row++;					
			}
			catch (SQLException e) {			
				e.printStackTrace();
			}
		}			
        
		table = new JTable(data,colNames);
		table.setRowHeight(30);
		table.setEnabled(false);
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		// set fonts and colors.
		Font f = new Font("SANS_SERIF", Font.PLAIN, Common.FONT_SIZE);
		table.setFont(f);
		table.setForeground(Color.GRAY);
		
		// the table header
		Font tf = new Font("SANS_SERIF", Font.BOLD, Common.TABLE_HEADER_FONT);
		table.getTableHeader().setFont(tf);
		table.getTableHeader().setForeground(Color.RED);
		return table;
	}
	
	Component buildNewStockInputPane() {
		JPanel panel = new JPanel();
		GridLayout layout = new GridLayout(Common.GRID_ROWS,Common.GRID_COLS);
		layout.setHgap(Common.H_GAP);
		layout.setVgap(Common.V_GAP);
		panel.setLayout(layout);
		
		JLabel forEmptySpace = new JLabel("");
		JLabel headerTxt = new JLabel("ADD NEW ITEM");
		headerTxt.setForeground(Color.RED);
		
		Font f = new Font("SANS_SERIF", Font.BOLD, Common.HEADER_FONT);
		headerTxt.setFont(f);
		
		Font formFont = new Font("SANS_SERIF",Font.PLAIN, Common.FORM_BODY);
		
		JLabel itemCategry = new JLabel("Item Category");
		itemCategry.setFont(formFont);
		
		String[] categories = {"Vegetables", "Fruits", "Toiletries", "Others"};
		itemCategories = new JComboBox(categories);
		JLabel othersCtgry = new JLabel("Other Categories");
		othersCtgry.setFont(formFont);
		
		othersCagryTxt = new JTextField();
		JLabel itemLabel = new JLabel("Item Name");
		itemLabel.setFont(formFont);
		
		itemTextBox = new JTextField();
		JLabel qtyLabel = new JLabel("Item Quantity");
		qtyLabel.setFont(formFont);
		
		qtyTextBox = new JTextField();
		JLabel date = new JLabel("Select Date");		
		date.setFont(formFont);
		
		// The add button
		JButton addBtn = new JButton("Add Stock");
		addBtn.setFont(formFont);
		
		panel.add(headerTxt);
		panel.add(forEmptySpace);
    	panel.add(itemCategry);
		panel.add(itemCategories);
    	panel.add(othersCtgry);
    	panel.add(othersCagryTxt);
    	othersCagryTxt.setEditable(false); //disable this input field by default.
    	
    	panel.add(itemLabel);
		panel.add(itemTextBox);
		panel.add(qtyLabel);
		panel.add(qtyTextBox);
		panel.add(date);
		// display datepicker
		panel.add(showDate());
		panel.add(addBtn);
		
		// Add a selection listener on selection of "Others" in categories.	
		itemCategories.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent evt) {
	        	
	            String selectedCatgry = evt.getItem().toString();
	            if(selectedCatgry.equalsIgnoreCase("Others")) {	            	
	            	othersCagryTxt.setEditable(true);           	
	            }
	            else {
	            	othersCagryTxt.setEditable(false);
	            }	            
	        }
	    });		
		
		// update db on click on button "Add".
		ButtonHandler addBtnHandler = new ButtonHandler();
		addBtn.addActionListener(addBtnHandler);		
		
		return panel;
	}
	
	/* build the panel to show up on selecting 'Update Stock'.*/
	Component buildStockUpdatePane() {
		JPanel panel = new JPanel();
		GridLayout layout = new GridLayout(Common.GRID_ROWS,Common.GRID_COLS);
		layout.setHgap(50);
		layout.setVgap(25);
		panel.setLayout(layout);
		
		
		JLabel forEmptySpace = new JLabel("");
		JLabel header = new JLabel("UPDATE EXISTING STOCK");
		header.setForeground(Color.RED);
		Font f = new Font("SANS_SERIF", Font.BOLD, Common.HEADER_FONT);
		header.setFont(f);
		
		Font formFont = new Font("SANS_SERIF",Font.PLAIN, Common.FORM_BODY);
		JLabel availableCategories = new JLabel("Select Category");
		availableCategories.setFont(formFont);
		
		// Create a list for holding the categories of available.
		ArrayList<String> categoriesList = new ArrayList<String>();		
		SecDBManager dbData = new SecDBManager();
		ArrayList<String> availableItems = dbData.getAvailableItems();
		for(String itemName: availableItems) {
			String category = dbData.getItemCategory(itemName);	
			if(!categoriesList.contains(category)) {
				categoriesList.add(category);
			}											
		}
				
		//combo list to show the categories		
		JComboBox categoriesCombo = new JComboBox(categoriesList.toArray());
				
		// ComboList to show the list of a particular category.
		JLabel selectedCategoryLabel = new JLabel("Select An Item To Update");
		selectedCategoryLabel.setFont(formFont);
		
		//JComboBox itemFromSelection = new JComboBox();
		//items = null;	
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		//JComboBox itemFromSelection = new JComboBox();
		itemFromSelection = new JComboBox(model);
		
		// Add a selection listener on selection of "Others" in categories.	
		categoriesCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {			        	
				String selectedCatgry = evt.getItem().toString();
				
				// Get the list of items on the selected category.				
				items = dbData.getItemsFromCategory(selectedCatgry);
				
				model.removeAllElements();
				for(int i = 0; i < items.length; i++) {
					model.addElement(items[i]);				
				}
				
				itemFromSelection.revalidate();
				itemFromSelection.setVisible(true);
			            
			}
		});
		
		JLabel qtyLabel = new JLabel("Quantity to update");
		qtyLabel.setFont(formFont);
		
		qtyUpdateTxt = new JTextField();
		JLabel dateLabel = new JLabel("Select updation date");
		dateLabel.setFont(formFont);
		
		JButton updateBtn = new JButton("Update Stock");
		updateBtn.setFont(formFont);
		
		panel.add(header);
		panel.add(forEmptySpace);
		panel.add(availableCategories);
		panel.add(categoriesCombo);
		panel.add(selectedCategoryLabel);
		/* initially an empty combo. This will be populated on selection of
		 * a category.
		 */
		panel.add(itemFromSelection); 
		panel.add(qtyLabel);
		panel.add(qtyUpdateTxt);
		panel.add(dateLabel);
		panel.add(showDate());
		panel.add(updateBtn);
		
		// update db on click on button "Update Stock".
		ButtonHandler UpdateBtnHandler = new ButtonHandler();
		updateBtn.addActionListener(UpdateBtnHandler);
				
		return panel;
	}
	
	class SelectionListener implements TreeSelectionListener {
		
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			JTree tree = (JTree) e.getSource();
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree
			        .getLastSelectedPathComponent();
			
			CardLayout c1;
			switch(selectedNode.toString()) {
			case Common.VIEW_STOCK_REPORT:c1 = (CardLayout)(cards.getLayout());
								c1.show(cards, "Card1");
									break;
			case Common.ADD_NEW_ITEM:c1 = (CardLayout)(cards.getLayout());
								c1.show(cards, "Card2");
								break;
			case Common.UPDATE_STOCK:c1 = (CardLayout)(cards.getLayout());
								c1.show(cards, "Card3");
								break;
							
			default: c1 = (CardLayout)(cards.getLayout());
					c1.show(cards, "Card1");				
			}
		}
	}
	
	/* The datepicker to let the user select the date. */
	Component showDate() {
		SqlDateModel sqlModel = new SqlDateModel();
		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		LocalDate now = LocalDate.now();
		/* deliberately adding a -1 to the month as there seems to be a bug there
		 * with the jdatepicker class.
		 */
		sqlModel.setDate(now.getYear(), ((now.getMonthValue())-1), now.getDayOfMonth());
		sqlModel.setSelected(true);
		
		JDatePanelImpl datePanel = new JDatePanelImpl(sqlModel, p);		
		datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		return datePicker;
	}
	
	/* copied as such from 
	 * http://stackoverflow.com/questions/26794698/how-do-i-implement-jdatepicker
	 * and it works good!
	 */
	public class DateLabelFormatter extends AbstractFormatter {

	    private String datePattern = "yyyy-MM-dd";
	    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

	    @Override
	    public Object stringToValue(String text) throws ParseException {
	        return dateFormatter.parseObject(text);
	    }

	    @Override
	    public String valueToString(Object value) throws ParseException {
	        if (value != null) {
	            Calendar cal = (Calendar) value;
	            return dateFormatter.format(cal.getTime());
	        }

	        return "";
	    }

	}
	
	class ButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			if(e.getActionCommand().equals("Add Stock")) {
				// Input fields validation.
				if(validateAddStockInputFields() == false) {
					JOptionPane.showMessageDialog(panel1,"Input fields cannot be empty");
				}
				else {
					AddValuesToDB();
				}
			}
			else if(e.getActionCommand().equals("Update Stock")) {
				// Input fields validation.
				if(validateUpdateStockInputFields() == false) {
					JOptionPane.showMessageDialog(panel1,"Input fields cannot be empty");
				}
				else {
					UpdateValuesToDB();
				}
			}		
		}		
	}
	
	/* Validate the input form fields in the "Add Stock" page. */
	public Boolean validateAddStockInputFields() {
		if( (itemTextBox.getText().isEmpty()) || (qtyTextBox.getText().isEmpty()) ) {
			return false;
		}
		else {
			return true;
		}			
	}
	
	/* Validate the input form fields in the "Update Stock" page. */
	public Boolean validateUpdateStockInputFields() {
		if( (itemFromSelection.getItemCount() == 0) || (qtyUpdateTxt.getText().isEmpty()) ) {
			return false;
		}
		else {
			return true;
		}			
	}
	
	/* Add "Add Stock" form values to database.*/
	public void AddValuesToDB() {
		String itemName = itemTextBox.getText();
		double itemQty = Double.parseDouble(qtyTextBox.getText());
		Date selectedDate = (Date) datePicker.getModel().getValue();
		int day = datePicker.getModel().getDay();
		int month = datePicker.getModel().getMonth();
		int year = datePicker.getModel().getYear();
		
		/* There seems to be a bug in the datepicker class
		 * in generating the month. It displays one month lesser.
		 * Hence I am adding one to the retrieved value.
		 */
		month++;
		
		/* Read item categories if anything other than "Others", 
		 * else read from Others category input box.
		 */
		String itemCategory;
		if(othersCagryTxt.getText().isEmpty()) {
			itemCategory = itemCategories.getSelectedItem().toString();
		}
		else {
			itemCategory = othersCagryTxt.getText();
		}			
		
		// Add these values to the database.
		try {
			SecDBManager dbData = new SecDBManager();
			dbData.doAddTransaction(itemName, itemQty, itemCategory, year, month, day);	
			JOptionPane.showMessageDialog(panel1,"Added to database successfully");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}	
	}
	
	public void UpdateValuesToDB() {
		String itemName = itemFromSelection.getSelectedItem().toString();
		Double qty = Double.parseDouble(qtyUpdateTxt.getText());
		Date selectedDate = (Date)datePicker.getModel().getValue();		
		int itemId = 0;
		
		try {
			SecDBManager dbData = new SecDBManager();
			
			// get the itemid from the name.
			itemId = dbData.getItemIdFromName(itemName);
			
			dbData.doUpdateTransaction(itemId, qty, selectedDate);	
			JOptionPane.showMessageDialog(panel1,"Added to database successfully");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
}
