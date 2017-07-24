package warehouse;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

/**
* 
* This thread monitors the status of each outstanding order processing threads.
* 
* @author  Alex So
* @version 1.00 2017-07-23
* 
*/
public class OrderThreadsMonitor implements Runnable {
	
	InventoryAllocator allocator;
	List<Future<Integer>> runningThreadsList;
	
	public OrderThreadsMonitor(List<Future<Integer>> list, InventoryAllocator allocator) {
		runningThreadsList = list;
		this.allocator = allocator;
	}

	/**
	 * This monitor thread continuously checks each outstanding threads to see if it is done.
	 * If it is done, remove and "Future" object from the thread list, and check if inventory is empty.
	 * After each iteration of the thread list, it will call yield to allow other threads to run.
	 * 
	 * @return Nothing.
	 */
	@Override
	public void run() {
		while (true) {
			Iterator<Future<Integer>> iter = runningThreadsList.iterator();
			if (iter.hasNext()) {
				Future<Integer> future = (Future<Integer>) iter.next();
				if (future.isDone()) {
					runningThreadsList.remove(future);
					
					if (allocator.getInventory().isEmpty()) {
						allocator.shutdown();
						break;
					}
					
				}
			}
			Thread.yield();
		}
			
		
	}
}