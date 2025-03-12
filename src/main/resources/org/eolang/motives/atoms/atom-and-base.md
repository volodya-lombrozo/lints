# Atom and Base

In [XMIR], atom object can not have `@base` attribute.

Incorrect:

```xml
<program>
  <objects>
    <o name="obj" base="bar">
      <o name="λ"/>
    </o>
  </objects>
</program>
```

Correct:

```xml
<program>
  <objects>
    <o name="obj">
      <o name="λ"/>
    </o>
  </objects>
</program>
```

or:

```xml
<program>
  <objects>
    <o name="obj" base="bar">
      <o name="foo"/>
    </o>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
