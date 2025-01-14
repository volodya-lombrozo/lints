# Atom is not unique

All atom values (`@atom` attribute that `<o/>` may have in [XMIR]) across all
`.eo` (or `.xmir`) files must not be duplicated.

Incorrect:

`foo.eo`:

```eo
# Foo.
[attr] > foo
  [] > test /int
```

`bar.eo`:

```eo
# Bar.
[attr] > bar
  [] > test /int
```

Atom, duplicated in single file is prohibited too:

`xyz.eo`:

```eo
# Xyz.
[attr] > xyz
  [] > foo /int
  [] > bar /int
```

To fix this, remove duplicated atom.
