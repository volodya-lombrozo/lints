# `@line` Is Absent

In [XMIR], each `<o/>` with it's `@base`, must have `@line` attribute as well.

Incorrect:

```xml
<program>
  <objects>
    <o>
      <o name="bar" base="foo"/>
    </o>
  </objects>
</program>
```

Correct:

```xml
<program>
  <objects>
    <o>
      <o name="bar" base="foo" line="1"/>
    </o>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
