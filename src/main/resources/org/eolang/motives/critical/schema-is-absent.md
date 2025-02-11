# Schema Is Absent

[XMIR] document must have `xsi:noNamespaceSchemaLocation` inside it.

Incorrect:

```xml
<program>
  <objects/>
</program>
```

Correct:

```xml
<program xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="https://www.eolang.org/xsd/XMIR-anything.xsd">
  <objects/>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
