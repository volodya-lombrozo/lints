import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.junit.jupiter.api.Test;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

final class LintsItTest {

    @Test
    void runs() {
        final XML xmir = new XMLDocument("<program/>");
        for (final Lint lint : new XslLints()) {
            MatcherAssert.assertThat(
                "passes with no exceptions",
                lint.defects(xmir),
                Matchers.notNullValue()
            );
        }
    }
}
