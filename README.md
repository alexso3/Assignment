# Assignment
Programming exercise (Alex So)

Warehouse Invnentory Application v1.0  

The program simulates a warehouse inventory system which accepts orders for products stored in the inventory.
The orders are sent to the main program (WarehouseApp.java) in JSON format.
Example of the JSON syntax is in the test program (TestWarehouse.java).

Initial State:
      The initial number of products and the quantities can be initialized in method (initInventory) in the file WarehouseApp.java 

Compilation:
       The source code should be compiled using Java SE v1.8
       External Library: "java-simple-1.1.1.jar" needs to be included when compiling the source code. (included in the source code folder)

Run:
       The main class is the WarehouseApp.java, and the application should be executed using Java 1.8 JVM.
       The program will start once WarehouseApp class is created.

Test:
       Please refer to file TestWarehouse.java on how to test the program.
 
Program Logic Overview:
       
        After WarehouseApp class is created, it spawns off a thread to run the "InventoryAllocator".
        All order input is sent to the WarehouseApp class, and in turn, the input is passed to a DataSource object which will manage the input data.
        The inventory allocator object will continuously ask DataSource object to provide input orders data.
        Datasource will send a stream of orders to inventory allocator when the data is available and is asked by the inventory allocator.
        For each orders stream received by the inventory allocator, it will create a separate thread to process the orders stream.
        So it is a multi-threaded application.
        As soon as the the inventory is all zeros, the application will be shutdown and a reports of the orders results will be printed.
