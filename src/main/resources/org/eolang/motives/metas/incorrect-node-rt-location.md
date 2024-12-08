# Incorrect `+rt` node location

The location of NodeJS runtime should follow the regexp:

```regexp
^([a-zA-Z0-9_.-]+):([0-9_.-]+)$
```

Incorrect:

```eo
+rt node foo
+rt node hello, world!
+rt node bar-test:test:1.2.3

# No comments.
[] > foo
```

Correct:

```eo
+rt node eo2js-runtime:0.0.0
+rt node bar-test:1.1
+rt node foo:1.2.3

# No comments.
[] > foo
```
