# API ENDPOINTS

#### POST - /accounts
#### GET  - /accounts/{accountCode}
#### POST - /money-transfer
#### GET  - /money-transfer/{transferId}
_________________________________________

## Documentation

### POST - /accounts
request body
```json
{
  "accountCode": "<accountCode>",
  "balance": 0.00
}
```

#### 201 - Account created successfully
response body
```json
{
  "accountCode": "<accountCode>",
  "balance": 0.00
}
```

#### 422 - Account code already in use
response body
```json
{}
```

### GET - /accounts/{accountCode}

#### 200 - Account data retrieved successfully
response body
```json
{
  "accountCode": "<accountCode>",
  "balance": 0.00
}
```
### POST - /money-transfer
request body
```json
{
  "sourceAccount": "<sourceAccountCode>",
  "destinationAccount": "<destinationAccountCode>",
  "amount": 0.00
}
```

#### 202 - Transfer has been received and will be processed, the Location header on response will be filled  with the URI to check the transfer status.
response header
```text
"Location": "/money-transfer/{transferId}"
```

### GET - /money-transfer/{transferId}

#### 200 - Money transfer status retrieved successfully
response body
```json
{
  "transferId": "<transferId>",
  "status": "[PENDING or COMPLETED or CANCELED]"
}
```

#### 404 - Transfer not found
response body
```json
{}
```

#### 422 - Transfer id has invalid format
response body
```json
{}
```
______________________________________________

# HOW TO EXECUTE

On root project folder for linux machines type
```
gradlew run
```
On windows machines type
```
gradlew.bat run
```



