## Wait-for graph

### Example
* Input:
```sql
read(T1,A2)
read(T2,A1)
read(T3,A3)
read(T3,A2)
read(T3,A1)
end(T2)
end(T1)
```

* Output:
```sql
read(T1,A2) is finished
read(T2,A1) is finished
read(T3,A3) is finished
read(T3,A2) is suspended
read(T3,A1) is suspended
Executing suspended commands...
read(T3,A2) is suspended
read(T3,A1) is finished
Executing suspended commands...
read(T3,A2) is finished
```