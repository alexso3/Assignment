package warehouse;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
* 
* This is the inventory allocator thread.
* It is continuously ask the data source for a input stream of orders, it then spawn off an individual thread to process the orders, so as to increase the performance throughput.
* It also creates a monitoring thread to detect when those threads are done and detect when the inventory becomes all zero.
* When the inventory is zero, it will shut down the queue, and the main program.
* 
* 
* @author  Alex So
* @version 1.00 2017-07-23
* 
*/
public class InventoryAllocator implements Runnable {
	private WarehouseApp warehouse;
	private Inventory inventory;
	
	public Inventory getInventory() {
		return inventory;
	}

	private DataSource datasource;
	private OrdersHistory history;
	
	private ExecutorService pool;
	
	private List<Future<Integer>> runningThreads = new CopyOnWriteArrayList<>();
	
	public OrdersHistory getHistory() {
		return history;
	}

	public InventoryAllocator(Inventory inventory, DataSource datasource, WarehouseApp warehouse) {
		this.warehouse = warehouse;
		this.inventory = inventory;		
		this.datasource = datasource;
		history = new OrdersHistory();
		pool = Executors.newCachedThreadPool();
		
		Thread monitor = new Thread(new OrderThreadsMonitor(runningThreads, this));
		try {
			monitor.start();
		}
		catch (Exception e) {
			System.out.println("Received exceptions from Monitoring treads");
			e.printStackTrace();
		}
	}
	
	/**
	 * This will continuously ask data source for raw orders stream data.
	 * When a new stream is available, it creates a thread to process the stream for better performance.
	 * When inventory is zero, the threads pool will be shutdown and the Warehouse application will be exited.
	 * 
	 * @return Nothing.
	 */
	@Override
	public void run() {
	
		while (true) {	
			try {				
				List<Order> list = datasource.getOrders();	
				
				ProcessOrdersList threadProcess = new ProcessOrdersList(inventory, history, list);				
				Future<Integer> result = pool.submit(threadProcess);
				runningThreads.add(result);
				
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}	
		}	

	}
	
	/**
	 * Shut down the threads pool and exit the main Warehouse program.
	 * @return Nothing.
	 */
	
	protected void shutdown() {
		if (pool != null) {
			pool.shutdownNow();
		}
		warehouse.shutdown();  
		
	}
	
}



