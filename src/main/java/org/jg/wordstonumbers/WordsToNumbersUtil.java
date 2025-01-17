package org.jg.wordstonumbers;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Testing algorithm to convert words in paragraph representing numbers into
 * standard digit representation
 * 
 * Note that currently all formatting and occurrences of "and" are getting
 * stripped out. Not needed for current purposes
 * 
 * e.g. Input: "There were twenty five coins" Output: "there were 25 coins"
 *
 */
public class WordsToNumbersUtil {
	
	final static List<String> units = Arrays.asList("zero", "one", "two", "three", "four", "five",
			"six", "seven", "eight", "nine");
	
	final static List<String> teens = Arrays.asList("eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
			"seventeen", "eighteen", "nineteen");
	
	final static List<String> tens = Arrays.asList("ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty",
			"ninety");
	
	final static List<String> multipliersInOrder = Arrays.asList("hundred", "thousand", "million", "billion", "trillion");
	
	final static List<String> allowedStrings = Arrays.asList("a", "and", "zero", "one", "two", "three", "four", "five",
			"six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
			"seventeen", "eighteen", "nineteen", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty",
			"ninety", "hundred", "thousand", "million", "billion", "trillion");

	public static void main(String[] args) {

		String sentence = "6.12 patients (seventy two knees) were excluded: three patients (three knees) had peri-prosthetic infection, one patient (two knees) died in circumstances unrelated to surgery, one patient (one knee) had a cerebrovascular accident and one patient (one knee) had severe spinal problems. A further seven patients (ten knees) were excluded as they did not attend the clinic. Consequently, 333 knees (207 patients) which were followed up beyond 12 months were included. A total of 81 TKRs were unilateral and 126 were bilateral, with an interval of between one and three weeks (typically two) between operations. There were 196 women and 11 men with a mean age of 68.7 years (54 to 87). The mean height and weight were 151.6 cm (138 to 174) and 60.4 kg (36 to 83). The mean body mass index (BMI) was 26.2 kg/m2 (18.0 to 37.0). All patients gave informed consent to participate in the study. Pre-operative information was gathered three days before surgery by an independent investigator (YGK), who recorded the range of movement (ROM), flexion contracture and the maximum flexion, and collected data from questionnaires. The pre-operative evaluation included the demography, diagnosis, medical history and physical examination, including the ROM, and collateral and antero-posterior stress tests. The passive, non-weight-bearing ROM was measured to the nearest 5� by a goniometer, with the patient supine. Patients were also evaluated by the knee and function scores of the American Knee Society (AKS),1 Western Ontario McMaster Universities Osteoarthritis Index (WOMAC)26 and the Short Form-36 (SF-36).27 All operations were performed by a single surgeon (TKK). A total of 172 knees were implanted with E-motion (FP type, Aesculap, Tuttlingen, Germany) and 161 with Genesis II (CR type in 112 knees and PS type in 49; Smith & Nephew, Memphis, Tennessee) arthroplasties. All operations used a medial parapatellar arthrotomy, and for 101 TKRs with the E-motion, computer-assisted navigation was used (Orthopilot 4.0, Aescalup, Germany). The other 71 TKRs with the E-motion and 161 with Genesis II, used conventional intramedullary guides for the femur and extramedullary guides for the tibia. Patellar resurfacing was routine and all implants were fixed with cement. The patients remained in hospital for two weeks, undergoing a standard rehabilitation programme, commencing on the first post-operative day with continuous passive movement. On the third day, they attended the rehabilitation department for training with crutches or walking aids. Between the fourth and the 14th days they had daily physiotherapy. Progression to maximum flexion was recorded with the goal of achieving 110� of flexion without a flexion contracture by discharge. Post-operative assessment was carried out by the same investigator (YGK) at three, six and 12 months, and yearly thereafter. Evaluations included ROM, AKS, WOMAC and SF-36 scores. In bilateral cases, AKS and WOMAC scores were measured separately for each knee...";
		String words = convertTextualNumbersInDocument(sentence);
		System.out.println("Paragraph before: " + sentence);
		System.out.println("Paragraph after: " + words);
	}

	/**
	 * Main driver method. Converts textual numbers (e.g. twenty five) to
	 * integers (e.g. 25)
	 * 
	 * Does not currently cater for decimal points. e.g. "five point two"
	 * 
	 * @param inputText
	 */
	public static String convertTextualNumbersInDocument(String inputText) {

		// splits text into words and deals with hyphenated numbers. Use linked
		// list due to manipulation during processing
		List<String> words = new LinkedList<String>(cleanAndTokenizeText(inputText));

		// replace all the textual numbers
		words = replaceTextualNumbers(words);

		// put spaces back in and return the string. Should be the same as input
		// text except from textual numbers
		return wordListToString(words);
	}
	
	private static String cleanAndLowecaseWord(String word) {
		return word.replaceAll("[^a-zA-Z\\s]", "").replaceAll("(\\t|\\r\\n|\\r|\\n)", "").toLowerCase();
	}
	
	private static boolean reasonableNumber(String word, List<String> processingList) {
		if (processingList.size() == 0) {
			if (multipliersInOrder.contains(word)) {
				processingList.add("one");
			}
			return !word.equals("and");
		}
		
		String previousWord = processingList.get(processingList.size() - 1);
		String previousStripped = cleanAndLowecaseWord(previousWord);
		
		if (units.contains(word) || teens.contains(word) || tens.contains(word)) {
			if(units.contains(previousStripped) || teens.contains(previousStripped)) {
				return false;
			} else if (tens.contains(previousStripped)) {
				if (units.contains(word)) {
					return true;
				} else {
					return false;
				}
			} else if (previousStripped.equals("a")) {
				return false;
			} else if (previousStripped.equals("and")) {
				return reasonableNumber(word, processingList.subList(0, processingList.size() -1));
			} else {
				return true;
			}
		} else if(multipliersInOrder.contains(word)) {
			if(multipliersInOrder.contains(previousStripped)) {
				//check the previous multiplier, if bigger return true
				int currentIndex = multipliersInOrder.indexOf(word);
				int previousIndex = multipliersInOrder.indexOf(previousStripped);
				if (previousIndex != currentIndex) {
					return true;
				} else {
					return false;
				}
			/*} else if ((teens.contains(previousWord) || tens.contains(previousWord)) && previousWord.equals("hundred")) {
				//Weird case. Allowed for now. E.g: twenty-five hundred = 2500*/
			} else if (previousStripped.equals("and")) {
				return false;
			} else if (previousStripped.equals("a")) {
				return true;
			} else {
				
				int i = processingList.size()-1;
				String previousMultiplier = null;
				while (previousMultiplier == null && i >= 0) {
					String iWord = processingList.get(i);
					String iClean = cleanAndLowecaseWord(iWord);
					if (multipliersInOrder.contains(iClean)) {
						previousMultiplier = iClean;
					}
					i--;
				}
				if (previousMultiplier != null) {
					//check the previous multiplier, if bigger return true
					int currentIndex = multipliersInOrder.indexOf(word);
					int previousIndex = multipliersInOrder.indexOf(previousMultiplier);
					if (previousIndex != currentIndex) {
						return true;
					} else {
						return false;
					}
				} else {
					//if non existent return true
					return true;
				}
			}
		} else if(word.equals("and")) {
			return true;
		} else if(word.equals("a")) {
			return false;
		}
		return false;
	}
	
	/**
	 * Does the replacement of textual numbers, processing each word at a time
	 * and grouping them before doing the conversion
	 * 
	 * @param words
	 * @return
	 */
	private static List<String> replaceTextualNumbers(List<String> words) {

		// holds each group of textual numbers being processed together. e.g.
		// "one" or "five hundred and two"
		List<String> processingList = new LinkedList<String>();

		boolean finalDot = false;
		int i = 0;
		while (i < words.size() || !processingList.isEmpty()) {

			// caters for sentences only containing one word (that is a number)
			String word = "";
			if (i < words.size()) {
				word = words.get(i);
			}

			// strip word of all punctuation to make it easier to process
			String wordStripped = cleanAndLowecaseWord(word);

			// 2nd condition: skip "and" words by themselves and at start of num
			if (!finalDot && allowedStrings.contains(wordStripped) && reasonableNumber(wordStripped, processingList)) {
				words.remove(i); // remove from main list, will process later
				processingList.add(word);
				
				finalDot = hasFinalDot(word);
			} else if (processingList.size() > 0) {
				// found end of group of textual words to process

				//if "and" is the last word, add it back in to original list
				String lastProcessedWord = processingList.get(processingList.size() - 1);
				if (lastProcessedWord.equals("and") || lastProcessedWord.equals("a")) {
					words.add(i, lastProcessedWord);
					processingList.remove(processingList.size() - 1);
				}

				if (processingList.size() > 0) {
					// main logic here, does the actual conversion
					String wordAsDigits = String.valueOf(convertWordsToNum(processingList));
					
					wordAsDigits = retainPunctuation(processingList, wordAsDigits);
					words.add(i, String.valueOf(wordAsDigits));
	
					processingList.clear();
				}
				
				finalDot = false;
				
				i++;
			} else {
				i++;
			}
		}

		return words;
	}

	private static boolean hasFinalDot(String word) {
		char lastChar = word.trim().charAt(word.trim().length() - 1);
		return lastChar == '.';
	}

	/**
	 * Retain punctuation at the start and end of a textual number.
	 * 
	 * e.g. (seventy two) -> (72)
	 * 
	 * @param processingList
	 * @param wordAsDigits
	 * @return
	 */
	private static String retainPunctuation(List<String> processingList, String wordAsDigits) {

		String reTrim = "(^[\\s]+)|([\\s]+$)";

		// Create a Pattern object
		Pattern r = Pattern.compile(reTrim);

		String firstWord = processingList.get(0);
		String lastWord = processingList.get(processingList.size() - 1);
		
		String initialTrim = "";
		String finalTrim = "";
		
		Matcher m = r.matcher(firstWord);
		if (m.find()) {
			initialTrim = m.group(1) == null ? "" : m.group(1);
		} 
		
		m = r.matcher(lastWord);
		if (m.find()) {
			finalTrim = m.group(2) == null ? "" : m.group(2);
		} 
		
		char lastChar = lastWord.trim().charAt(lastWord.trim().length() - 1);
		if (!Character.isLetter(lastChar)) {
			wordAsDigits += lastChar;
		}

		char firstChar = firstWord.trim().charAt(0);
		if (!Character.isLetter(firstChar)) {
			wordAsDigits = firstChar + wordAsDigits;
		}

		return initialTrim + wordAsDigits + finalTrim;
	}

	/**
	 * Splits up hyphenated textual words. e.g. twenty-two -> twenty two
	 * 
	 * @param sentence
	 * @return
	 */
	private static List<String> cleanAndTokenizeText(String sentence) {
		//List<String> words = new LinkedList<String>(Arrays.asList(sentence.split(" +")));
		List<String> words = new LinkedList<String>(Arrays.asList(sentence.split(" ")));

		// remove hyphenated textual numbers
		for (int i = 0; i < words.size(); i++) {
			String str = words.get(i);
			if (str.contains("-") || str.contains("\n") || str.contains("\t")) {
				List<String> splitWords = Arrays.asList(str.split("-"));
			/*if (str.contains("-") || str.contains("\n") || str.contains("\t")) {
				List<String> splitWords = Arrays.asList(str.split("-|\\s"));*/
				// just check the first word is a textual number. Caters for
				// "twenty-five," without having to strip the comma
				if (splitWords.size() > 1 && allowedStrings.contains(splitWords.get(0))) {
					words.remove(i);
					words.addAll(i, splitWords);
				}
			}

		}

		return words;
	}

	/**
	 * Creates string including spaces from a list of words
	 * 
	 * @param list
	 * @return
	 */
	private static String wordListToString(List<String> list) {
		StringBuilder result = new StringBuilder("");
		for (int i = 0; i < list.size(); i++) {
			String str = list.get(i);
			if (i == 0 && str != null) {
				result.append(list.get(i));
			} else if (str != null) {
				result.append(" " + list.get(i));
			}
		}

		return result.toString();
	}

	/**
	 * Logic for taking a textual number string and converting it into a number
	 * e.g. twenty five -> 25
	 * 
	 * This relies on there only being one textual number being processed. Steps
	 * prior to this deal with breaking a paragraph down into individual textual
	 * numbers, which could consist of a number of words.
	 * 
	 * @param input
	 * @return
	 */
	private static long convertWordsToNum(List<String> words) {
		long finalResult = 0;
		long intermediateResult = 0;
		for (String str : words) {
			// clean up string for easier processing
			str = cleanAndLowecaseWord(str);
			if (str.equalsIgnoreCase("zero")) {
				intermediateResult += 0;
			} else if (str.equalsIgnoreCase("one")) {
				intermediateResult += 1;
			} else if (str.equalsIgnoreCase("two")) {
				intermediateResult += 2;
			} else if (str.equalsIgnoreCase("three")) {
				intermediateResult += 3;
			} else if (str.equalsIgnoreCase("four")) {
				intermediateResult += 4;
			} else if (str.equalsIgnoreCase("five")) {
				intermediateResult += 5;
			} else if (str.equalsIgnoreCase("six")) {
				intermediateResult += 6;
			} else if (str.equalsIgnoreCase("seven")) {
				intermediateResult += 7;
			} else if (str.equalsIgnoreCase("eight")) {
				intermediateResult += 8;
			} else if (str.equalsIgnoreCase("nine")) {
				intermediateResult += 9;
			} else if (str.equalsIgnoreCase("ten")) {
				intermediateResult += 10;
			} else if (str.equalsIgnoreCase("eleven")) {
				intermediateResult += 11;
			} else if (str.equalsIgnoreCase("twelve")) {
				intermediateResult += 12;
			} else if (str.equalsIgnoreCase("thirteen")) {
				intermediateResult += 13;
			} else if (str.equalsIgnoreCase("fourteen")) {
				intermediateResult += 14;
			} else if (str.equalsIgnoreCase("fifteen")) {
				intermediateResult += 15;
			} else if (str.equalsIgnoreCase("sixteen")) {
				intermediateResult += 16;
			} else if (str.equalsIgnoreCase("seventeen")) {
				intermediateResult += 17;
			} else if (str.equalsIgnoreCase("eighteen")) {
				intermediateResult += 18;
			} else if (str.equalsIgnoreCase("nineteen")) {
				intermediateResult += 19;
			} else if (str.equalsIgnoreCase("twenty")) {
				intermediateResult += 20;
			} else if (str.equalsIgnoreCase("thirty")) {
				intermediateResult += 30;
			} else if (str.equalsIgnoreCase("forty")) {
				intermediateResult += 40;
			} else if (str.equalsIgnoreCase("fifty")) {
				intermediateResult += 50;
			} else if (str.equalsIgnoreCase("sixty")) {
				intermediateResult += 60;
			} else if (str.equalsIgnoreCase("seventy")) {
				intermediateResult += 70;
			} else if (str.equalsIgnoreCase("eighty")) {
				intermediateResult += 80;
			} else if (str.equalsIgnoreCase("ninety")) {
				intermediateResult += 90;
			} else if (str.equalsIgnoreCase("hundred")) {
				if (intermediateResult == 0) {
					intermediateResult = 1;
				}
				intermediateResult *= 100;
			} else if (str.equalsIgnoreCase("thousand")) {
				if (intermediateResult == 0) {
					intermediateResult = 1;
				}
				intermediateResult *= 1000;
				finalResult += intermediateResult;
				intermediateResult = 0;
			} else if (str.equalsIgnoreCase("million")) {
				if (intermediateResult == 0) {
					intermediateResult = 1;
				}
				intermediateResult *= 1000000;
				finalResult += intermediateResult;
				intermediateResult = 0;
			} else if (str.equalsIgnoreCase("billion")) {
				if (intermediateResult == 0) {
					intermediateResult = 1;
				}
				intermediateResult *= 1000000000;
				finalResult += intermediateResult;
				intermediateResult = 0;
			} else if (str.equalsIgnoreCase("trillion")) {
				if (intermediateResult == 0) {
					intermediateResult = 1;
				}
				intermediateResult *= 1000000000000L;
				finalResult += intermediateResult;
				intermediateResult = 0;
			}
		}

		finalResult += intermediateResult;
		intermediateResult = 0;
		return finalResult;
	}

}
