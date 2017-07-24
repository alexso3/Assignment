package warehouse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/**
* 
* This represents the inventory of the warehouse
* It contains all the product names and their corresponding available counts
* 
* @author  Alex So
* @version 1.00 2017-07-23
* 
*/
public class Inventory {
	
	private Map<String, Product> products; 
	
	public Inventory() {
		
		products = new HashMap<>();		
	}
	
	public void addProduct(Product product) {
		products.put(product.getName(), product);
	}

	public Map<String, Product> getProducts() {
		return products;
	}

	public Product getProduct(String name) {
		return products.get(name);
	}
	
	/**
	 * Go through each product to see if they are all empty.
	 * @return Inventory is empty or not
	 */
	public synchronized boolean isEmpty() {
		
		boolean result = true;
	
		Iterator<String> iter = products.keySet().iterator();
		while (iter.hasNext()) {
			Product product = products.get(iter.next());		
			if (product.quantity > 0) {
				result = false;
				break;
			}
		}		
		return result;
	}
	
	/**
	 * Get all the product names in sorted order.
	 * @return Sorted array of product names.
	 */
	public synchronized String[] getSortedProductNames() {
		
		String[] sortedNames = new String[products.size()];
		
		Set<String> nameSet = products.keySet();
		int idx = 0;
		for (String name : nameSet) {
			sortedNames[idx] = name;
			idx++;
		}
		Arrays.sort(sortedNames);
		
		return sortedNames;
		
	}
	
	
	
	
	
	
}
