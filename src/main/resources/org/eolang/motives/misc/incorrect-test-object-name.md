# Incorrect test object name

Test object name must follow regexp:

```regexp
^[a-z][a-z]+(-[a-z][a-z]+)*$
```

Incorrect:

```eo
+tests

# Runs something.
[] > runs-something
  42 > tEst
  42 > test123
  42 > test--
  42 > test--test
  42 > t
  42 > test-test-
  42 > test-Test
  42 > test_abc
```

Correct:

```eo
+tests

# Runs something.
[] > runs-something
  42 > test
  42 > foo-bar
  42 > ok
```
