# CS360-InventoryApplication

Briefly summarize the requirements and goals of the app you developed. What user needs was this app designed to address? 

This app was designed to cater to various user needs for storing inventory data. The application has a login process for both new and returning users. The application required a database system for storing user credentials and inventory data. It also offers SMS notifications to keep users informed about updates to inventory information. The application allows users to enter the name, quantity, and price of each object stored in the table, and have it stored after logging out. This application was designed to track inventory and notify users of low stock. 

What screens and features were necessary to support user needs and produce a user-centered UI for the app? How did your UI designs keep users in mind? Why were your designs successful? 

The screens necessary to support user needs included login, registration, data display, data entry, and SMS notification. These screens allowed users to login/register, allow/decline SMS notification, access their stored inventory data, and add/edit/delete data to the table. The UI design kept users in mind by creating a flow that simplifies access to their inventory information. After logging in the application, it prompts users for SMS access and then directly displays their data table. A user is then able to add, remove, and edit data directly from that screen which simplifies data entry for users as well.  

How did you approach the process of coding your app? What techniques or strategies did you use? How could those be applied in the future? 

The first approach I took to coding my application was understanding the requirements. I gained an understanding of the user needs and functionalities. This allowed me to find the direction for starting the application. I then began to design the application based on the user flow and how they would interact with the system. This started with creating the database for storing login information and inventory data then creating the login page and functionality. A strategy I used while completing this application was taking breaks during development to prevent frustration and making unnecessary mistakes.  

How did you test to ensure your code was functional? Why is this process important and what did it reveal? 

The method I used for testing the code was functional was using unit tests. I conducted unit tests on individual components to verify their functionality. This approach allowed me to isolate specific parts of the code and confirm that they worked correctly. This included testing login functionality and data table functionality. This process is important because it helps deliver a high-quality and user-friendly app. 

Considering the full app design and development process, from initial planning to finalization, where did you have to innovate to overcome a challenge? 

A challenge I had to overcome while developing my application was implementing the database for storing users and items. When I first developed the application, I had a table to store users and objects, but the objects were not connected to the user. This caused the same objects to show in the table regardless of the login information used. The solution to the issue was to add a primary key to the objects table to link the objects to the specific user. 

In what specific component from your mobile app were you particularly successful in demonstrating your knowledge, skills, and experience? 

A component from my application that demonstrated my knowledge was the creation of the data table for adding inventory data. I used two xml files for formatting the data table and grid_item_layout for structuring the objects added to the list. Then created methods for interacting with the data table for adding, deleting, and editing. Finally creating functionality in DataDisplayActivity.java for selecting an item in the data display and adding/deleting/editing an object. 
