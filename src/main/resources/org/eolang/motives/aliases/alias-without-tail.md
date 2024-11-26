# Alias Without Tail

Object alias must have a tail.

Incorrect:

```eo
+alias

# Foo.
[] > foo
```

Correct:

```eo
+alias a
+alias a b
+alias a b c
+alias a b c d

# Foo.
[] > foo
```
