package sectests;

import org.testng.annotations.Test;

import sec.SecDBManager;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Calendar;


public class SecDBManagerTests {
	SecDBManager dbManager;
	int itemId = 0;
	
	Date date = null;
	String itemCatgry = null;	
	String itemName = null;
  
	// Test data
	String testDataItemName = "Drumsticks";
	Double testDataQty = 2.0;
	String testDataItemCatgry = "vegetables";
	int testDataYr = 2016;
	int testDataMon = 11;
	int testDataDay = 14;
	double testDataUpdateQty = 4.0;
	
	@Test(priority=1)
	public void doAddTransactionTest() {
		dbManager.doAddTransaction(testDataItemName, testDataQty, testDataItemCatgry, testDataYr, testDataMon, testDataDay);
	}
	
	@Test(dependsOnMethods="doAddTransactionTest")
	public void doAddTransactionStockMasterTest() {	  
	  
		//compare the results
		// get data from stockmaster where itemcategory="Tomatoes"
		// the row should have the same data as "Beans", 2.0, "vegetables".
		// store the itemid.
		ResultSet rs = dbManager.getStockMasterTableFromName("Carrots");
		try {
			rs.next();
			itemId = rs.getInt("itemid");
			itemName = rs.getString("itemname");
			itemCatgry = rs.getString("itemcategory");			
		} 
		catch (SQLException e) {		
			e.printStackTrace();
		}
	  
		Assert.assertEquals(itemCatgry, "vegetables");
		Assert.assertEquals(itemName, "Carrots");			 
	}
  
	@Test(dependsOnMethods={"doAddTransactionTest","doAddTransactionStockMasterTest"})
	public void doAddTransactionItemStockIdTest() {
		int id = 0;
		
		// get data from itemstock for beans.
		// compare the itemid, should be the same.		
		ResultSet rs = 	dbManager.getStockItemsTableFromName("Carrots");
		try {
			rs.next();
			id = rs.getInt("itemid");			
		}
		catch(Exception stEx) {
			stEx.printStackTrace();
		}
		
		Assert.assertEquals(id, itemId);
		// get data from itemaddtransaction for "Beans" and  the date above.
		// compare the id and the quantity, should be the same.		  
	}
  
	@Test(dependsOnMethods={"doAddTransactionTest","doAddTransactionStockMasterTest"})
	public void doAddTransactionItemTransactionTest() {
		double qty = 0.0;
		
		LocalDate lDate = LocalDate.of(2016, 11, 14);
		Date date = java.sql.Date.valueOf(lDate);
		
		// get data from itemaddtransaction for "Beans" and  the date above.
		// compare the id and the quantity, should be the same.		
		qty  = 	dbManager.getQtyFromTransactTable(itemId, date);
		
		
		Assert.assertEquals(qty, 2.0);
				  
	}
	
	@Test(priority=2, dependsOnMethods={"doAddTransactionTest","doAddTransactionStockMasterTest"})
	public void doUpdateTransactionTest() {		
		LocalDate lDate = LocalDate.of(testDataYr, testDataMon, testDataDay);
		Date date = java.sql.Date.valueOf(lDate);
		dbManager.doUpdateTransaction(itemId, testDataUpdateQty, date);
	}
	
	// Test each individual update method
	@Test(dependsOnMethods="doUpdateTransactionTest")
	public void updateItemStockTest() {
		//retrieve the availableqty from itemstock and verify it is the updated quantity
		double qty = dbManager.getItemStock(itemId);
		Assert.assertEquals(qty, 6.0);
	}
	
	// update test method2
	@Test(dependsOnMethods="doUpdateTransactionTest")
	public void updateItemAddTransactionTableTest() {
		LocalDate lDate = LocalDate.of(testDataYr, testDataMon, testDataDay);
		Date date = java.sql.Date.valueOf(lDate);
		double qty = dbManager.getQtyFromTransactTable(itemId, date);
		Assert.assertEquals(qty, 6.0);		
	}
	
	/* Temporarily commenting this. 
	// The delete tests method1
	@Test(priority=3, dependsOnMethods={"doAddTransactionTest","doAddTransactionStockMasterTest"})
	public void doDeleteTransactionTest() {
		dbManager.doDeleteTransaction(itemId);
	}
	
	
	@Test(dependsOnMethods="doDeleteTransactionTest")
	public void deleteStockMasterTest() {
		double stock = dbManager.getItemStock(itemId);
		Assert.assertEquals(stock, 0.0);
	}
	
	@Test(dependsOnMethods="doDeleteTransactionTest")
	public void deleteItemAddTransactionTest() {
		LocalDate lDate = LocalDate.of(testDataYr, testDataMon, testDataDay);
		Date date = java.sql.Date.valueOf(lDate);
		double stock = dbManager.getQtyFromTransactTable(itemId, date);
		Assert.assertEquals(stock, 0.0);
	}
	
	@Test(dependsOnMethods="doDeleteTransactionTest")
	public void deleteItemStockTest() {
		double stock = dbManager.getItemStock(itemId);
		Assert.assertEquals(stock, 0.0);
	}
	*/
	
	@BeforeClass
	public void beforeClass() {
		dbManager = new SecDBManager();
	}  
	
}
