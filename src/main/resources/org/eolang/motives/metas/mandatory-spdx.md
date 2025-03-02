# Mandatory `+spdx`

Special meta `+spdx` should be present in each `.eo` program.

Incorrect:

```eo
# Foo.
[] > foo
```

Correct:

```eo
+spdx SPDX-License-Identifier: MIT

# Foo.
[] > foo
```
