# Incorrect `+spdx`

Tail of special meta `+spdx` should be [SPDX]-compliant headers.

Incorrect:

```eo
+spdx foo bar

# Foo.
[] > foo
```

Correct:

```eo
+spdx SPDX-License-Identifier: MIT

# Foo.
[] > foo
```

[SPDX]: https://en.wikipedia.org/wiki/Software_Package_Data_Exchange
