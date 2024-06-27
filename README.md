## Wait-for graph

### Example
* Input:
```sql
read(T1,A2)
read(T2,A1)
read(T2,A3)
read(T3,A3)
read(T2,A1)
read(T3,A2)
end(T2)
end(T1)
```

* Output:
```sql
read(T1,A2) finished
read(T2,A1) finished
read(T2,A3) finished
read(T3,A3) suspended
read(T2,A1) finished
read(T3,A2) suspended
read(T3,A3) finished
read(T3,A2) suspended
read(T3,A2) finished
```