package warehouse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import warehouse.OrderResult.Result;

/**
* 
* To increase performance, each stream of orders is process by a separate threads.
* This thread will be created by the inventory allocator.
* 
* @author  Alex So
* @version 1.00 2017-07-23
* 
*/

public class ProcessOrdersList implements Callable<Integer> {
	
	private Inventory inventory;
	private OrdersHistory history;
	private List<Order> ordersList;
	
	public ProcessOrdersList(Inventory inventory, OrdersHistory history, List<Order> orderslist) {
		this.inventory = inventory;
		this.history = history;
		this.ordersList = orderslist;

	}
	/**
	 * Thread entry point.
	 * Process a list of Order objects.
	 * 
	 * @return total numbers of successful processing in a single order stream.
	 */
	@Override
	public Integer call() throws Exception {
		
		int totalSuccess = 0;
		
		// process each single order in a data stream
		for (Order order : ordersList) {
			if (inventory.isEmpty() == false) {
				OrderResult result = ProcessOrder(order);
				if (result.getResult() == Result.SUCCESS) {
					totalSuccess += 0;
				}
			}				
		}
		return totalSuccess;
	}
	
	/**
	 * This process a single Order.
	 * It checks if the product in the inventory can fulfill the quantity asked in each order.
	 * It also save the result in a history queue for reporting
	 * @param order input order
	 * 
	 * @return OrderResult object represents the result of the order processing. 
	 */
	public OrderResult ProcessOrder(Order order) {
		OrderResult result = new OrderResult();
		try {
			Product product;
			String productName;
			Map<String, List<Integer>> orderProducts = order.getOrderedItems();
			
			// process each product request in a single order  (each order has one of more products requests)
			Iterator<String> items = orderProducts.keySet().iterator();
			while (items.hasNext()) {
				productName = items.next();
				product = inventory.getProduct(productName);
				if (product != null) {
					List<Integer> quantityList = orderProducts.get(productName); 
					for (Integer decrementAmount : quantityList) {	
						// ask the project in the inventory to see if it can allocate the request quantity
						if (product.decrementCount(decrementAmount)) {
							// product count has enough to fulfill the order, go ahead to decrement the available count
							if (result.getAllocated().containsKey(productName)) {
								result.getAllocated().get(productName).add(decrementAmount);
							}
							else {
								List<Integer> qtyList = new ArrayList<>();
								qtyList.add(decrementAmount);
								result.getAllocated().put(productName, qtyList);
							}
							
						}
						else {
							// product cannot fulfill the requested quantity, remember in the "back ordered" list 
							if (result.getBackordered().containsKey(productName)) {
								result.getBackordered().get(productName).add(decrementAmount);
							}
							else {
								List<Integer> qtyList = new ArrayList<>();
								qtyList.add(decrementAmount);
								result.getBackordered().put(productName, qtyList);
							}
							
							
							
						}
					}
					result.setResult(Result.SUCCESS);
					
				}
				else {
					//  product not found, exit the loop and return error
					result.setErrorMessage("Product name not found: " + productName);
					result.setResult(Result.FAIL);
					break;
				}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			result.setErrorMessage(e.getMessage());
			result.setResult(Result.FAIL);
		}
		order.setResult(result);
		// save the order and result to a history queue for reporting purposes.
		history.add(order);	
		return result;
	}

}
