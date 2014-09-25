package gherkin.formatter;


import gherkin.parser.Parser;
 import org.junit.Test;

 import java.io.*;
 import java.util.ArrayList;
 import java.util.List;

 import static org.junit.Assert.assertEquals;

public class WikiMarkupFormatterTest {

     private static String basicFeatureDescriptionWithTag =
             "#language: en\r\n" +
             "@very_important @crazy_stuff\r\n" +
             "Feature: Some terse yet descriptive text of what is desired\r\n" +
             " Textual description of the business value of this feature\r\n" +
             " Business rules that govern the scope of the feature\r\n" +
             " Any additional information that will make the feature easier to understand\r\n";

     private static String background =
             " Background: Some prerequisites..\r\n" +
             "   Given somebody is there \r\n" +
             "     And they do their thing\r\n";

     private static String basicScenario =
             " Scenario: Some determinable business situation\r\n" +
             "   Given some precondition\r\n" +
             "     And some other precondition\r\n" +
             "    When some action by the actor\r\n" +
             "      And some other action\r\n" +
             "      And yet another action\r\n" +
             "     Then some testable outcome is achieved\r\n" +
             "      And something else we can check happens too\r\n";

     private static String scenarioWithoutDescriptionWithTag =
             " @lame\r\n" +
             " Scenario:\r\n" +
             "   Given some precondition\r\n" +
             "     And some other precondition\r\n" +
             "    When some action by the actor\r\n" +
             "      And some other action\r\n" +
             "      And yet another action\r\n" +
             "     Then some testable outcome is achieved\r\n" +
             "      And something else we can check happens too\r\n";

     private static String scenarioWithGivenTable =
             " Scenario: scenario with a table!\r\n" +
             "   Given all these people:\r\n" +
             "     | name  | email           | phone |\n" +
             "     | Aslak | aslak@email.com | 123   |\n" +
             "     | Matt  | matt@email.com  | 234   |\n" +
             "     | Joe   | joe@email.org   | 456   |\n" +
             "     And some other precondition\r\n" +
             "    When some action by the actor\r\n" +
             "      And some other action\r\n" +
             "      And yet another action\r\n" +
             "     Then some testable outcome is achieved\r\n" +
             "      And something else we can check happens too\r\n";

     private static String scenarioOutlineWithExampleTable =
             " Scenario Outline: eating\n" +
             "  Given there are <start> cucumbers\n" +
             "  When I eat <eat> cucumbers\n" +
             "  Then I should have <left> cucumbers\n" +
             "\n" +
             "  Examples:\n" +
             "    | start | eat | left |\n" +
             "    |  12   |  5  |  7   |\n" +
             "    |  20   |  5  |  15  |";

     public static String gherkin =
             basicFeatureDescriptionWithTag +
                     "\r\n" +
                     background +
                     "\r\n" +
                     basicScenario +
                     "\r\n" +
                     scenarioWithoutDescriptionWithTag +
                     "\r\n" +
                     scenarioWithGivenTable +
                     "\r\n" +
                     scenarioOutlineWithExampleTable;

     @Test
     public void featureAndTagsShouldbeFormattedAsDesired() throws IOException {

         List<String> lines = doFormatter(basicFeatureDescriptionWithTag);

         asserAmountOfLinesAsExpected(lines, 3);

         assertLine(lines, 0, "=Some terse yet descriptive text of what is desired=");
         assertLine(lines, 1, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'very_important', 'crazy_stuff' '''''");
         assertLine(lines, 2, "Textual description of the business value of this feature Business rules that govern the scope of the feature Any additional information that will make the feature easier to understand");
     }

     @Test
     public void backgroundShouldbeFormattedAsDesired() throws IOException {

         String basicFeatureDescription = basicFeatureDescriptionWithTag + "\r\n" + background;

         List<String> lines = doFormatter(basicFeatureDescription);

         asserAmountOfLinesAsExpected(lines, 9);

         assertLine(lines, 0, "=Some terse yet descriptive text of what is desired=");
         assertLine(lines, 1, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'very_important', 'crazy_stuff' '''''");
         assertLine(lines, 2, "Textual description of the business value of this feature Business rules that govern the scope of the feature Any additional information that will make the feature easier to understand");
         assertLine(lines, 3, "==Some prerequisites..==");
         assertLine(lines, 4, "");
         assertLine(lines, 5, "<table style=\"border:none\">");
         assertLine(lines, 6, "<tr><td style=\"border:none\">Given</td><td style=\"border:none\">somebody is there</td></tr>");
         assertLine(lines, 7, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">they do their thing</td></tr>");
         assertLine(lines, 8, "</table>");
     }

     @Test
     public void basicScenarioShouldbeFormattedAsDesired() throws IOException {

         String basicFeatureDescription = basicFeatureDescriptionWithTag + "\r\n" + basicScenario;

         List<String> lines = doFormatter(basicFeatureDescription);

         asserAmountOfLinesAsExpected(lines, 14);

         int l = 0;
         assertLine(lines,   l, "=Some terse yet descriptive text of what is desired=");
         assertLine(lines, ++l, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'very_important', 'crazy_stuff' '''''");
         assertLine(lines, ++l, "Textual description of the business value of this feature Business rules that govern the scope of the feature Any additional information that will make the feature easier to understand");
         assertLine(lines, ++l, "==Some determinable business situation==");
         assertLine(lines, ++l, "");
         assertLine(lines, ++l, "<table style=\"border:none\">");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">Given</td><td style=\"border:none\">some precondition</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">some other precondition</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">When</td><td style=\"border:none\">some action by the actor</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">some other action</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">yet another action</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">Then</td><td style=\"border:none\">some testable outcome is achieved</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">something else we can check happens too</td></tr>");
         assertLine(lines, ++l, "</table>");
     }

     @Test
     public void scenarioWithoutDescriptionWithTagShouldBeFormattedAsDesired() throws IOException {

         String basicFeatureDescription = basicFeatureDescriptionWithTag + "\r\n" + scenarioWithoutDescriptionWithTag;

         List<String> lines = doFormatter(basicFeatureDescription);

         asserAmountOfLinesAsExpected(lines, 15);
         int l = 0;
         assertLine(lines, l, "=Some terse yet descriptive text of what is desired=");
         assertLine(lines, ++l, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'very_important', 'crazy_stuff' '''''");
         assertLine(lines, ++l, "Textual description of the business value of this feature Business rules that govern the scope of the feature Any additional information that will make the feature easier to understand");
         assertLine(lines, ++l, "=={{color|red|''Undefined section''}}==");
         assertLine(lines, ++l, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'lame' '''''");
         assertLine(lines, ++l, "");
         assertLine(lines, ++l, "<table style=\"border:none\">");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">Given</td><td style=\"border:none\">some precondition</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">some other precondition</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">When</td><td style=\"border:none\">some action by the actor</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">some other action</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">yet another action</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">Then</td><td style=\"border:none\">some testable outcome is achieved</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">something else we can check happens too</td></tr>");
         assertLine(lines, ++l, "</table>");
     }

     @Test
     public void scenarioWithGivenTableShouldBeFormattedAsDesired() throws IOException {

         String basicFeatureDescription = basicFeatureDescriptionWithTag + "\r\n" + scenarioWithGivenTable;

         List<String> lines = doFormatter(basicFeatureDescription);

         asserAmountOfLinesAsExpected(lines, 20);
         int l = 0;
         assertLine(lines, l, "=Some terse yet descriptive text of what is desired=");
         assertLine(lines, ++l, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'very_important', 'crazy_stuff' '''''");
         assertLine(lines, ++l, "Textual description of the business value of this feature Business rules that govern the scope of the feature Any additional information that will make the feature easier to understand");
         assertLine(lines, ++l, "==scenario with a table!==");
         assertLine(lines, ++l, "");
         assertLine(lines, ++l, "<table style=\"border:none\">");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">Given</td><td style=\"border:none\">all these people:</td></tr>");
         assertLine(lines, ++l, "<table style=\"border:none\">");
         assertLine(lines, ++l, "<tr><th style=\"border:none\">name</th><th style=\"border:none\">email</th><th style=\"border:none\">phone</th></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">Aslak</td><td style=\"border:none\">aslak@email.com</td><td style=\"border:none\">123</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">Matt</td><td style=\"border:none\">matt@email.com</td><td style=\"border:none\">234</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">Joe</td><td style=\"border:none\">joe@email.org</td><td style=\"border:none\">456</td></tr>");
         assertLine(lines, ++l, "</table>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">some other precondition</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">When</td><td style=\"border:none\">some action by the actor</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">some other action</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">yet another action</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">Then</td><td style=\"border:none\">some testable outcome is achieved</td></tr>");
         assertLine(lines, ++l, "<tr><td style=\"border:none\">And</td><td style=\"border:none\">something else we can check happens too</td></tr>");
         assertLine(lines, ++l, "</table>");
     }

     private void asserAmountOfLinesAsExpected(List<String> lines, int expected) {
         assertEquals("Formatter produces unexpected quantity of lines. ", expected, lines.size());
     }

     private void assertLine(List<String> lines, int position, String expected) {
         assertEquals("On line " + position, expected, lines.get(position));
     }

     private List<String> doFormatter(String feature) throws IOException {

         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         PrintStream out = new PrintStream(byteArrayOutputStream);

         Formatter formatter;
         formatter = new WikiMarkupFormatter(out, new WikiMarkupFormatter.Options(true, null));
         Parser parser = new Parser(formatter);
         parser.parse(feature, "", 0);
         formatter.close();

         return extractLines(byteArrayOutputStream);
     }

     private List<String> extractLines(ByteArrayOutputStream byteArrayOutputStream) throws IOException {
         BufferedReader br = new BufferedReader(new InputStreamReader(
                 new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));

         String line;
         List<String> lines = new ArrayList<String>();
         int lineNumber = 0;

         while ((line = br.readLine()) != null) {
             System.out.println(lineNumber + ":" + line);
             lineNumber++;
             lines.add(line);
         }
         return lines;
     }
 }