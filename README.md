# revolut-test

Running the app
---

1. Run `mvn clean install` to build your application
2. Start application with `java -jar target/revolut-test-1.0-SNAPSHOT.jar server config.yml`

Endpoints
---
Create account
```
curl -X POST -H 'Content-Type: application/json' -d '{ "customerId" : 1, "balance" : { "currencyCode" : "USD", "amount" : 20.000000 } }' http://localhost:8080/account
curl -X POST -H 'Content-Type: application/json' -d '{ "customerId" : 2, "balance" : { "currencyCode" : "USD" } }' http://localhost:8080/account
```

Fetch accounts
```
curl http://localhost:8080/account
```

Create Transfer
```
curl -X POST -H 'Content-Type: application/json' -d '{ "fromAccountId" : 1, "toAccountId" : 2, "amount" : { "currencyCode" : "USD", "amount" : 10.000000 } }' http://localhost:8080/transfer
```

Fetch transfers
```
curl http://localhost:8080/transfer
```
