/**
 *
 * An implementation of ExpressionParser
 *
 * Starter code to implement an ExpressionParser. Your parser methods should use
 * the following grammar: E := A | X A := A+M | M M := M*M | X X := (E) | L L :=
 * [0-9]+ | [a-z]
 */
public class SimpleExpressionParser implements ExpressionParser {
	/**
	 * Attempts to create an expression tree -- flattened as much as possible --
	 * from the specified String. Throws a ExpressionParseException if the specified
	 * string cannot be parsed.
	 *
	 * @param str
	 *            the string to parse into an expression tree
	 *
	 * @param withJavaFXControls
	 *            you can just ignore this variable for R1
	 *
	 * @return the Expression object representing the parsed expression tree
	 */
	public Expression parse(String str, boolean withJavaFXControls) throws ExpressionParseException {
		// Remove spaces -- this simplifies the parsing logic
		str = str.replaceAll(" ", "");
		Expression expression = parseExpression(str);
		if (expression == null) {
			// If we couldn't parse the string, then raise an error
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Flatten the expression before returning
		expression.flatten();
		return expression;
	}

	/**
	 * Parses the string
	 * @param str the string to be parsed
	 * @return the parsed expression
	 */
	private Expression parseExpression(String str) {
		return parseE(str);
	}

	/**
	 * Parses the E string using the rule E -> A | X
	 * @param str the E string to be parsed
	 * @return the expression if it can be parsed, null otherwise
	 */
	private Expression parseE(String str) {
		if (parseA(str) != null) { // if it can be parsed as an A
			return parseA(str);
		} else if (parseX(str) != null) { // if it can be parsed as an X
			return parseX(str);
		}
		return null;
	}

	/**
	 * Parses the A string using the rule A -> A + M | M
	 * @param str the A string to be parsed
	 * @return the expression if it can be parsed, null otherwise
	 */
	private Expression parseA(String str) {
		// try A + M
		int idxOfPlus = str.indexOf('+');
		while (idxOfPlus > 0) { // try each +
			if (parseA(str.substring(0, idxOfPlus)) != null && parseM(str.substring(idxOfPlus + 1)) != null) {
				final Expression result = new SimpleCompoundExpression("+");
				((AbstractCompoundExpression) result).addSubexpression(parseA(str.substring(0, idxOfPlus)));
				((AbstractCompoundExpression) result).addSubexpression(parseM(str.substring(idxOfPlus + 1)));
				return result;
			}
			idxOfPlus = str.indexOf('+', idxOfPlus + 1);
		}
		// try M
		if (parseM(str) != null) {
			return parseM(str);
		}
		return null;
	}

	/**
	 * Parses the M string using the rule M -> M * M | X
	 * @param str the M string to be parsed
	 * @return the parsed expression if it can be parsed, null otherwise
	 */
	private Expression parseM(String str) {
		// try M * M
		int idxOfTimes = str.indexOf('*');
		while (idxOfTimes > 0) { // try each *
			if (parseM(str.substring(0, idxOfTimes)) != null && parseM(str.substring(idxOfTimes + 1)) != null) {
				final Expression result = new SimpleCompoundExpression("*");
				((AbstractCompoundExpression) result).addSubexpression(parseM(str.substring(0, idxOfTimes)));
				((AbstractCompoundExpression) result).addSubexpression(parseM(str.substring(idxOfTimes + 1)));
				return result;
			}
			idxOfTimes = str.indexOf('*', idxOfTimes + 1);
		}
		// try X
		if (parseX(str) != null) {
			return parseX(str);
		}
		return null;
	}

	/**
	 * Parses the X string using the rule X -> (E) | L
	 * @param str the X string to be parsed
	 * @return the parsed expression if it can be parsed, null otherwise
	 */
	private Expression parseX(String str) {
		// try (E)
		if (str.startsWith("(") && str.endsWith(")") && parseE(str.substring(1, str.length() - 1)) != null) {
			final Expression result = new ParentheticalExpression();
			((AbstractCompoundExpression) result).addSubexpression(parseE(str.substring(1, str.length() - 1)));
			return result;
		}
		// try L
		if (parseL(str) != null) {
			return parseL(str);
		}
		return null;
	}

	/**
	 * Parses the L string using the rule L -> [0-9]+ | [a-z]
	 * @param str the L string to be parsed
	 * @return the parsed expression if it can be parsed, null otherwise
	 */
	private Expression parseL(String str) {
		// try [0-9]+
		try {
			Integer.parseInt(str);
			return new LiteralExpression(str);
		} catch (Exception e) {
		}
		// try [a-z]
		if (str.length() == 1 && str.equals(str.toLowerCase())) {
			return new LiteralExpression(str);
		}
		return null;
	}
}
