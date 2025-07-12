-> E-Commerce Order Placement System

This Spring Boot application simulates an order placement workflow for an e-commerce system.  
It validates product stock, simulates payment, and manages order creation — including edge cases and concurrent requests.

---

-> Features

- Validate product stock before placing an order
- Lock and deduct stock (thread-safe)
- Simulate random payment success/failure
- Save order data with price & quantity
- Handle edge cases like:
  - Product not found
  - User not found
  - Insufficient stock
  - Payment failure
- Unit tested business logic using JUnit & Mockito


-> Tech Stack

Technology : Java - 17 , Spring Boot - 3.5.3 , Maven , MySQL , JPA/Hibernate , JUnit , Mockito 
Tools : Postman , Intellij Idea


Project Structure (Layered Architecture)


 Controller     Handles REST APIs (`/orders`, `/products`, `/users`) 
 Service        Business logic (stock check, payment simulation, etc.) 
 Repository     JPA-based DB interaction (MySQL) 
 Model          Entity classes: `Product`, `Order`, `User` 
 Exception      Custom exception handling with `@RestControllerAdvice` 
 Test           Unit tests written for `OrderServiceImpl` (covers success & edge cases) 

One Manual Operation only : User need to create database manually in Mysql , 
                            database Name : ecommerce

Sample API Requests & Responses

add User for place Order

Post : localhost:8080/user
request parameter :
{
    "UserId": "pragnesh003",
    "name": "Pragnesh",
    "email": "prag3@example.com",
    "Password": 123456
}
response 
{
    "id": 4,
    "userId": "pragnesh003",
    "name": "Pragnesh",
    "email": "prag3@example.com",
    "password": "123456"
}

Now for Place order we required Product so we add product first 

POST : localhost:8080/product (for addProduct)

request parameter : 
{
  "name": "IPhone",
  "price": 100000,
  "stock": 10
}
response 
{
    "name": "IPhone",
    "price": 100000.0,
    "stock": 10,
    "createdDate": "2025-07-12T12:50:03.855354",
    "updatedDate": "2025-07-12T12:50:03.855354",
    "id": 4
}  

Similar we can Update the product 

PUT : localhost:8080/product/1

request parameter :  (update price lets assume 400 previous, now 500 rupees) .
{
        "name": "Realme",
        "price": 500.0,
        "stock": 16
}

{
    "name": "Realme",
    "price": 500.0,
    "stock": 16,
    "createdDate": "2025-07-12T11:25:14.710047",
    "updatedDate": "2025-07-12T12:54:58.7485423",
    "id": 1
}

GET : localhost:8080/product (get all products)

[
    {
        "name": "Realme",
        "price": 500.0,
        "stock": 16,
        "createdDate": "2025-07-12T11:25:14.710047",
        "updatedDate": "2025-07-12T12:54:58.748542",
        "id": 1
    },
    {
        "name": "Iphone",
        "price": 1000.0,
        "stock": 11,
        "createdDate": "2025-07-12T11:25:26.890787",
        "updatedDate": "2025-07-12T12:34:59.980658",
        "id": 2
    },
    {
        "name": "Sony",
        "price": 800.0,
        "stock": 20,
        "createdDate": "2025-07-12T11:25:44.417294",
        "updatedDate": "2025-07-12T11:25:44.417294",
        "id": 3
    },
    {
        "name": "IPhone",
        "price": 100000.0,
        "stock": 10,
        "createdDate": "2025-07-12T12:50:03.855354",
        "updatedDate": "2025-07-12T12:50:03.855354",
        "id": 4
    }
]

place an order now : required ProductId, UserId, Quantity

POST : localhost:8080/order 

Request :

{
  "productId": 2,
  "userId": "pragnesh001",
  "quantity": 2
}

response 
{
  {
    "id": 5,
    "userId": "pragnesh001",
    "quantity": 2,
    "totalAmount": 1000.0,
    "status": "PLACED",
    "createdAt": "2025-07-12T12:58:35.7659495",
    "product": {
        "name": "Realme",
        "price": 500.0,
        "stock": 14,
        "createdDate": "2025-07-12T11:25:14.710047",
        "updatedDate": "2025-07-12T12:58:35.7769423",
        "id": 1
    }
  }
}

GET : localhost:8080/order  (get all orders)

[
    {
        "id": 1,
        "userId": "pragnesh002",
        "quantity": 2,
        "totalAmount": 1000.0,
        "status": "PLACED",
        "createdAt": "2025-07-12T11:26:14.39652",
        "product": {
            "name": "Realme",
            "price": 500.0,
            "stock": 14,
            "createdDate": "2025-07-12T11:25:14.710047",
            "updatedDate": "2025-07-12T12:58:35.776942",
            "id": 1
        }
    },
    {
        "id": 2,
        "userId": "pragnesh002",
        "quantity": 2,
        "totalAmount": 2000.0,
        "status": "PLACED",
        "createdAt": "2025-07-12T12:16:28.248448",
        "product": {
            "name": "Iphone",
            "price": 1000.0,
            "stock": 11,
            "createdDate": "2025-07-12T11:25:26.890787",
            "updatedDate": "2025-07-12T12:34:59.980658",
            "id": 2
        }
    },
    {
        "id": 3,
        "userId": "pragnesh001",
        "quantity": 2,
        "totalAmount": 1000.0,
        "status": "PLACED",
        "createdAt": "2025-07-12T12:34:27.414953",
        "product": {
            "name": "Realme",
            "price": 500.0,
            "stock": 14,
            "createdDate": "2025-07-12T11:25:14.710047",
            "updatedDate": "2025-07-12T12:58:35.776942",
            "id": 1
        }
    },
    {
        "id": 4,
        "userId": "pragnesh001",
        "quantity": 2,
        "totalAmount": 2000.0,
        "status": "PLACED",
        "createdAt": "2025-07-12T12:34:59.975664",
        "product": {
            "name": "Iphone",
            "price": 1000.0,
            "stock": 11,
            "createdDate": "2025-07-12T11:25:26.890787",
            "updatedDate": "2025-07-12T12:34:59.980658",
            "id": 2
        }
    },
    {
        "id": 5,
        "userId": "pragnesh001",
        "quantity": 2,
        "totalAmount": 1000.0,
        "status": "PLACED",
        "createdAt": "2025-07-12T12:58:35.76595",
        "product": {
            "name": "Realme",
            "price": 500.0,
            "stock": 14,
            "createdDate": "2025-07-12T11:25:14.710047",
            "updatedDate": "2025-07-12T12:58:35.776942",
            "id": 1
        }
    }
]
