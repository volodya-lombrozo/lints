# Atom is not unique

All object FQNs that have `@atom` attribute across al `.eo` files must not be
duplicated.

Incorrect:

`foo.eo`:

```eo
+package xyz

# A.
[] > a /number
```

`bar.eo`:

```eo
+package xyz

# A.
[] > a /number
```

To fix this, remove duplicated atom.
