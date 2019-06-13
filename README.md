# TicketSystemJava
# Overview
Matthew Moses, Owner of Moses computer repair, who is also a good friend of mine just created a website. It has contact info, a mission statement and, some basic service info.
Matthew came to me with a big problem. “I have a website with my service but, customers are unable to communicate with the company through the website ”. I want to solve this by creating a web app that users can sign up, login and add a work order request. 
I plan on using java/spring/thymeleaf/hibernate to implement my solution. Users will have a form for adding a work order which will include: the type of computer they need to work on(Laptop, Desktop). Operating System (Windows, Linux, Mac). serial number for their computer. And a brief description of the problem they are having.
# Features
# ------ User Sign Up -------
This application will allow customers to signup and login. It will also allow Admin users to login pulling from a seprete database. The first Admin will be the owner Matthew and he will be the only one that can allow ValidUsers to become Admins. 
# ------ Ticket System ------
# ------ Customer Side ------
Once logged in customers will be taken to a welcome page. At that point they can navigate to an add work request form. Users will be allowed to put in multiple work requests. although this form must be valid. Customers can only put in one work request for a given Computer Serial Number. Users can have many work request for multiple serial numbers but only one per computer.
# ------ Admin Side ---------
Once logged in Admins will see new tickets and new users. They can navigate to a view tickets page. They can search for a specific ticket or see all showing the oldest ticket first. they can then view one ticket and change the status of the ticket.
# Technologies
* Java
* Spring
* Thymeleaf3
* Hibernate
* IntelliJ
* Ampps
* PhpMyAdmin
# Breaking new ground
For this project I will have to create a secure server for users to login. I will have to read up on the docs for password hasing in java and tinker around, see what works for this project. I am going to have to re-fresh my memory on sorting lists and searching through lists. This will test my ability to keepo track of a large project and teach me how to communicate with a customer about what they would like from the application. This will be my first time creating an application with a REAL user in mind.
