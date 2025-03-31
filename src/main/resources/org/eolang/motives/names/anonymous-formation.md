# Anonymous Formation

Formation should have name and be present as separate object.

Incorrect:

```eo
# Main.
malloc.of
  64
  [m]
    QQ.io.stdout > @
      "foo"
```

Correct:

```eo
# Main.
malloc.of
  64
  [m] >>
    QQ.io.stdout > @
      "foo"
```
