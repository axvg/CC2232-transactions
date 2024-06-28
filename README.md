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
read(T1,A2) is executing
read(T2,A1) is executing
read(T3,A3) is executing
read(T3,A2) is suspended
read(T3,A1) is suspended
End transaction T2
Executing suspended commands...
read(T3,A2) is suspended
read(T3,A1) is executing
End transaction T1
Executing suspended commands...
read(T3,A2) is executing
```

* Cycle
```sql
read(T1,A1)
read(T2,A2)
read(T3,A3)
read(T1,A2)
read(T2,A3)
read(T3,A1)
end(T1)
end(T2)
end(T3)
```