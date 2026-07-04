# Mandatory `+architect` Meta

The program must have exactly one `+architect` special meta.

Incorrect:

```eo
[] > foo
```

Correct:

```eo
+architect foo@example.com

[] > foo
```
