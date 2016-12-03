package sec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SecDBManager {
	private Connection con;
	private Statement st;
	private ResultSet rs;		
	
	String[] items;
	
	public SecDBManager() {
		String conStr = "jdbc:mysql://localhost:3306/" + Common.stockDb;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(conStr,Common.dbUser,Common.dbPwd);
			st = con.createStatement();			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void setStockMaster(String itemName, String itemCategory) {
		try {
			// convert to lower case before inserting.
			itemName = itemName.toLowerCase();
			itemCategory = itemCategory.toLowerCase();
			String query = "insert into StockMaster (itemname,itemcategory) values(\"" + itemName + "\"," + "\"" + 
					itemCategory + "\")";
			
			int rs = st.executeUpdate(query);			
		}
		catch (Exception stEx) {
			stEx.printStackTrace();
		}
	}
	
	/* makes an insert to itemAddTransaction table with today's date for ItemAddedOn field.*/
	private void setAddTransaction(int itemId, double qty) {
		try {
			String query = "insert into itemAddTransaction values(" + itemId + "," + qty + "," 
					+ "curDate()" + ")";
			
			rs = st.executeQuery(query);
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}	 
	}

	
	/* makes an insert to itemAddTransaction table.
	 * Overloaded form of setAddTransaction.
	 */
	// The Date object is from sql.date
	private void setAddTransaction(int itemId, double qty, int year, int month, int day) {
		try {
			LocalDate date = LocalDate.of(year, month, day);
			String query = "insert into itemAddTransaction values (" + itemId + "," + qty + ","
					+ "'" + date + "'" + ")";			
			
			
			int rs = st.executeUpdate(query);
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
	}
	
	private void setAddTransaction(int itemId, double qty, Date date) {
		try {
			
			String query = "insert into itemAddTransaction values (" + itemId + "," + qty + ","
					+ "'" + date + "'" + ")";			
			System.out.println("Query3 " + query);
			int rs = st.executeUpdate(query);
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
	}
	
	// this will insert today's date into the table.
	private void setItemStock(int itemId, double qty) {
		try {			
			String query = "insert into itemStock values (" + itemId + "," + qty + ","
					+ "curDate()" + ")";			
			
			int rs = st.executeUpdate(query);
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
	}
	
	// overloaded
	private void setItemStock(int itemId, double qty, int year, int month, int day) {
		try {
			LocalDate date = LocalDate.of(year, month, day);
			String query = "insert into itemStock values (" + itemId + "," + qty + ","
					+ "'" + date + "'" + ")";			
			
			int rs = st.executeUpdate(query);
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
	}
	
	/* updates the table ItemAddTransaction for newly added stock for 
	 * a particular item. 
	 */
	private void updateItemAddTransaction(int itemId, double qty, Date date) {
		try {			
			String query = "update itemaddtransaction set itemQtyAdded = " + qty + " where itemId = " + itemId + 
					" and itemaddedon = '" + date + "'";	
			
			System.out.println("Query2 " + query);
			int rs = st.executeUpdate(query);
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
	}

	private void updateItemStock(int itemId, double qty, Date date) {
		try {			
			String query = "update itemstock set availableqty = " + qty + " , lastupdated = "
					+ "'" + date + "' where itemId = " + itemId;			
			
			System.out.println("Query1 " + query);
			int rs = st.executeUpdate(query);
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
	}
	
	/* delete from itemstock. */
	private void deleteItemStock(int itemId) {
		try {
			String query = "delete from itemstock where itemid=" +
					itemId;
			
			int rs = st.executeUpdate(query);
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
 	}
	
	/* delete all entries from itemaddtransaction. */
	private void deleteItemFromTransaction(int itemId) {
		try {
			String query = "delete from itemaddtransaction where itemid = " +
					itemId;
			int rs=st.executeUpdate(query);
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
	}
	
	/* delete entry from stockmaster. */
	private void deleteStockMaster(int itemId) {
		try {
			String query = "delete from stockmaster where itemid=" +
					itemId;
			int rs = st.executeUpdate(query);
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
	}
	
	/* Retrieves the id of the stock, given its name. */
	private int getStockIdFromName(String itemName) {
		int id = 0;
		try {
			itemName = itemName.toLowerCase();
			String query = "select itemid from stockmaster where itemname=\"" +
					itemName + "\"";
			
			rs = st.executeQuery(query);
			rs.next();
			id = rs.getInt("itemid");
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		return id;
	}
	
	/*Get the last updated date from itemaddtransaction table. */
	private Date getLastDateUpdated(int itemId) {
		Date date = null;
		try{
			String query="select itemaddedon from itemaddtransaction where itemid=" +
					itemId;
			rs = st.executeQuery(query);
			//rs.next();
			// get the latest updated 
			rs.last();
			date=rs.getDate("itemaddedon");
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		return date;
	}
	
	/* Returns all rows of data with all column from table
	 * StockMaster.
	 */
	public ResultSet getStockMasterData() {
		try {
			String query = "select * from StockMaster";
			rs = st.executeQuery(query);			
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		return rs;
	}
	
	/* Retrieves the name of the Stock given its id. */
	public String getStockFromItemId(int itemId) {
		String itemName = "";
		try {
			String query = "select itemname from StockMaster where itemId =" +
					itemId;
			rs = st.executeQuery(query);
			itemName = rs.getString("itemname");
			
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		return itemName;
	}	
	
	/* The following methods for retrieving the categories are part of the Item class
	 * and not part of the database.
	 * Hence retrieving a category should never be done from here.
	 * I m retaining this only for the purpose of checking up if it ever gets necessary.
	 */
	/* Retrieves the category of the item given its name. */
	public String getItemCategory(String itemName) {
		String itemCategory = "";
		try {
			itemName = itemName.toLowerCase();
			String query = "select itemCategory from StockMaster where itemName = '"
						+ itemName + "'";
			
			rs = st.executeQuery(query);
			rs.next();
			itemCategory = rs.getString("itemCategory");
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		return itemCategory;
	}
	
	/* Overloaded - retrieves the category of the item given its id. */
	public String getItemCategory(int itemId) {
		String itemCategory = "";
		try {
			String query = "select itemCategory from StockMaster where itemId ="
						+ itemId;
			rs = st.executeQuery(query);
			itemCategory = rs.getString("itemCategory");
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		return itemCategory;
	}	
	
	
	
	/* Gets the available stock on the specified id */
	public double getItemStock(int itemId) {
		double stock = 0;
		try {
			String query = "select availableqty from itemstock where itemid =" +
					itemId;
			rs = st.executeQuery(query);
			rs.next();
			stock = rs.getDouble("availableqty");
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		return stock;
	}
	
	
	/* Gets the list of items that are available in stock i.e., > 0. */
	public ArrayList<String> getAvailableItems() {
		ArrayList<String> itemsInStock = new ArrayList<String>();
		try {
			String query = "select itemname from stockmaster a, itemstock b where a.itemid = b.itemid"
					+ " and b.availableqty > 0";
			rs = st.executeQuery(query);
			
			while(rs.next()){
				itemsInStock.add(rs.getString("itemname"));
			}
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		return itemsInStock;
	}
	
	/* Get the row data from stockmaster for a particular itemname. */
	public ResultSet getStockMasterTableFromName(String itemName) {
		try {
			String query = "select * from stockmaster where itemname =\"" +
					itemName + "\"";
			rs = st.executeQuery(query);			
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		return rs;
	}
	
	/* Get the itemId from itemName. */
	public int getItemIdFromName(String itemName) {
		int itemId = 0;
		try {
			String query = "select itemid from stockmaster where itemname = '" 
					+ itemName + "'";
			rs = st.executeQuery(query);
			rs.next();
			itemId = rs.getInt("itemid");
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		return itemId;
	}
	
	/* get row data from stockitems, given itemname. */
	public ResultSet getStockItemsTableFromName(String itemName) {
		try {
			String query = "select b.itemname, a.availableqty, a.lastupdated from itemstock a, "
					+ "stockmaster b where a.itemid = b.itemid and b.itemname =\"" +
					itemName + "\"";
			rs = st.executeQuery(query);
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		return rs;
	}
	
	/* Returns the list of items available on the specified category. */
	public String[] getItemsFromCategory(String itemCategory) {
		ArrayList<String> itemList = new ArrayList<String>();
		int i = 0; //to index the string array for adding strings into it.
		try {
			String query = "select itemname from stockmaster where itemcategory = '" 
					+ itemCategory + "'";
			
			rs = st.executeQuery(query);
			while(rs.next()) {
				itemList.add(rs.getString("itemname"));
			}
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		//convert the list to an array and return.
		String []items = new String[itemList.size()];
		for(i = 0; i < itemList.size(); i++) {
			items[i] = itemList.get(i);				
		}
		
		return items;
	}
	/* get row quantity itemaddtransaction, given the date and id. */
	public double getQtyFromTransactTable(int itemId, Date date) {
		double qty = 0;
		
		try {
			String query = "select itemqtyadded from itemaddtransaction where itemid = " +
					itemId + " and itemaddedon = '" + date + "'";
			
			
			rs = st.executeQuery(query);
			rs.next();
			qty = rs.getDouble("itemqtyadded");
			
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		return qty;		
	}
	
	/* First time addition for the item.
	 * Do fresh inserts to itemaddtransaction, stockmaster, itemstock.
	 */
	public void doAddTransaction(String itemName, double qty, String itemCategory, int year, int month, int day) {
		
		int itemId = 0;
		
		// insert into stockmaster table.
		setStockMaster(itemName,itemCategory);
		
		// get the itemid injected.
		itemId = getStockIdFromName(itemName);
		
		// insert into itemstock.
		setItemStock(itemId, qty, year, month, day);
		
		//insert into itemaddtransaction.
		setAddTransaction(itemId,qty,year, month, day);		
	}
	
	
	public void doUpdateTransaction(int itemId, double qty, Date date) {
		// get the quantity in itemStock and add it with the new one.
		// update the date in itemstock with the date
		double availableQty = 0;
		availableQty = getItemStock(itemId);
		availableQty+=qty;
		
		/* Not updating the added value, but the one updated by the user itself.
		 * This is because the user will update the update on stock remaining on EOD and
		 * what was updated each day will be updated in the transaction table.
		 */
		
		//updateItemStock(itemId, availableQty);
		updateItemStock(itemId, qty, date);
		
		/* get the latest itemaddedon from itemaddtransaction
		 * If the incoming date is the same as itemaddedon, then add the value
		 * else insert a new row.
		 */
		 
		Date updatedDate = getLastDateUpdated(itemId);
		System.out.println("Updated date is " + updatedDate);
		System.out.println("Current date is " + date);
		
		/* Compare the strings rather than the date objects themseleves
		 * since the date objects do not comapre to be equal though they point to the same
		 * date, maybe due to the time.
		 */
		
		String updateDateStr = updatedDate.toString();
		String dateStr = date.toString();
		
		if(updateDateStr.equals(dateStr)) {
			//updateItemAddTransaction(itemId, availableQty, date);	
			System.out.println("Should be here");
			updateItemAddTransaction(itemId, qty, date);	
		}
		else {
			setAddTransaction(itemId, qty,date);			
		}		
	}
	
	public void doDeleteTransaction(int itemId) {
		// delete entry from itemstock
		deleteItemStock(itemId);
		
		// delete all entries of the itemid from itemaddtransaction
		deleteItemFromTransaction(itemId);
		
		// delete entry from stockmaster
		deleteStockMaster(itemId);
	}
}