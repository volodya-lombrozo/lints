# Wrong test order

Unit tests must be located after live objects:

Incorrect:

```xml
<object>
  <o name="foo">
    <o name="bar"/>
    <o name="+runs-program"/>
    <o name="boom"/>
  </o>
</object>
```

Correct:

```xml
<object>
  <o name="foo">
    <o name="bar"/>
    <o name="boom"/>
    <o name="+runs-program"/>
  </o>
</object>
```
