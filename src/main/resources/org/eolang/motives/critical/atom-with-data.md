# Atom With Data

Each atom, represented by `<o/>` containing `@name='λ'`, in [XMIR], must not have
the text data inside.

Incorrect:

```xml
<program>
  <objects>
    <o name="λ">
      A1-B2-C3-D4-E5
    </o>
  </objects>
</program>
```

Correct:

```xml
<program>
  <objects>
    <o name="λ"/>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
