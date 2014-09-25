package gherkin.formatter;

import gherkin.formatter.model.*;
import gherkin.util.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static gherkin.formatter.WikiMarkup.Formats.*;
import static gherkin.util.FixJava.join;
import static gherkin.util.FixJava.map;

/**
   * This class pretty prints feature files in Wikimarkup. This class prints feature, backround,
   * scenarios and their tags.
   */
 public class WikiMarkupFormatter implements Formatter {


    private final NiceAppendable out;
    private final Options options;
    private Mapper<Tag, String> tagNameMapper = new Mapper<Tag, String>() {
          @Override
          public String map(Tag tag) {
              return tag.getName().replace("@", "");
          }
    };
    private WikiMarkup formats;
    private List<Step> steps = new ArrayList<Step>();
    private List<MatchResultPair> matchesAndResults = new ArrayList<MatchResultPair>();
    private DescribedStatement statement;

    public WikiMarkupFormatter(Appendable out, Options options) {
        this.out = new NiceAppendable(out);
        this.options = options;
        this.formats = new WikiMarkup();
    }

    @Override
    public void uri(String uri) {
    }

    @Override
    public void feature(Feature feature) {
        out.println(getFormat(SECTION1).text(feature.getName()));
        List<Tag> tags = feature.getTags();
        printTags(tags);
        printDescription(feature.getDescription().replace("\r\n", " "), "", false);
    }

    @Override
    public void background(Background background) {
        replay();
        statement = background;
    }

    @Override
    public void scenario(Scenario scenario) {
        replay();
        statement = scenario;
    }

    @Override
    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        replay();
        statement = scenarioOutline;
    }

    @Override
    public void startOfScenarioLifeCycle(Scenario scenario) {
        // NoOp
    }

    @Override
    public void endOfScenarioLifeCycle(Scenario scenario) {
        // NoOp
    }

    @Override
    public void examples(Examples examples) {
        replay();
        out.println();
        printComments(examples.getComments(), " ");
        printTags(examples.getTags());
        out.println(" " + examples.getKeyword() + ": " + examples.getName());
        printDescription(examples.getDescription(), " ", true);
        renderTable(examples.getRows());
    }

    @Override
    public void step(Step step) {
        steps.add(step);
    }

    @Override
    public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void done() {
    }

    @Override
    public void close() {
        out.close();
    }

    private void replay() {
        printStatement();
        printSteps();
    }

    private void printSteps() {
        if (steps.isEmpty()) return;
        out.println(WikiMarkup.TABLE_START);
        while (!steps.isEmpty()) {
            if (matchesAndResults.isEmpty()) {
                printStep();
            } else {
                MatchResultPair matchAndResult = matchesAndResults.remove(0);
                printStep();
                if (matchAndResult.hasResultErrorMessage()) {
                    printError(matchAndResult.result);
                }
            }
        }
        out.println(WikiMarkup.TABLE_END);
    }

    private void printStatement() {
        if (statement == null) return;

        StringBuilder buffer = new StringBuilder();
        buffer.append(
                formats.get(SECTION2).text(
                        statement.getName().isEmpty() ?
                                getFormat(COLOR_RED).text(getFormat(ITALICS).text("Undefined section")) :
                                statement.getName()
                ));

        out.println(buffer);
        if (statement instanceof TagStatement) {
            printTags(((TagStatement) statement).getTags());
        }
        out.println(statement.getDescription());
        statement = null;
    }

    private void printStep() {
        Step step = steps.remove(0);
        printComments(step.getComments(), " ");
        out.println(getFormat(TABLE_ROW).text(getFormat(TABLE_CELL).text(step.getKeyword()) +
                getFormat(TABLE_CELL).text(step.getName())));
        if (step.getRows() != null) {
            renderTable(step.getRows());
        } else if (step.getDocString() != null) {
            docString(step.getDocString());
        }
    }

    private Format getFormat(WikiMarkup.Formats key) {
        return formats.get(key);
    }

    private void renderTable(List<? extends Row> rows) {
        if (rows.isEmpty()) return;

        out.println(WikiMarkup.TABLE_START);
        for (int i = 0; i < rows.size(); i++) {
            Format cellFormat = (i == 0)? getFormat(TABLE_HEAD) : getFormat(TABLE_CELL);

            String cells = "";
            for (String cellContents : rows.get(i).getCells()) {
                cells += cellFormat.text(cellContents);
            }
            out.println(getFormat(TABLE_ROW).text(cells));
        }
        out.println(WikiMarkup.TABLE_END);
    }

    private void printError(Result result) {
        Format failed = formats.get("failed");
        out.println(indent(failed.text(result.getErrorMessage()), " "));
    }

    private void docString(DocString docString) {
        out.println(" \"\"\"");
        out.println(escapeTripleQuotes(indent(docString.getValue(), " ")));
        out.println(" \"\"\"");
    }

    public void eof() {
        replay();
    }

    private void printComments(List<Comment> comments, String indent) {
        for (Comment comment : comments) {
            out.println(indent + comment.getValue());
        }
    }

    private void printTags(List<Tag> tags) {
        if (tags.isEmpty() || !options.isTagRenderingActive()) return;

        NiceAppendable appendable = out.append(" ");
        appendable.append(options.getInformationSign() != null ? options.getInformationSign() : WikiMarkup.BIG_BLUE_INFORMATION_SIGN);
        appendable.append(" This section is tagged as");
        appendable.append(getFormat(BOLD).text(getFormat(ITALICS).text(" '" + join(map(tags, tagNameMapper), "', '") + "' ")));
        appendable.println();
    }

    private void printDescription(String description, String indentation, boolean newline) {
        if (!"".equals(description)) {
            out.println(indent(description, indentation));
            if (newline) out.println();
        }
    }

    private static final Pattern START = Pattern.compile("^", Pattern.MULTILINE);

    private static String indent(String s, String indentation) {
        return START.matcher(s).replaceAll(indentation);
    }

    private static final Pattern TRIPLE_QUOTES = Pattern.compile("\"\"\"", Pattern.MULTILINE);
    private static final String ESCAPED_TRIPLE_QUOTES = "\\\\\"\\\\\"\\\\\"";

    private static String escapeTripleQuotes(String s) {
        return TRIPLE_QUOTES.matcher(s).replaceAll(ESCAPED_TRIPLE_QUOTES);
    }

    public static class Options {
        private boolean tagRenderingActive;
        private String informationSign;

        public Options(boolean TagRenderingActive, String informationSign) {
            this.tagRenderingActive = TagRenderingActive;
            this.informationSign = informationSign;
        }

        public String getInformationSign() {
            return informationSign;
        }

        public boolean isTagRenderingActive() {
            return tagRenderingActive;
        }
    }
}