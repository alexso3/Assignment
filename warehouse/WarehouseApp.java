package warehouse;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
* 
* This is the main program of the Warehouse application.
* It initially setup the inventory of the warehouse.
* Creates the data source of the application; it will pass the orders input to the datasource class to setup the data source.
* 
* WarehouseApp is a multi-threaded program.
* It initially creates an Inventory allocator thread, in turn the inventory allocator will spawn a separate thread to process each data order stream, so that it can increase the
* performance of the application.
* 
* Input: 
* 
*    Each order stream contains one "StartOrderStream" header, one "EndOrderStream" trailer, and multiple orders line between the header and trailer.
* 
*    Each line should be in valid JSON format
* 
*    "StartOrderStream" format: {"StartOrderStream":(stream ID)}   (Stream ID) is a number identify the data stream; must be at the beginning of each stream.
*    "EndOrderStream" format:   {"EndOrderStream":(stream ID)}    (Stream ID) is a number identify the data stream, and be at the end of each stream and value must match the header.
* 
*    Valid order format: {"Header":(header-#),"Lines":[{"Product":"(product-name)","Quantity":(quantity)}]}
*                     (header-#) - a unique number within a data stream to identify the order
*                     (product-name) - name of the product to be ordered
*                     (quantity) - number of product to be ordered
*                     multiple products can be ordered, it is specified in the list (enclosed by the "[" and "]" characters, each product in the list is delimited by a ',' character.
*             Example order line :-  {"Header":1,"Lines":[{"Product":"A","Quantity":20},{"Product":"C","Quantity":1}]}       
*
* 
* Output: when the inventory of the WarehouseApp is zero, it will halt and print out the header of each order, the quantity on each line, the quantity allocated to each line and
*         the quantity backordered for each line.
*         
*         For example: 
*            if the initial warehouse has the following inventory for products A, B, C, D and E.
*            A x 2
*            B x 3
*            C x 1
*            D x 0
*            E x 0
*            
*            and the order lines are:
* 	         {"Header":1,"Lines":[{"Product":"A","Quantity":1},{"Product":"C","Quantity":1}]}
*			 {"Header":2,"Lines":[{"Product":"E","Quantity":5}]}
* 			 {"Header":3,"Lines":[{"Product":"D","Quantity":4}]}	
* 			 {"Header":4,"Lines":[{"Product":"A","Quantity":1},{"Product":"C","Quantity":1}]}
* 			 {"Header":5,"Lines":[{"Product":"B","Quantity":3}]}
* 			 {"Header":6,"Lines":[{"Product":"D","Quantity":4}]}
* 
*            The output should be:
*            1:	1,0,1,0,0::1,0,1,0,0::0,0,0,0,0	
*			 2:	0,0,0,0,5::0,0,0,0,0::0,0,0,0,5	
*			 3:	0,0,0,4,0::0,0,0,0,0::0,0,0,4,0	
* 			 4:	1,0,1,0,0::1,0,0,0,0::0,0,1,0,0	
* 			 5:	0,3,0,0,0::0,3,0,0,0::0,0,0,0,0	 
* 
* Program Logic Overview:
*       
*       After WarehouseApp class is created, it spawns off a thread to run the "InventoryAllocator".
*       All order input is sent to the WarehouseApp class, and in turn, the input is passed to a DataSource object which will manage the input data.
*       The inventory allocator object will continuously ask DataSource object to provide input orders data.
*       Datasource will send a stream of orders to inventory allocator when the data is available and is asked by the inventory allocator.
*       For each orders stream received by the inventory allocator, it will create a separate thread to process the orders stream.
*       So it is a multi-threaded application.
*       As soon as the the inventory is all zeros, the application will be shutdown and a reports of the orders results will be printed.
*
* @author  Alex So
* @version 1.00 2017-07-23
* 
*/

public class WarehouseApp {
	
	public static final String PRODUCT_A = "A";
	public static final String PRODUCT_B = "B";
	public static final String PRODUCT_C = "C";
	public static final String PRODUCT_D = "D";
	public static final String PRODUCT_E = "E";
	
	public static final String START_ORDER_STREAM = "{\"StartOrderStream\"";
	public static final String ORDER = "{\"Header\"";
	public static final String END_ORDER_STREAM = "{\"EndOrderStream\"";
	public static final String SHUTDOWM = "{\"Shutdown\"";	
	
	public static final int TYPE_STARTSTREAM = 0;
	public static final int TYPE_ORDER = 1;
	public static final int TYPE_ENDSTREAM = 2;
	public static final int TYPE_SHUTDOWN = 3;
	
	private static Inventory inventory; 
	private static InventoryAllocator allocator;
	private static DataSource datasource;
		

	public WarehouseApp() {
		datasource = new DataSource();
		inventory = new Inventory();
		initInventory();
		allocator = new InventoryAllocator(inventory, datasource, this);
		Thread allocatorTask = new Thread(allocator);
		allocatorTask.start();
	}
	
	/**
	 * This initially set up the inventory.
	 * @return Nothing.d
	 */
	
	private void initInventory() {
				
		inventory.addProduct(new Product("A", 150));
		inventory.addProduct(new Product("B", 150));
		inventory.addProduct(new Product("C", 100));
		inventory.addProduct(new Product("D", 100));
		inventory.addProduct(new Product("E", 200));
			
	}
	
	
	protected InventoryAllocator getAllocator() {
		return allocator;
	}
	
	/**
	 * When inventory becomes zero, this method will be called.
	 * @return Nothing
	 */
	protected void shutdown() {
		printOrders();		
		System.out.println("Inventory is zero, warehouse system exited ...");
		System.exit(0);
	}
	
	public void receiveOrder(String data) {
		int type = getOrderType(data);
		
		switch (type) {
			case TYPE_STARTSTREAM:
				datasource.startOrderStream(data); // tell datasource to start a new stream of order
				break;
			case TYPE_ORDER:
				datasource.queueOrder(data);  
				break;
			case TYPE_ENDSTREAM:
				datasource.endOrderStream(data);
				//datasource.prtQ();
				break;
			case TYPE_SHUTDOWN:
				this.shutdown();
				break;
			default:				
		}
		
	}
	
	/**
	 * This will print out the out the header of each order, the quantity on each line, the quantity allocated to each line and
	 * the quantity backordered for each line.       
	 * @return Nothing
	 */
	public void printOrders() {
		String[] productNames = inventory.getSortedProductNames();
		OrdersHistory history = allocator.getHistory();
		List<Order> ordersList = history.getPastOrders();
		
		// sort orders
		Collections.sort(ordersList, Order.getComparator());
		
		
		StringBuffer buffer = new StringBuffer();
		
		for (Order order : ordersList) {
			buffer.setLength(0);   // clear string buffer
			// append Header
			buffer.append(order.getId() + ": ");
			// generate order quantities   
			buffer.append(generateQuantityString(order.getOrderedItems(), productNames));
			buffer.append("::");
			// generate allocated quantities
			buffer.append(generateQuantityString(order.getResult().getAllocated(), productNames));
			buffer.append("::");
			buffer.append(generateQuantityString(order.getResult().getBackordered(), productNames));
			
			System.out.println(buffer.toString());
		}								
	}
	
	/**
	 * This prints out the header of an order, the quantity of the order, the quantity allocated to each order and
	 * the quantity backordered for each order .      
	 * @return Nothing.
	 */
	private static String generateQuantityString(Map<String, List<Integer>> itemsMap, String[] nameList) {
		StringBuffer buffer = new StringBuffer();
		int idx = 0;
		for (String name : nameList) {
			if (itemsMap.containsKey(name)) {
				List<Integer> quantityList = itemsMap.get(name);
				int totalQuantityOrdered = 0;
				for (Integer qty : quantityList) {
					totalQuantityOrdered += qty;
				}
				buffer.append(totalQuantityOrdered);
			}
			else {
				buffer.append("0");
			}
			
			if (++idx < nameList.length) {
				buffer.append(",");
			}
		}
		return buffer.toString();
	}
	
	
	
	/**
	 * Determine the type of an input order line.
	 *       
	 * @return The type value represent the order line.
	 */
			
	private int getOrderType(String data) {
		int type = 0; 
		if (data.startsWith(START_ORDER_STREAM)) {
			type = TYPE_STARTSTREAM; 
		}
		else if (data.startsWith(END_ORDER_STREAM)) {
			type = TYPE_ENDSTREAM;
		}
		else if (data.startsWith(ORDER)) {
			type = TYPE_ORDER;
		}
		else if (data.startsWith(SHUTDOWM)) {
			type = TYPE_SHUTDOWN;
		}
			
		
		return type;
	}
	
	

}
