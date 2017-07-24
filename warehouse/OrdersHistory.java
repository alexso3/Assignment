package warehouse;

import java.util.List;
import java.util.ArrayList;

/**
* 
* After each order is processed, the order object is queued into this history object.
* It is used for the producing the output lines when the inventory becomes all zeros.
* 
* @author  Alex So
* @version 1.00 2017-07-23
* 
*/
public class OrdersHistory {
	// all processed order object is queued in this history list
	protected List<Order> pastOrders;
	
	public OrdersHistory() {
		pastOrders = new ArrayList<>();
	}

	public synchronized List<Order> getPastOrders() {
		return pastOrders;
	}
	
	public synchronized void add(Order order) {
		pastOrders.add(order);
	}
}
