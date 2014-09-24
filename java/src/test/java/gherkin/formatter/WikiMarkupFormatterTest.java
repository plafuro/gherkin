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

         List<String> lines = doFormatter(basicFeatureDescriptionWithTag, true);

         asserAmountOfLinesAsExpected(lines, 3);

         assertLine(lines, 0, "=Some terse yet descriptive text of what is desired=");
         assertLine(lines, 1, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'very_important', 'crazy_stuff' '''''");
         assertLine(lines, 2, "Textual description of the business value of this feature Business rules that govern the scope of the feature Any additional information that will make the feature easier to understand");
     }

     @Test
     public void backgroundShouldbeFormattedAsDesired() throws IOException {

         String basicFeatureDescription = basicFeatureDescriptionWithTag + "\r\n" + background;

         List<String> lines = doFormatter(basicFeatureDescription, true);

         asserAmountOfLinesAsExpected(lines, 11);

         assertLine(lines, 0, "=Some terse yet descriptive text of what is desired=");
         assertLine(lines, 1, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'very_important', 'crazy_stuff' '''''");
         assertLine(lines, 2, "Textual description of the business value of this feature Business rules that govern the scope of the feature Any additional information that will make the feature easier to understand");
         assertLine(lines, 3, "==Some prerequisites..==");
         assertLine(lines, 4, "");
         assertLine(lines, 5, "{|");
         assertLine(lines, 6, "|-");
         assertLine(lines, 7, "|| Given || somebody is there");
         assertLine(lines, 8, "|-");
         assertLine(lines, 9, "|| And || they do their thing");
         assertLine(lines, 10, "|}");
     }

     @Test
     public void basicScenarioShouldbeFormattedAsDesired() throws IOException {

         String basicFeatureDescription = basicFeatureDescriptionWithTag + "\r\n" + basicScenario;

         List<String> lines = doFormatter(basicFeatureDescription, true);

         asserAmountOfLinesAsExpected(lines, 21);

         assertLine(lines, 0, "=Some terse yet descriptive text of what is desired=");
         assertLine(lines, 1, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'very_important', 'crazy_stuff' '''''");
         assertLine(lines, 2, "Textual description of the business value of this feature Business rules that govern the scope of the feature Any additional information that will make the feature easier to understand");
         assertLine(lines, 3, "==Some determinable business situation==");
         assertLine(lines, 4, "");
         assertLine(lines, 5, "{|");
         assertLine(lines, 6, "|-");
         assertLine(lines, 7, "|| Given || some precondition");
         assertLine(lines, 8, "|-");
         assertLine(lines, 9, "|| And || some other precondition");
         assertLine(lines, 10, "|-");
         assertLine(lines, 11, "|| When || some action by the actor");
         assertLine(lines, 12, "|-");
         assertLine(lines, 13, "|| And || some other action");
         assertLine(lines, 14, "|-");
         assertLine(lines, 15, "|| And || yet another action");
         assertLine(lines, 16, "|-");
         assertLine(lines, 17, "|| Then || some testable outcome is achieved");
         assertLine(lines, 18, "|-");
         assertLine(lines, 19, "|| And || something else we can check happens too");
         assertLine(lines, 20, "|}");
     }

     @Test
     public void scenarioWithoutDescriptionWithTagShouldBeFormattedAsDesired() throws IOException {

         String basicFeatureDescription = basicFeatureDescriptionWithTag + "\r\n" + scenarioWithoutDescriptionWithTag;

         List<String> lines = doFormatter(basicFeatureDescription, true);

         asserAmountOfLinesAsExpected(lines, 22);

         assertLine(lines, 0, "=Some terse yet descriptive text of what is desired=");
         assertLine(lines, 1, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'very_important', 'crazy_stuff' '''''");
         assertLine(lines, 2, "Textual description of the business value of this feature Business rules that govern the scope of the feature Any additional information that will make the feature easier to understand");
         assertLine(lines, 3, "=={{color|red|''Undefined section''}}==");
         assertLine(lines, 4, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'lame' '''''");
         assertLine(lines, 5, "");
         assertLine(lines, 6, "{|");
         assertLine(lines, 7, "|-");
         assertLine(lines, 8, "|| Given || some precondition");
         assertLine(lines, 9, "|-");
         assertLine(lines, 10, "|| And || some other precondition");
         assertLine(lines, 11, "|-");
         assertLine(lines, 12, "|| When || some action by the actor");
         assertLine(lines, 13, "|-");
         assertLine(lines, 14, "|| And || some other action");
         assertLine(lines, 15, "|-");
         assertLine(lines, 16, "|| And || yet another action");
         assertLine(lines, 17, "|-");
         assertLine(lines, 18, "|| Then || some testable outcome is achieved");
         assertLine(lines, 19, "|-");
         assertLine(lines, 20, "|| And || something else we can check happens too");
         assertLine(lines, 21, "|}");
     }

     @Test
     public void scenarioWithGivenTableShouldBeFormattedAsDesired() throws IOException {

               String basicFeatureDescription = basicFeatureDescriptionWithTag + "\r\n" + scenarioWithGivenTable;

               List<String> lines = doFormatter(basicFeatureDescription, true);

               asserAmountOfLinesAsExpected(lines, 31);

               assertLine(lines, 0, "=Some terse yet descriptive text of what is desired=");
               assertLine(lines, 1, " {{color|blue|<big>ℹ</big>}} This section is tagged as''''' 'very_important', 'crazy_stuff' '''''");
               assertLine(lines, 2, "Textual description of the business value of this feature Business rules that govern the scope of the feature Any additional information that will make the feature easier to understand");
               assertLine(lines, 3, "==scenario with a table!==");
               assertLine(lines, 4, "");
               assertLine(lines, 5, "{|");
               assertLine(lines, 6, "|-");
               assertLine(lines, 7, "|| Given || all these people:");
               assertLine(lines, 8, "{|");
               assertLine(lines, 10, "|| name|| email|| phone");
               assertLine(lines, 11, "|-");
               assertLine(lines, 12, "|| Aslak|| aslak@email.com|| 123");
               assertLine(lines, 13, "|-");
               assertLine(lines, 14, "|| Matt|| matt@email.com|| 234");
               assertLine(lines, 15, "|-");
               assertLine(lines, 16, "|| Joe|| joe@email.org|| 456");
               assertLine(lines, 17, "|}");
               assertLine(lines, 18, "|-");
               assertLine(lines, 19, "|| And || some other precondition");
               assertLine(lines, 20, "|-");
               assertLine(lines, 21, "|| When || some action by the actor");
               assertLine(lines, 22, "|-");
               assertLine(lines, 23, "|| And || some other action");
               assertLine(lines, 24, "|-");
               assertLine(lines, 25, "|| And || yet another action");
               assertLine(lines, 26, "|-");
               assertLine(lines, 27, "|| Then || some testable outcome is achieved");
               assertLine(lines, 28, "|-");
               assertLine(lines, 29, "|| And || something else we can check happens too");
               assertLine(lines, 30, "|}");
           }

     private void asserAmountOfLinesAsExpected(List<String> lines, int expected) {
         assertEquals("Formatter produces unexpected quantity of lines. ", expected, lines.size());
     }

     private void assertLine(List<String> lines, int position, String expected) {
         assertEquals(expected, lines.get(position));
     }

     private List<String> doFormatter(String feature, boolean renderTags) throws IOException {

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         PrintStream out = new PrintStream(baos);

         Formatter formatter = new WikiMarkupFormatter(out, renderTags);
         Parser parser = new Parser(formatter);
         parser.parse(feature, "", 0);
         formatter.close();

         return extractLines(baos);
     }

     private List<String> extractLines(ByteArrayOutputStream baos) throws IOException {
         BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));

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