### Goal
- Resource Efficiency: How much system resources they use.
- Throughput: Number of tasks executed per unit time.

#### Refer 
See [picture](img.png)

#### Note
- Do not bring other applications / web layer etc. into the test.
- Do not use a long-running query!
  - As we want to do a performance comparison of **R2DBC** vs **JDBC / JPA** drivers.
  - Intention is not to test the underlying database engine.
  - The database could be postgres or mysql.

#### How to set up?
```bash
$ docker compose -f r2dbc-vs-jdbc/docker-compose.yaml up
```

- Monitor the console output. Ensure that we have 10 million customer records inserted.
  - Look for this message `database system is ready to accept connections` in docker compose log
  - Launch `PGAdmin4` with the below connection parameters

#### Database Connection Parameters

| Database Server Details | Values                    |
|-------------------------|---------------------------|
| Register Server Name    | customer-r2dbc-vs-jdbc    |
| Host name/address       | localhost                 |
| Port                    | 5432                      |
| Username                | postgres                  |
| Password                | password                  |

#### How to run?
```bash
$ cd r2dbc-vs-jdbc/
```
- We have 2 tests in each of the module. Take a look at the makefile.
  - `Efficiency Test`
  - `Throughput Test`

```bash
$ make package
$ make reactive-throughput-test
$ make traditional-throughput-test
$ make traditional-efficiency-test
$ make reactive-efficiency-test
```