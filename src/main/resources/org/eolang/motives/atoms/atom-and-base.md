# Atom and Base

In [XMIR], inside `<o/>`, both, `@name='λ'` and `@base` cannot co-exist.

Incorrect:

```xml
<program>
  <objects>
    <o>
      <o name="λ" base="bar"/>
    </o>
  </objects>
</program>
```

Correct:

```xml
<program>
  <objects>
    <o>
      <o name="λ"/>
    </o>
  </objects>
</program>
```

or:

```xml
<program>
  <objects>
    <o>
      <o name="foo" base="bar"/>
    </o>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
