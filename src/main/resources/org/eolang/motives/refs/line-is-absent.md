# `@line` is absent

In [XMIR], each `<o/>` element must also have a `@line` attribute.

Incorrect:

```xml
<object>
  <o>
    <o name="bar"/>
  </o>
</object>
```

Correct:

```xml
<object>
  <o line="1">
    <o name="bar" line="1"/>
  </o>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
