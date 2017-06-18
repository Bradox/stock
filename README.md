[![Build Status](https://secure.travis-ci.org/rajithd/spring-boot-oauth2.png)](http://travis-ci.org/rajithd/spring-boot-oauth2)

# Build and Run
```java
mvn clean install spring-boot:run
```
# Usage
```
### Get stock
```sh
curl -i http://localhost:8080/stock/1

{
   "id":1,
   "name":"Iphone 6",
   "shortDescription":"phone",
   "longDescription":"iphone model 6",
   "price":999.00,
   "quantity":2,
   "products":[
      {
         "id":2,
         "serialNo":"2",
         "status":"IN_STOCK"
      },
      {
         "id":1,
         "serialNo":"1",
         "status":"IN_STOCK"
      }
   ]
}

```


### Get product product

curl -i 'http://localhost:8080/product/a1'

if everything goes ok:
```
{"id":11,"serialNo":"a1","status":"RESERVED"}
```

if product does not exist:
```
{"timestamp":1497780708401,"status":404,"error":"Not Found","exception":"com.tresw.stock.service.StockService$ProductNotFoundException","message":"No message available","path":"/product/a111"}
```

### Get product products from stock

curl -i 'http://localhost:8080//stock/1/products'

if everything goes ok:
```
[{"id":2,"serialNo":"2","status":"IN_STOCK"},{"id":1,"serialNo":"1","status":"IN_STOCK"}]
```

if stock does not exist:
```
{"timestamp":1497780789830,"status":404,"error":"Not Found","exception":"com.tresw.stock.service.StockService$StockNotFoundException","message":"No message available","path":"//stock/3/products"}
```


### Sell product
```sh
curl -i -X PUT  'http://localhost:8080/product/sell/1'
```
Possible Responses HTTP/1.1 200 if everything is ok

if product does not exist:
```
{"timestamp":1497780329228,"status":404,"error":"Not Found","exception":"com.tresw.stock.service.StockService$ProductNotFoundException","message":"No message available","path":"/product/sell/3"}
```

if product is not available:
```
{"timestamp":1497780379860,"status":409,"error":"conflict","exception":"com.tresw.stock.service.StockService$ProductNotAvailableException","message":"No message available","path":"/product/sell/2"}
```

### Sell reserve product
```sh
curl -i  -X PUT  'http://localhost:8080/product/reserve/1'
```
Possible Responses HTTP/1.1 200 if everything is ok

if product does not exist:
```
{"timestamp":1497780877207,"status":404,"error":"Not Found","exception":"com.tresw.stock.service.StockService$ProductNotFoundException","message":"No message available","path":"/product/unreserve/45"}
```

if product is not available:
```
{"timestamp":1497780912873,"status":409,"error":"Conflict","exception":"com.tresw.stock.service.StockService$ProductNotAvailableException","message":"No message available","path":"/product/unreserve/1"}

### Sell undo reserve product
```sh
curl -i -X PUT  'http://localhost:8080/product/unreserve/1'
```
Possible Responses HTTP/1.1 200 if everything is ok

if product does not exist:
```
{"timestamp":1497780505320,"status":404,"error":"Not Found","exception":"com.tresw.stock.service.StockService$ProductNotFoundException","message":"No message available","path":"/product/reserve/a11"}
```

if product is not available:
```
{"timestamp":1497780473461,"status":409,"error":"conflict","exception":"com.tresw.stock.service.StockService$ProductNotAvailableException","message":"No message available","path":"/product/reserve/a1"}


### Sell add product 
```sh
curl -i -X PUT 'http://localhost:8080/stock/1/123'
```

Possible Responses HTTP/1.1 200 if everything is ok

if product does exists already:
```
{"timestamp":1497781096405,"status":409,"error":"Conflict","exception":"com.tresw.stock.service.StockService$ProductAlreadyExistsException","message":"No message available","path":"/stock/1/123"}
```

if stock does not exist:
```
{"timestamp":1497781116320,"status":404,"error":"Not Found","exception":"com.tresw.stock.service.StockService$StockNotFoundException","message":"No message available","path":"/stock/5/123"}
```

### get stocks paginated 

curl -i 'http://localhost:8080/stocks?page=0&count=10&order=ASC&sort=name'

``` 
 [{"id":1,"name":"Iphone 6","shortDescription":"phone","longDescription":"iphone model 6","price":999.00,"quantity":3,"products":[{"id":1,"serialNo":"1","status":"IN_STOCK"},{"id":13,"serialNo":"123","status":"IN_STOCK"},{"id":2,"serialNo":"2","status":"IN_STOCK"}]},{"id":2,"name":"s8","shortDescription":"phone","longDescription":"samsung model 6","price":789.00,"quantity":1,"products":[{"id":12,"serialNo":"a2","status":"IN_STOCK"},{"id":11,"serialNo":"a1","status":"RESERVED"}]}]
```

### add stock 

curl -i  -H "Content-Type: application/json" -d'{"name":"test","shortDescription":"test","longDescription":"test","price":10,"quantity":0}'  -X PUT  'http://localhost:8080/stock/'

``` 
{"id":3,"name":"test","shortDescription":"test","longDescription":"test","price":10,"quantity":0}
``` 

### update stock 

curl -i  -H "Content-Type: application/json" -d'{"id":4, "name":"test","shortDescription":"test","longDescription":"test","price":10,"quantity":0}'  -X PUT  'http://localhost:8080/stock/4'

if everything goes ok:
``` 
{"id":4,"name":"test","shortDescription":"test","longDescription":"test","price":10,"quantity":0}
``` 

if stock not found:
``` 
{"timestamp":1497781964459,"status":404,"error":"Not Found","exception":"com.tresw.stock.service.StockService$StockNotFoundException","message":"No message available","path":"/stock/5"}
``` 
if conflict
``` 
{"timestamp":1497782009641,"status":409,"error":"Conflict","exception":"com.tresw.stock.service.StockService$IncorrectStockException","message":"No message available","path":"/stock/3"}
``` 

# TODO's

### Add security, I was not sure if I had to include it or not for this test purposes.
### Add service registration and discovery, I was not sure if I had to include it for this test purposes