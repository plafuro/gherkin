package gherkin.formatter;

import com.sun.deploy.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class WikiMarkup implements Formats {

    public static final String TABLE_START = "<table style=\"border:none\">";
    public static final String TABLE_END = "</table>";
    public static final String BIG_BLUE_INFORMATION_SIGN = "{{color|blue|<big>ℹ</big>}}";

    public static enum Formats {
        SECTION1, SECTION2, ITALICS, BOLD, COLOR_GREY, COLOR_RED, NORMAL, TABLE, TABLE_HEAD, TABLE_ROW, TABLE_CELL
    }

    private static final Map<Formats, Format> formats = new HashMap<Formats, Format>() {{
        put(Formats.SECTION1, new EnclosingFormat("="));
        put(Formats.SECTION2, new EnclosingFormat("=="));
        put(Formats.ITALICS, new EnclosingFormat("''"));
        put(Formats.BOLD, new EnclosingFormat("'''"));
        put(Formats.NORMAL, new EnclosingFormat(""));
        put(Formats.COLOR_GREY, new Color("grey"));
        put(Formats.COLOR_RED, new Color("red"));
        put(Formats.TABLE_ROW, new HtmlTagFormat("tr"));
        put(Formats.TABLE_HEAD, new HtmlTagFormat("th","border:none"));
        put(Formats.TABLE_CELL, new HtmlTagFormat("td","border:none"));
        put(Formats.TABLE, new HtmlTagFormat("tr"));
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

    public static class CurlyBracesEnclosedFormat implements Format {
        private final String settings;

        public CurlyBracesEnclosedFormat(String settings) {
            this.settings = settings;
        }

        public String text(String text) {
            return "{{" + settings + text + "}}";
        }
    }

    public static class Color extends CurlyBracesEnclosedFormat {

        public Color(String color) {
            super("color|"+color+"|");
        }
    }

    public static class HtmlTagFormat implements  Format{

        private final String tagName;
        private final String style;

        public HtmlTagFormat(String tagName, String style){
            this.tagName = tagName;
            this.style = style;
        }

        public HtmlTagFormat(String tagName){
            this.tagName = tagName;
            this.style = "";
        }

        @Override
        public String text(String text) {
            String styleElement = !style.isEmpty() ? " style=\""+style+"\"" : style;
            return "<"+tagName+styleElement+">"+ StringUtils.trimWhitespace(text)+"</"+tagName+">";
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
}
