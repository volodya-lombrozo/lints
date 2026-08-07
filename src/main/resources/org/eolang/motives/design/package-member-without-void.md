# Package member without void

A member of a package is also implicitly a method of any object whose forma
matches the package name: `bytes.as-number x` and `x.as-number` reach the
same `Φ.bytes.as-number` object, because the runtime binds the receiver into
the member's first void attribute when the implicit, dotted form is used. A
member that declares no void attributes has no slot for that receiver, so
the implicit form fails at runtime, even though the explicit, namespaced
form works fine.

Incorrect:

```eo
+package bytes

# As number.
[] > as-number
```

Correct:

```eo
+package bytes

# As number.
[b] > as-number
```

Only top-level objects of a file that declares `+package` are checked: a
void-less formation is fine everywhere else, for example a ready-made
constant such as `[] > stdin`, or a local helper nested inside another
formation's body.

See [objectionary/lints#1170] for the discussion that motivated this lint.

[objectionary/lints#1170]: https://github.com/objectionary/lints/issues/1170
