# Alias Without Tail

All aliases must have tail after it.

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
