package warehouse;

/**
* 
* This represents a product in the inventory.
* 
* @author  Alex So
* @version 1.00 2017-07-23
* 
*/
public class Product {
	public enum Status {ACTIVE, INACTIVE};
	
	protected String name;
	protected int quantity;
	protected int backOrderQuantity;
	protected Status status;
	
	public Product(String name, int initQuantity) {
		this.name = name;
		this.quantity = initQuantity;
		backOrderQuantity = 0;
		status = Status.ACTIVE;
	}
	
	/**
	 * Reduce the available count of the product.
	 * @param amount The amount to be subtracted from the product available count.
	 * 
	 * @return whether the product available count is large enough to be subtracted
	 */
	protected synchronized boolean decrementCount(int amount) {
		boolean result = false;;		
		
		if ( quantity >= amount) {
			quantity -= amount;
			result = true;
		}
		else {
			// need to back order
			backOrderQuantity += amount;
			result = false;
		}		
			
		return result;
	}
	
	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public synchronized int getQuantity() {
		return quantity;
	}

	protected void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getBackOrderQuantity() {
		return backOrderQuantity;
	}

	protected void setBackOrderQuantity(int backOrderQuantity) {
		this.backOrderQuantity = backOrderQuantity;
	}
	
	public Status getStatus() {
		return status;
	}

	protected void setStatus(Status status) {
		this.status = status;
	}
}
