package warehouse;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 
* This represents an order from a raw order input data
* It contains all the product names and their corresponding available counts
* 
* @author  Alex So
* @version 1.00 2017-07-23
* 
*/

public class Order {
	
	protected long orderDateTime;
	protected long id;
	protected Map<String, List<Integer>> orderedItems = new HashMap<>();
	protected OrderResult result;
	
	public void setResult(OrderResult result) {
		this.result = result;
	}

	public Order(long id) {
		this.id = id;
		orderDateTime = System.nanoTime(); 			
	}		
	
	protected OrderResult getResult() {
		return result;
	}


	protected void add(String productName, List<Integer> quantityList) {
		orderedItems.put(productName, quantityList);
	}

	protected Map<String, List<Integer>> getOrderedItems() {
		return orderedItems;
	}

	protected void setOrderedItems( Map<String, List<Integer>>  orderedItems) {
		this.orderedItems = orderedItems;
	}

	public long getOrderDateTime() {
		return orderDateTime;
	}

	public long getId() {
		return id;
	}
	
	/**
	 * The Comparator used for sorting the Order objects, according to the time of creation.
	 *
	 * @return Comparator object for sorting Order object
	 */
	public static Comparator<Order> getComparator() {
		Comparator<Order> comp = new Comparator<Order>() {
			@Override
			public int compare(Order o1, Order o2) {			
				
				if (o1.getOrderDateTime() == o2.getOrderDateTime()) {
					return 0;
				}
				else if (o1.getOrderDateTime() > o2.getOrderDateTime()) {
					return 1;
				}
				else {
					return -1;
				}
			}
		};
		return comp;
	}

	
	
}
