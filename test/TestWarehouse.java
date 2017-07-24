package test;

import warehouse.WarehouseApp;

/**
* 
* This is a test program for testing the Warehouse application.
* It uses the WarehouseApp receiveOrder method to send either a header/trailer, or a product order in JSON format.
* WarehouseApp is a multi-threaded program, so this test program can send multiple streams of data to the Warehouse app to simulate concurrent processing.
* 
* Each order stream contains one "StartOrderStream" header, one "EndOrderStream" trailer, and multiple orders line between the header and trailer.
* 
* Each line should be in valid JSON format
* 
* "StartOrderStream" format: {"StartOrderStream":(stream ID)}   (Stream ID) is a number identify the data stream; must be at the beginning of each stream.
* "EndOrderStream" format:   {"EndOrderStream":(stream ID)}    (Stream ID) is a number identify the data stream, and be at the end of each stream and value must match the header.
* 
* Valid order format: {"Header":(header-#),"Lines":[{"Product":"(product-name)","Quantity":(quantity)}]}
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
* @author  Alex So
* @version 1.00 2017-07-23
* 
*/

public class TestWarehouse {
		
	public static void main(String[] args) {
						
		WarehouseApp warehouse = new WarehouseApp();
	
		/* Sending #1 stream of orders */		
		warehouse.receiveOrder("{\"StartOrderStream\":1}");
		warehouse.receiveOrder("{\"Header\":1,\"Lines\":[{\"Product\":\"A\",\"Quantity\":20},{\"Product\":\"C\",\"Quantity\":1}]}");		
		warehouse.receiveOrder("{\"Header\":2,\"Lines\":[{\"Product\":\"E\",\"Quantity\":100}]}");
		warehouse.receiveOrder("{\"Header\":3,\"Lines\":[{\"Product\":\"D\",\"Quantity\":2}]}");
		warehouse.receiveOrder("{\"Header\":4,\"Lines\":[{\"Product\":\"A\",\"Quantity\":30},{\"Product\":\"C\",\"Quantity\":9},{\"Product\":\"B\",\"Quantity\":1}]}");
		warehouse.receiveOrder("{\"Header\":5,\"Lines\":[{\"Product\":\"B\",\"Quantity\":50}]}");
		warehouse.receiveOrder("{\"Header\":6,\"Lines\":[{\"Product\":\"C\",\"Quantity\":80}]}");
		warehouse.receiveOrder("{\"Header\":7,\"Lines\":[{\"Product\":\"D\",\"Quantity\":8}]}");
		warehouse.receiveOrder("{\"Header\":8,\"Lines\":[{\"Product\":\"E\",\"Quantity\":16}]}");
		warehouse.receiveOrder("{\"Header\":9,\"Lines\":[{\"Product\":\"E\",\"Quantity\":800}]}");
		warehouse.receiveOrder("{\"Header\":10,\"Lines\":[{\"Product\":\"D\",\"Quantity\":20}]}");		
		warehouse.receiveOrder("{\"Header\":11,\"Lines\":[{\"Product\":\"A\",\"Quantity\":30}]}");
		warehouse.receiveOrder("{\"Header\":12,\"Lines\":[{\"Product\":\"E\",\"Quantity\":16}]}");		
		warehouse.receiveOrder("{\"Header\":13,\"Lines\":[{\"Product\":\"C\",\"Quantity\":10}]}");
		warehouse.receiveOrder("{\"Header\":14,\"Lines\":[{\"Product\":\"D\",\"Quantity\":30}]}");	
		warehouse.receiveOrder("{\"Header\":15,\"Lines\":[{\"Product\":\"D\",\"Quantity\":20},{\"Product\":\"E\",\"Quantity\":60}]}");
		warehouse.receiveOrder("{\"Header\":16,\"Lines\":[{\"Product\":\"B\",\"Quantity\":60}]}");
		warehouse.receiveOrder("{\"Header\":17,\"Lines\":[{\"Product\":\"E\",\"Quantity\":8}]}");		
		warehouse.receiveOrder("{\"Header\":18,\"Lines\":[{\"Product\":\"A\",\"Quantity\":70}]}");
		warehouse.receiveOrder("{\"Header\":19,\"Lines\":[{\"Product\":\"D\",\"Quantity\":20}]}");
		warehouse.receiveOrder("{\"Header\":20,\"Lines\":[{\"Product\":\"B\",\"Quantity\":16},{\"Product\":\"B\",\"Quantity\":23}]}");		
		warehouse.receiveOrder("{\"EndOrderStream\":1}");
		/* Ending #1 stream of orders */
		
		
		
		
		
				    											
	}
}
