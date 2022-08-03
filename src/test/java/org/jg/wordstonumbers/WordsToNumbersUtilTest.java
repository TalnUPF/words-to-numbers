package org.jg.wordstonumbers;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class WordsToNumbersUtilTest {

	public static Stream<Arguments> orignialTestData() {
		return Stream.of( 
			Arguments.of("one" , "1"), 
			Arguments.of("twelve", "12"),
			Arguments.of("thirty two", "32"),
			Arguments.of("three hundred", "300"),
			Arguments.of("four hundred and twenty", "420"),
			Arguments.of("six hundred and fifty two", "652"),
			Arguments.of("one thousand and twenty two", "1022"),
			Arguments.of("uno", "uno"), 
			Arguments.of("nity four", "nity 4"), 
			Arguments.of("234", "234"), 
			Arguments.of("three 23", "3 23"), 
			Arguments.of("there were twenty five thousand people", "there were 25000 people"), 
			Arguments.of("i was number one", "i was number 1"), 
			Arguments.of("I was number one", "I was number 1"), 
			Arguments.of("Twenty one People", "21 People"), 
			Arguments.of("Twenty one Thousand and one People were there", "21001 People were there"), 
			Arguments.of("it was twenty-four hours", "it was 24 hours"), 
			Arguments.of("it was twenty-four.", "it was 24."), 
			Arguments.of("it was twenty-test.", "it was 20 test."), 
			Arguments.of("it was test-four.", "it was test-four."), 
			Arguments.of("it was five-million, three hundred and twenty thousand, two hundred and twelve", "it was 5320212"), 
			Arguments.of("it was fifty two, that was two", "it was 52, that was 2"), 
			Arguments.of("it was fifty two: that was two", "it was 52: that was 2"), 
			Arguments.of("it was one-hundred, and twenty pieces", "it was 120 pieces"), 
			Arguments.of("it was fifty people, two more than me", "it was 50 people, 2 more than me"), 
			Arguments.of("there were twenty-five, more", "there were 25, more"), 
			Arguments.of("there were twenty-five-hours", "there were 25 hours"),
			Arguments.of("it was (seventy two knees) and (twenty one)", "it was (72 knees) and (21)"),
			Arguments.of("sun and moon are in line", "sun and moon are in line"),
			Arguments.of("and fifty and", "and 50 and"),
			Arguments.of("and fifty", "and 50"),
			Arguments.of("and and fifty", "and and 50"),
			Arguments.of("this (and fifty)", "this (and 50)"),
			Arguments.of("peas and rice", "peas and rice"),
			Arguments.of("twenty four mice and nine", "24 mice and 9"),
			Arguments.of("twenty four and pizza", "24 and pizza"),
			Arguments.of("and twenty four and pizza", "and 24 and pizza"),
			Arguments.of("and two thousand and four and pizza", "and 2004 and pizza"),
			Arguments.of("interval of between one and three weeks", "interval of between 1 and 3 weeks"),
			Arguments.of("interval of between twenty one and thirty three weeks", "interval of between 21 and 33 weeks"),
			Arguments.of("interval of between one hundred and one and thirty three weeks", "interval of between 101 and 33 weeks")
			);
	}
	
	public static Stream<Arguments> specialCasesTestData() {
		return Stream.of( 
			Arguments.of("Spaces, tabs and new lines kept", "\tninety six. A hundred.   \n\nTwo   three with fifty nine  .\nNo numbers", "\t96. 100.   \n\n2   3 with 59  .\nNo numbers"),
			//Arguments.of("Spaces, tabs and new lines not kept", "One million   fifteen thousand\nfour hundred\tand two.", "1015402."),
			Arguments.of("Initial and final symbols", "¡Twenty two!", "¡22!"),
			Arguments.of("Long number", "Is has cost over one million three hundred twenty five thousand nine hundred and seventy eight dollars.", "Is has cost over 1325978 dollars."),
			Arguments.of("Sentence dot", "Twenty. Two", "20. 2"),
			Arguments.of("Consecutive numbers", "six nine four hundred two fifty four eighty two.", "6 9 402 54 82."),
			Arguments.of("Consecutive single numbers", "six nine seven three four three eight three five six", "6 9 7 3 4 3 8 3 5 6"),
			Arguments.of("Multiplier with no numeral", "Million dollar baby.", "1000000 dollar baby."),
			Arguments.of("Final and", "She is twenty two and loves to dance.", "She is 22 and loves to dance."),
			Arguments.of("Single word separation", "four thousand nine years three hundred forty two days.", "4009 years 342 days."),
			Arguments.of("No final dot", "ninety nine", "99"),
			Arguments.of("Consecutive equal multiplier", "six hundred two hundred two.", "602 102."),
			Arguments.of("'a' as one", "A hundred years.", "100 years."),
			Arguments.of("'a' as determinant", "She was a seven years kid.", "She was a 7 years kid."),
			Arguments.of("Comma", "Forty four, seventy five", "44, 75"),
			Arguments.of("Valir previous smaller multipliers", "A hundred million yens.", "100000000 yens.")
			);
	}
	
	@ParameterizedTest()
	@MethodSource("orignialTestData")
	public void testWordsToNumbersGeneral(String input, String expectedResult) {
		Assertions.assertEquals(expectedResult, WordsToNumbersUtil.convertTextualNumbersInDocument(input));
	}
	
	@ParameterizedTest(name = "{0}")
	@MethodSource("specialCasesTestData")
	public void testWordsToNumbersSpecialCases(String testName, String input, String expectedResult) {
		Assertions.assertEquals(expectedResult, WordsToNumbersUtil.convertTextualNumbersInDocument(input));
	}
}
