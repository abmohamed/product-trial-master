# Product Management API

A Spring Boot REST API for product management with user authentication, shopping cart, and wishlist functionality.

## Features

- User authentication using JWT tokens
- Product management (CRUD operations)
- Shopping cart management
- Wishlist management
- Role-based access control (admin vs normal users)

## Technical Stack

- Java 21
- Spring Boot 3.2.1
- Spring Security with JWT
- JSON file-based storage
- Maven for dependency management

## API Endpoints

### Authentication

1. Create Account:
```bash
POST /api/account
{
    "username": "username",
    "firstname": "firstname",
    "email": "user@example.com",
    "password": "password123"
}
```

2. Get Token:
```bash
POST /api/token
{
    "email": "user@example.com",
    "password": "password123"
}
```

### Products

All product endpoints require authentication token in header: `Authorization: Bearer YOUR_TOKEN`

1. Get all products:
```bash
GET /api/products
```

2. Get product by ID:
```bash
GET /api/products/{id}
```

3. Create product (admin only):
```bash
POST /api/products
{
    "code": "code-1",
    "name": "Product Name",
    "description": "Product Description",
    "image": "image.jpg",
    "category": "Electronics",
    "price": 99.99,
    "quantity": 100,
    "internalReference": "REF-1",
    "shellId": 1,
    "inventoryStatus": "INSTOCK",
    "rating": 4
}
```

4. Update product (admin only):
```bash
PATCH /api/products/{id}
{
    "name": "Updated Name",
    "price": 149.99
}
```

5. Delete product (admin only):
```bash
DELETE /api/products/{id}
```

### Shopping Cart

All cart endpoints require authentication token.

1. View cart:
```bash
GET /api/cart
```

2. Add to cart:
```bash
POST /api/cart/items?productId={productId}&quantity={quantity}
```

3. Update quantity:
```bash
PATCH /api/cart/items/{itemId}?quantity={newQuantity}
```

4. Remove from cart:
```bash
DELETE /api/cart/items/{itemId}
```

5. Clear cart:
```bash
DELETE /api/cart
```

### Wishlist

All wishlist endpoints require authentication token.

1. View wishlist:
```bash
GET /api/wishlist
```

2. Add to wishlist:
```bash
POST /api/wishlist/items?productId={productId}
```

3. Remove from wishlist:
```bash
DELETE /api/wishlist/items/{itemId}
```

## Authentication

The API uses JWT tokens for authentication. To access protected endpoints:

1. First create an account or use the default admin account:
   - Email: admin@admin.com
   - Password: admin123

2. Get a JWT token using the /api/token endpoint

3. Include the token in all subsequent requests:
   ```
   Authorization: Bearer YOUR_TOKEN
   ```

## Authorization

- Normal users can:
  - View products
  - Manage their cart
  - Manage their wishlist

- Admin users (admin@admin.com) can also:
  - Create products
  - Update products
  - Delete products

## Getting Started

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
   ```bash
   ./mvnw clean package
   ```
4. Run the application:
   ```bash
   java -jar target/product-0.0.1-SNAPSHOT.jar
   ```
5. The API will be available at `http://localhost:8080`

## Data Storage

The application uses JSON files for data persistence:
- products.json: Store product data
- users.json: Store user accounts
- cart.json: Store shopping cart items
- wishlist.json: Store wishlist items

## Error Handling

The API returns appropriate HTTP status codes:
- 200: Successful operation
- 201: Resource created
- 204: No content (successful deletion)
- 400: Bad request
- 401: Unauthorized
- 403: Forbidden
- 404: Resource not found
- 500: Internal server error