package warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
* 
* This represents a data source which organizes the input order stream into each unit and stores them into a queue.
* The inventory allocator will ask this class for input orders stream.
* 
* @author  Alex So
* @version 1.00 2017-07-23
* 
*/

public class DataSource {
	private static final String TYPE_START_STREAM = "start";
	private static final String TYPE_END_STREAM = "stop";
	private static final String START_KEY = "StartOrderStream";
	private static final String END_KEY = "EndOrderStream";
	
	
	// String constants in order line
	private static final String ORDER_HEADER = "Header";
	private static final String ORDER_QUANTITY = "Quantity";
	private static final String ORDER_PRODUCT = "Product";
	
	
	private static final int QUEUE_SIZE = 100;	
	public BlockingQueue<List<Order>> q = new ArrayBlockingQueue<>(QUEUE_SIZE);	
	private List<Order> orderlist = null;
	long lastStreamId;
	
	public DataSource() {
		
	}
	
	/**
	 * When inventory becomes zero, this method will be called.
	 * @param data Stream header or trailer line
	 * @param type Type of the input stream
	 * @return the ID of an orders stream
	 */
	private long getStreamId(String data, String type)  {
		long id = 0;
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(data);
			if (type.equals(TYPE_START_STREAM)) {
				id = (Long) obj.get(START_KEY);
			}
			else if (type.equals(TYPE_END_STREAM)) {
				id = (Long) obj.get(END_KEY);
			}
			else {
				System.out.println("Invalid Start or End stream header syntax !");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		 
		return id;
	}
	
	
	/**
	 * Process the header of a data stream
	 *   Create a empty list for storing the orders followint the header.
	 * @param data Stream header line
	 * @return Nothing.
	 */
	protected void startOrderStream(String data) {	
	
		lastStreamId = getStreamId(data, TYPE_START_STREAM);			
		orderlist = new ArrayList<>();
	}
	
	/**
	 * Creates an order object representing a raw input order and add it to the orders list.
	 *   
	 * @param data Raw order line from the input stream.
	 * @return Nothing.
	 */
	protected void queueOrder(String data) {
				
		Order newOrder = createOrder(data);
		
		if (newOrder != null && orderlist != null) {
			orderlist.add(newOrder);
		}
	}
	
	/**
	 * Process the trailer of a data stream
	 *   Validate the trailer
	 *   put the list of orders into a queue
	 * @param data Stream trailer line
	 * @return Nothing.
	 */
	protected void endOrderStream(String data) {
		try {
			
			long streamId = getStreamId(data, TYPE_END_STREAM);
			if (lastStreamId != streamId) {
				System.out.println("Input StartOrderStream id: "+ lastStreamId + " doesn't match EndOrderStream id: " + streamId + " , previous stream orders discarded!");				
				lastStreamId = -1; // reset last stream id 
				orderlist = null;
				return;
			}				
			
			q.put(orderlist);
			
			lastStreamId = -1;  // reset stream sequence id
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetch a list of order from the queue.
	 * This method is used by client of this data source, like the Inventory allocator	 
	 * @return A list of order objects.
	 */
	protected synchronized List<Order> getOrders() throws Exception {
		List<Order> orderList = q.take();
		return orderList;
	}
	
	
	/**
	 * Create an object represent the raw order data from the input stream.
	 * @param data Raw data from an order.	 
	 * @return An Order object.
	 */
	protected Order createOrder(String orderText) {
		
		JSONParser parser = new JSONParser();
		Order newOrder = null;
		try {
		
			JSONObject jobj = (JSONObject)parser.parse(orderText);
			newOrder = new Order((long) jobj.get(ORDER_HEADER));
			Map<String, List<Integer>> newOrderItems = newOrder.getOrderedItems();
									
			JSONArray orders = (JSONArray) jobj.get("Lines");
			
			String productName = "";
			
			for ( Object p : orders) {
				JSONObject x = (JSONObject) p;
				Long quantity = (Long) x.get(ORDER_QUANTITY);
				int qty = quantity.intValue();
				productName = (String) x.get(ORDER_PRODUCT);
				if (newOrderItems.containsKey(productName) == false) {
					List<Integer> quantityList = new ArrayList<>();
					quantityList.add(qty);
					newOrderItems.put(productName, quantityList);
				}
				else {
					newOrderItems.get(productName).add(qty);
				}
											
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return newOrder;
		
	}
	
	
}
