package gherkin.formatter;

import java.util.HashMap;
import java.util.Map;

public class WikiMarkup implements Formats {

    public static enum Formats {
        SECTION1, SECTION2, ITALICS, BOLD, COLOR_GREY, COLOR_RED, NORMAL, TABLE, TABLE_ROW, TABLE_CELL
    }

    private static final Map<Formats, Format> formats = new HashMap<Formats, Format>() {{
        put(Formats.SECTION1, new EnclosingFormat("="));
        put(Formats.SECTION2, new EnclosingFormat("=="));
        put(Formats.ITALICS, new EnclosingFormat("''"));
        put(Formats.BOLD, new EnclosingFormat("'''"));
        put(Formats.NORMAL, new EnclosingFormat(""));
        put(Formats.COLOR_GREY, new CurlyBracesEnclosedFormat("color|grey|"));
        put(Formats.COLOR_RED, new CurlyBracesEnclosedFormat("color|red|"));
        put(Formats.TABLE_ROW, new TableRowFormat());
        put(Formats.TABLE_CELL, new TableCellFormat());
        put(Formats.TABLE, new TableFormat());
    }};

    public static class EnclosingFormat implements Format {
        private final String enclosure;

        public EnclosingFormat(String enclosure) {
            this.enclosure = enclosure;
        }

        public String text(String text) {
            return enclosure + text + enclosure;
        }
    }

    public static class TableRowFormat implements Format {

        public TableRowFormat() {
        }

        public String text(String cells) {
            return "|-\r\n" + cells;
        }
    }

    public static class TableCellFormat implements Format {

        public String text(String text) {
            return "|| " + text;
        }
    }

    public static class CurlyBracesEnclosedFormat implements Format {
        private final String settings;

        public CurlyBracesEnclosedFormat(String settings) {
            this.settings = settings;
        }

        public String text(String text) {
            return "{{" + settings + text + "}}";
        }
    }

    public Format get(String key) {
        Format format = formats.get(Formats.valueOf(key));
        if (format == null) throw new NullPointerException("No format for key " + key);
        return format;
    }

    public Format get(Formats key) {
        return formats.get(key);
    }

    public String up(int n) {
        return "";
    }

    private static class TableFormat implements Format {
        @Override
        public String text(String text) {
            return "{|\r\n" + text + "\r\n}}";
        }
    }
}
