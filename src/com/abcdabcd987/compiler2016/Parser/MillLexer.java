// Generated from Mill.g4 by ANTLR 4.5.2

package com.abcdabcd987.compiler2016.Parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MillLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, Bool=2, Int=3, String=4, Void=5, If=6, For=7, While=8, Break=9, 
		Continue=10, Return=11, New=12, Class=13, LParen=14, RParen=15, LBracket=16, 
		RBracket=17, LBrace=18, RBrace=19, Less=20, LessEqual=21, Greater=22, 
		GreaterEqual=23, LeftShift=24, RightShift=25, Plus=26, PlusPlus=27, Minus=28, 
		MinusMinus=29, Star=30, Div=31, Mod=32, And=33, Or=34, AndAnd=35, OrOr=36, 
		Caret=37, Not=38, Tilde=39, Question=40, Colon=41, Semi=42, Comma=43, 
		Assign=44, Equal=45, NotEqual=46, Dot=47, NullLiteral=48, BoolConstant=49, 
		IntegerConstant=50, CharacterConstant=51, StringLiteral=52, Identifier=53, 
		Whitespace=54, Newline=55, BlockComment=56, LineComment=57;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "Bool", "Int", "String", "Void", "Null", "True", "False", "If", 
		"For", "While", "Break", "Continue", "Return", "New", "Class", "LParen", 
		"RParen", "LBracket", "RBracket", "LBrace", "RBrace", "Less", "LessEqual", 
		"Greater", "GreaterEqual", "LeftShift", "RightShift", "Plus", "PlusPlus", 
		"Minus", "MinusMinus", "Star", "Div", "Mod", "And", "Or", "AndAnd", "OrOr", 
		"Caret", "Not", "Tilde", "Question", "Colon", "Semi", "Comma", "Assign", 
		"Equal", "NotEqual", "Dot", "NullLiteral", "BoolConstant", "IntegerConstant", 
		"NonzeroDigit", "CharacterConstant", "CCharSequence", "CChar", "EscapeSequence", 
		"SimpleEscapeSequence", "StringLiteral", "SCharSequence", "SChar", "Identifier", 
		"IdentifierNondigit", "Digit", "Whitespace", "Newline", "BlockComment", 
		"LineComment"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'else'", "'bool'", "'int'", "'string'", "'void'", "'if'", "'for'", 
		"'while'", "'break'", "'continue'", "'return'", "'new'", "'class'", "'('", 
		"')'", "'['", "']'", "'{'", "'}'", "'<'", "'<='", "'>'", "'>='", "'<<'", 
		"'>>'", "'+'", "'++'", "'-'", "'--'", "'*'", "'/'", "'%'", "'&'", "'|'", 
		"'&&'", "'||'", "'^'", "'!'", "'~'", "'?'", "':'", "';'", "','", "'='", 
		"'=='", "'!='", "'.'", "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "Bool", "Int", "String", "Void", "If", "For", "While", "Break", 
		"Continue", "Return", "New", "Class", "LParen", "RParen", "LBracket", 
		"RBracket", "LBrace", "RBrace", "Less", "LessEqual", "Greater", "GreaterEqual", 
		"LeftShift", "RightShift", "Plus", "PlusPlus", "Minus", "MinusMinus", 
		"Star", "Div", "Mod", "And", "Or", "AndAnd", "OrOr", "Caret", "Not", "Tilde", 
		"Question", "Colon", "Semi", "Comma", "Assign", "Equal", "NotEqual", "Dot", 
		"NullLiteral", "BoolConstant", "IntegerConstant", "CharacterConstant", 
		"StringLiteral", "Identifier", "Whitespace", "Newline", "BlockComment", 
		"LineComment"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public MillLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Mill.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2;\u01a2\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\3\2\3\2\3\2\3\2"+
		"\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3"+
		"\21\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3"+
		"\27\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\34\3"+
		"\35\3\35\3\35\3\36\3\36\3\37\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3#\3#\3"+
		"$\3$\3%\3%\3&\3&\3\'\3\'\3\'\3(\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3"+
		".\3.\3/\3/\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\64\3\64"+
		"\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\5\65\u0141"+
		"\n\65\3\66\3\66\7\66\u0145\n\66\f\66\16\66\u0148\13\66\3\66\5\66\u014b"+
		"\n\66\3\67\3\67\38\38\38\38\39\69\u0154\n9\r9\169\u0155\3:\3:\5:\u015a"+
		"\n:\3;\3;\3<\3<\3<\3=\3=\5=\u0163\n=\3=\3=\3>\6>\u0168\n>\r>\16>\u0169"+
		"\3?\3?\5?\u016e\n?\3@\3@\3@\7@\u0173\n@\f@\16@\u0176\13@\3A\3A\3B\3B\3"+
		"C\6C\u017d\nC\rC\16C\u017e\3C\3C\3D\5D\u0184\nD\3D\3D\3D\3D\3E\3E\3E\3"+
		"E\7E\u018e\nE\fE\16E\u0191\13E\3E\3E\3E\3E\3E\3F\3F\3F\3F\7F\u019c\nF"+
		"\fF\16F\u019f\13F\3F\3F\3\u018f\2G\3\3\5\4\7\5\t\6\13\7\r\2\17\2\21\2"+
		"\23\b\25\t\27\n\31\13\33\f\35\r\37\16!\17#\20%\21\'\22)\23+\24-\25/\26"+
		"\61\27\63\30\65\31\67\329\33;\34=\35?\36A\37C E!G\"I#K$M%O&Q\'S(U)W*Y"+
		"+[,]-_.a/c\60e\61g\62i\63k\64m\2o\65q\2s\2u\2w\2y\66{\2}\2\177\67\u0081"+
		"\2\u0083\2\u00858\u00879\u0089:\u008b;\3\2\n\3\2\63;\6\2\f\f\17\17))^"+
		"^\f\2$$))AA^^cdhhppttvvxx\6\2\f\f\17\17$$^^\5\2C\\aac|\3\2\62;\4\2\13"+
		"\13\"\"\4\2\f\f\17\17\u01a3\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3"+
		"\2\2\2\2\13\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2"+
		"\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2"+
		"\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2"+
		"\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3"+
		"\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2"+
		"\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2"+
		"W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3"+
		"\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2o\3\2\2\2\2y\3\2\2"+
		"\2\2\177\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b"+
		"\3\2\2\2\3\u008d\3\2\2\2\5\u0092\3\2\2\2\7\u0097\3\2\2\2\t\u009b\3\2\2"+
		"\2\13\u00a2\3\2\2\2\r\u00a7\3\2\2\2\17\u00ac\3\2\2\2\21\u00b1\3\2\2\2"+
		"\23\u00b7\3\2\2\2\25\u00ba\3\2\2\2\27\u00be\3\2\2\2\31\u00c4\3\2\2\2\33"+
		"\u00ca\3\2\2\2\35\u00d3\3\2\2\2\37\u00da\3\2\2\2!\u00de\3\2\2\2#\u00e4"+
		"\3\2\2\2%\u00e6\3\2\2\2\'\u00e8\3\2\2\2)\u00ea\3\2\2\2+\u00ec\3\2\2\2"+
		"-\u00ee\3\2\2\2/\u00f0\3\2\2\2\61\u00f2\3\2\2\2\63\u00f5\3\2\2\2\65\u00f7"+
		"\3\2\2\2\67\u00fa\3\2\2\29\u00fd\3\2\2\2;\u0100\3\2\2\2=\u0102\3\2\2\2"+
		"?\u0105\3\2\2\2A\u0107\3\2\2\2C\u010a\3\2\2\2E\u010c\3\2\2\2G\u010e\3"+
		"\2\2\2I\u0110\3\2\2\2K\u0112\3\2\2\2M\u0114\3\2\2\2O\u0117\3\2\2\2Q\u011a"+
		"\3\2\2\2S\u011c\3\2\2\2U\u011e\3\2\2\2W\u0120\3\2\2\2Y\u0122\3\2\2\2["+
		"\u0124\3\2\2\2]\u0126\3\2\2\2_\u0128\3\2\2\2a\u012a\3\2\2\2c\u012d\3\2"+
		"\2\2e\u0130\3\2\2\2g\u0132\3\2\2\2i\u0140\3\2\2\2k\u014a\3\2\2\2m\u014c"+
		"\3\2\2\2o\u014e\3\2\2\2q\u0153\3\2\2\2s\u0159\3\2\2\2u\u015b\3\2\2\2w"+
		"\u015d\3\2\2\2y\u0160\3\2\2\2{\u0167\3\2\2\2}\u016d\3\2\2\2\177\u016f"+
		"\3\2\2\2\u0081\u0177\3\2\2\2\u0083\u0179\3\2\2\2\u0085\u017c\3\2\2\2\u0087"+
		"\u0183\3\2\2\2\u0089\u0189\3\2\2\2\u008b\u0197\3\2\2\2\u008d\u008e\7g"+
		"\2\2\u008e\u008f\7n\2\2\u008f\u0090\7u\2\2\u0090\u0091\7g\2\2\u0091\4"+
		"\3\2\2\2\u0092\u0093\7d\2\2\u0093\u0094\7q\2\2\u0094\u0095\7q\2\2\u0095"+
		"\u0096\7n\2\2\u0096\6\3\2\2\2\u0097\u0098\7k\2\2\u0098\u0099\7p\2\2\u0099"+
		"\u009a\7v\2\2\u009a\b\3\2\2\2\u009b\u009c\7u\2\2\u009c\u009d\7v\2\2\u009d"+
		"\u009e\7t\2\2\u009e\u009f\7k\2\2\u009f\u00a0\7p\2\2\u00a0\u00a1\7i\2\2"+
		"\u00a1\n\3\2\2\2\u00a2\u00a3\7x\2\2\u00a3\u00a4\7q\2\2\u00a4\u00a5\7k"+
		"\2\2\u00a5\u00a6\7f\2\2\u00a6\f\3\2\2\2\u00a7\u00a8\7p\2\2\u00a8\u00a9"+
		"\7w\2\2\u00a9\u00aa\7n\2\2\u00aa\u00ab\7n\2\2\u00ab\16\3\2\2\2\u00ac\u00ad"+
		"\7v\2\2\u00ad\u00ae\7t\2\2\u00ae\u00af\7w\2\2\u00af\u00b0\7g\2\2\u00b0"+
		"\20\3\2\2\2\u00b1\u00b2\7h\2\2\u00b2\u00b3\7c\2\2\u00b3\u00b4\7n\2\2\u00b4"+
		"\u00b5\7u\2\2\u00b5\u00b6\7g\2\2\u00b6\22\3\2\2\2\u00b7\u00b8\7k\2\2\u00b8"+
		"\u00b9\7h\2\2\u00b9\24\3\2\2\2\u00ba\u00bb\7h\2\2\u00bb\u00bc\7q\2\2\u00bc"+
		"\u00bd\7t\2\2\u00bd\26\3\2\2\2\u00be\u00bf\7y\2\2\u00bf\u00c0\7j\2\2\u00c0"+
		"\u00c1\7k\2\2\u00c1\u00c2\7n\2\2\u00c2\u00c3\7g\2\2\u00c3\30\3\2\2\2\u00c4"+
		"\u00c5\7d\2\2\u00c5\u00c6\7t\2\2\u00c6\u00c7\7g\2\2\u00c7\u00c8\7c\2\2"+
		"\u00c8\u00c9\7m\2\2\u00c9\32\3\2\2\2\u00ca\u00cb\7e\2\2\u00cb\u00cc\7"+
		"q\2\2\u00cc\u00cd\7p\2\2\u00cd\u00ce\7v\2\2\u00ce\u00cf\7k\2\2\u00cf\u00d0"+
		"\7p\2\2\u00d0\u00d1\7w\2\2\u00d1\u00d2\7g\2\2\u00d2\34\3\2\2\2\u00d3\u00d4"+
		"\7t\2\2\u00d4\u00d5\7g\2\2\u00d5\u00d6\7v\2\2\u00d6\u00d7\7w\2\2\u00d7"+
		"\u00d8\7t\2\2\u00d8\u00d9\7p\2\2\u00d9\36\3\2\2\2\u00da\u00db\7p\2\2\u00db"+
		"\u00dc\7g\2\2\u00dc\u00dd\7y\2\2\u00dd \3\2\2\2\u00de\u00df\7e\2\2\u00df"+
		"\u00e0\7n\2\2\u00e0\u00e1\7c\2\2\u00e1\u00e2\7u\2\2\u00e2\u00e3\7u\2\2"+
		"\u00e3\"\3\2\2\2\u00e4\u00e5\7*\2\2\u00e5$\3\2\2\2\u00e6\u00e7\7+\2\2"+
		"\u00e7&\3\2\2\2\u00e8\u00e9\7]\2\2\u00e9(\3\2\2\2\u00ea\u00eb\7_\2\2\u00eb"+
		"*\3\2\2\2\u00ec\u00ed\7}\2\2\u00ed,\3\2\2\2\u00ee\u00ef\7\177\2\2\u00ef"+
		".\3\2\2\2\u00f0\u00f1\7>\2\2\u00f1\60\3\2\2\2\u00f2\u00f3\7>\2\2\u00f3"+
		"\u00f4\7?\2\2\u00f4\62\3\2\2\2\u00f5\u00f6\7@\2\2\u00f6\64\3\2\2\2\u00f7"+
		"\u00f8\7@\2\2\u00f8\u00f9\7?\2\2\u00f9\66\3\2\2\2\u00fa\u00fb\7>\2\2\u00fb"+
		"\u00fc\7>\2\2\u00fc8\3\2\2\2\u00fd\u00fe\7@\2\2\u00fe\u00ff\7@\2\2\u00ff"+
		":\3\2\2\2\u0100\u0101\7-\2\2\u0101<\3\2\2\2\u0102\u0103\7-\2\2\u0103\u0104"+
		"\7-\2\2\u0104>\3\2\2\2\u0105\u0106\7/\2\2\u0106@\3\2\2\2\u0107\u0108\7"+
		"/\2\2\u0108\u0109\7/\2\2\u0109B\3\2\2\2\u010a\u010b\7,\2\2\u010bD\3\2"+
		"\2\2\u010c\u010d\7\61\2\2\u010dF\3\2\2\2\u010e\u010f\7\'\2\2\u010fH\3"+
		"\2\2\2\u0110\u0111\7(\2\2\u0111J\3\2\2\2\u0112\u0113\7~\2\2\u0113L\3\2"+
		"\2\2\u0114\u0115\7(\2\2\u0115\u0116\7(\2\2\u0116N\3\2\2\2\u0117\u0118"+
		"\7~\2\2\u0118\u0119\7~\2\2\u0119P\3\2\2\2\u011a\u011b\7`\2\2\u011bR\3"+
		"\2\2\2\u011c\u011d\7#\2\2\u011dT\3\2\2\2\u011e\u011f\7\u0080\2\2\u011f"+
		"V\3\2\2\2\u0120\u0121\7A\2\2\u0121X\3\2\2\2\u0122\u0123\7<\2\2\u0123Z"+
		"\3\2\2\2\u0124\u0125\7=\2\2\u0125\\\3\2\2\2\u0126\u0127\7.\2\2\u0127^"+
		"\3\2\2\2\u0128\u0129\7?\2\2\u0129`\3\2\2\2\u012a\u012b\7?\2\2\u012b\u012c"+
		"\7?\2\2\u012cb\3\2\2\2\u012d\u012e\7#\2\2\u012e\u012f\7?\2\2\u012fd\3"+
		"\2\2\2\u0130\u0131\7\60\2\2\u0131f\3\2\2\2\u0132\u0133\7p\2\2\u0133\u0134"+
		"\7w\2\2\u0134\u0135\7n\2\2\u0135\u0136\7n\2\2\u0136h\3\2\2\2\u0137\u0138"+
		"\7v\2\2\u0138\u0139\7t\2\2\u0139\u013a\7w\2\2\u013a\u0141\7g\2\2\u013b"+
		"\u013c\7h\2\2\u013c\u013d\7c\2\2\u013d\u013e\7n\2\2\u013e\u013f\7u\2\2"+
		"\u013f\u0141\7g\2\2\u0140\u0137\3\2\2\2\u0140\u013b\3\2\2\2\u0141j\3\2"+
		"\2\2\u0142\u0146\5m\67\2\u0143\u0145\5\u0083B\2\u0144\u0143\3\2\2\2\u0145"+
		"\u0148\3\2\2\2\u0146\u0144\3\2\2\2\u0146\u0147\3\2\2\2\u0147\u014b\3\2"+
		"\2\2\u0148\u0146\3\2\2\2\u0149\u014b\7\62\2\2\u014a\u0142\3\2\2\2\u014a"+
		"\u0149\3\2\2\2\u014bl\3\2\2\2\u014c\u014d\t\2\2\2\u014dn\3\2\2\2\u014e"+
		"\u014f\7)\2\2\u014f\u0150\5q9\2\u0150\u0151\7)\2\2\u0151p\3\2\2\2\u0152"+
		"\u0154\5s:\2\u0153\u0152\3\2\2\2\u0154\u0155\3\2\2\2\u0155\u0153\3\2\2"+
		"\2\u0155\u0156\3\2\2\2\u0156r\3\2\2\2\u0157\u015a\n\3\2\2\u0158\u015a"+
		"\5u;\2\u0159\u0157\3\2\2\2\u0159\u0158\3\2\2\2\u015at\3\2\2\2\u015b\u015c"+
		"\5w<\2\u015cv\3\2\2\2\u015d\u015e\7^\2\2\u015e\u015f\t\4\2\2\u015fx\3"+
		"\2\2\2\u0160\u0162\7$\2\2\u0161\u0163\5{>\2\u0162\u0161\3\2\2\2\u0162"+
		"\u0163\3\2\2\2\u0163\u0164\3\2\2\2\u0164\u0165\7$\2\2\u0165z\3\2\2\2\u0166"+
		"\u0168\5}?\2\u0167\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u0167\3\2\2"+
		"\2\u0169\u016a\3\2\2\2\u016a|\3\2\2\2\u016b\u016e\n\5\2\2\u016c\u016e"+
		"\5u;\2\u016d\u016b\3\2\2\2\u016d\u016c\3\2\2\2\u016e~\3\2\2\2\u016f\u0174"+
		"\5\u0081A\2\u0170\u0173\5\u0081A\2\u0171\u0173\5\u0083B\2\u0172\u0170"+
		"\3\2\2\2\u0172\u0171\3\2\2\2\u0173\u0176\3\2\2\2\u0174\u0172\3\2\2\2\u0174"+
		"\u0175\3\2\2\2\u0175\u0080\3\2\2\2\u0176\u0174\3\2\2\2\u0177\u0178\t\6"+
		"\2\2\u0178\u0082\3\2\2\2\u0179\u017a\t\7\2\2\u017a\u0084\3\2\2\2\u017b"+
		"\u017d\t\b\2\2\u017c\u017b\3\2\2\2\u017d\u017e\3\2\2\2\u017e\u017c\3\2"+
		"\2\2\u017e\u017f\3\2\2\2\u017f\u0180\3\2\2\2\u0180\u0181\bC\2\2\u0181"+
		"\u0086\3\2\2\2\u0182\u0184\7\17\2\2\u0183\u0182\3\2\2\2\u0183\u0184\3"+
		"\2\2\2\u0184\u0185\3\2\2\2\u0185\u0186\7\f\2\2\u0186\u0187\3\2\2\2\u0187"+
		"\u0188\bD\2\2\u0188\u0088\3\2\2\2\u0189\u018a\7\61\2\2\u018a\u018b\7,"+
		"\2\2\u018b\u018f\3\2\2\2\u018c\u018e\13\2\2\2\u018d\u018c\3\2\2\2\u018e"+
		"\u0191\3\2\2\2\u018f\u0190\3\2\2\2\u018f\u018d\3\2\2\2\u0190\u0192\3\2"+
		"\2\2\u0191\u018f\3\2\2\2\u0192\u0193\7,\2\2\u0193\u0194\7\61\2\2\u0194"+
		"\u0195\3\2\2\2\u0195\u0196\bE\2\2\u0196\u008a\3\2\2\2\u0197\u0198\7\61"+
		"\2\2\u0198\u0199\7\61\2\2\u0199\u019d\3\2\2\2\u019a\u019c\n\t\2\2\u019b"+
		"\u019a\3\2\2\2\u019c\u019f\3\2\2\2\u019d\u019b\3\2\2\2\u019d\u019e\3\2"+
		"\2\2\u019e\u01a0\3\2\2\2\u019f\u019d\3\2\2\2\u01a0\u01a1\bF\2\2\u01a1"+
		"\u008c\3\2\2\2\21\2\u0140\u0146\u014a\u0155\u0159\u0162\u0169\u016d\u0172"+
		"\u0174\u017e\u0183\u018f\u019d\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}