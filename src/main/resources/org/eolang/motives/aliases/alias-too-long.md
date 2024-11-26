# Alias Too Long

Object alias must have **2 parts at max**. 

Incorrect:

```eo
+alias a b c

# Foo.
[] > foo
```

Correct:

```eo
+alias
+alias a
+alias a b
+alias a b.c.d.e

# Foo.
[] > foo
```
