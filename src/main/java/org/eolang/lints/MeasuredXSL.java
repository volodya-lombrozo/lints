package org.eolang.lints;

import com.jcabi.log.Logger;
import com.jcabi.xml.Sources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XSL;

public final class MeasuredXSL implements XSL {

    private final static long DEFAULT = 100L;

    private final String name;

    private final XSL origin;
    private final long threshold;

    public MeasuredXSL(final String name, final XSL origin) {
        this(name, origin, MeasuredXSL.DEFAULT);
    }

    private MeasuredXSL(final String name, final XSL origin, final long threshold) {
        this.name = name;
        this.origin = origin;
        this.threshold = threshold;
    }

    @Override
    public XML transform(final XML xml) {
        final long start = System.currentTimeMillis();
        final XML res = this.origin.transform(xml);
        final long end = System.currentTimeMillis();
        if (end - start > this.threshold) {
            Logger.warn(
                this,
                "XSL transformation '%s' took %[ms]s, whereas threshold is %[ms]s\n",
                this.name,
                end - start,
                this.threshold
            );
        }
        return res;
    }

    @Override
    public String applyTo(final XML xml) {
        return this.origin.applyTo(xml);
    }

    @Override
    public XSL with(final Sources src) {
        return this.origin.with(src);
    }

    @Override
    public XSL with(final String name, final Object value) {
        return this.origin.with(name, value);
    }
}
