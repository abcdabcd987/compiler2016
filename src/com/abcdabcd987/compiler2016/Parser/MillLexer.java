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
		Assign=44, Equal=45, NotEqual=46, Dot=47, Constant=48, NullLiteral=49, 
		BoolConstant=50, IntegerConstant=51, CharacterConstant=52, StringLiteral=53, 
		Identifier=54, Whitespace=55, Newline=56, BlockComment=57, LineComment=58;
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
		"Equal", "NotEqual", "Dot", "Constant", "NullLiteral", "BoolConstant", 
		"IntegerConstant", "NonzeroDigit", "CharacterConstant", "CCharSequence", 
		"CChar", "EscapeSequence", "SimpleEscapeSequence", "StringLiteral", "SCharSequence", 
		"SChar", "Identifier", "IdentifierNondigit", "Digit", "Whitespace", "Newline", 
		"BlockComment", "LineComment"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'else'", "'bool'", "'int'", "'string'", "'void'", "'if'", "'for'", 
		"'while'", "'break'", "'continue'", "'return'", "'new'", "'class'", "'('", 
		"')'", "'['", "']'", "'{'", "'}'", "'<'", "'<='", "'>'", "'>='", "'<<'", 
		"'>>'", "'+'", "'++'", "'-'", "'--'", "'*'", "'/'", "'%'", "'&'", "'|'", 
		"'&&'", "'||'", "'^'", "'!'", "'~'", "'?'", "':'", "';'", "','", "'='", 
		"'=='", "'!='", "'.'", null, "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "Bool", "Int", "String", "Void", "If", "For", "While", "Break", 
		"Continue", "Return", "New", "Class", "LParen", "RParen", "LBracket", 
		"RBracket", "LBrace", "RBrace", "Less", "LessEqual", "Greater", "GreaterEqual", 
		"LeftShift", "RightShift", "Plus", "PlusPlus", "Minus", "MinusMinus", 
		"Star", "Div", "Mod", "And", "Or", "AndAnd", "OrOr", "Caret", "Not", "Tilde", 
		"Question", "Colon", "Semi", "Comma", "Assign", "Equal", "NotEqual", "Dot", 
		"Constant", "NullLiteral", "BoolConstant", "IntegerConstant", "CharacterConstant", 
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2<\u01ab\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\3\2\3\2\3"+
		"\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3"+
		"\21\3\21\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3"+
		"\27\3\27\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3"+
		"\34\3\35\3\35\3\35\3\36\3\36\3\37\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3#"+
		"\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3\'\3(\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-"+
		"\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\64"+
		"\3\64\3\64\3\64\3\64\5\64\u013a\n\64\3\65\3\65\3\65\3\65\3\65\3\66\3\66"+
		"\3\66\3\66\3\66\3\66\3\66\3\66\3\66\5\66\u014a\n\66\3\67\3\67\7\67\u014e"+
		"\n\67\f\67\16\67\u0151\13\67\3\67\5\67\u0154\n\67\38\38\39\39\39\39\3"+
		":\6:\u015d\n:\r:\16:\u015e\3;\3;\5;\u0163\n;\3<\3<\3=\3=\3=\3>\3>\5>\u016c"+
		"\n>\3>\3>\3?\6?\u0171\n?\r?\16?\u0172\3@\3@\5@\u0177\n@\3A\3A\3A\7A\u017c"+
		"\nA\fA\16A\u017f\13A\3B\3B\3C\3C\3D\6D\u0186\nD\rD\16D\u0187\3D\3D\3E"+
		"\5E\u018d\nE\3E\3E\3E\3E\3F\3F\3F\3F\7F\u0197\nF\fF\16F\u019a\13F\3F\3"+
		"F\3F\3F\3F\3G\3G\3G\3G\7G\u01a5\nG\fG\16G\u01a8\13G\3G\3G\3\u0198\2H\3"+
		"\3\5\4\7\5\t\6\13\7\r\2\17\2\21\2\23\b\25\t\27\n\31\13\33\f\35\r\37\16"+
		"!\17#\20%\21\'\22)\23+\24-\25/\26\61\27\63\30\65\31\67\329\33;\34=\35"+
		"?\36A\37C E!G\"I#K$M%O&Q\'S(U)W*Y+[,]-_.a/c\60e\61g\62i\63k\64m\65o\2"+
		"q\66s\2u\2w\2y\2{\67}\2\177\2\u00818\u0083\2\u0085\2\u00879\u0089:\u008b"+
		";\u008d<\3\2\n\3\2\63;\6\2\f\f\17\17))^^\f\2$$))AA^^cdhhppttvvxx\6\2\f"+
		"\f\17\17$$^^\5\2C\\aac|\3\2\62;\4\2\13\13\"\"\4\2\f\f\17\17\u01b0\2\3"+
		"\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\23\3\2\2\2"+
		"\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37"+
		"\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3"+
		"\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2"+
		"\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C"+
		"\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2"+
		"\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2"+
		"\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i"+
		"\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2q\3\2\2\2\2{\3\2\2\2\2\u0081\3\2\2\2\2"+
		"\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2\2\3\u008f"+
		"\3\2\2\2\5\u0094\3\2\2\2\7\u0099\3\2\2\2\t\u009d\3\2\2\2\13\u00a4\3\2"+
		"\2\2\r\u00a9\3\2\2\2\17\u00ae\3\2\2\2\21\u00b3\3\2\2\2\23\u00b9\3\2\2"+
		"\2\25\u00bc\3\2\2\2\27\u00c0\3\2\2\2\31\u00c6\3\2\2\2\33\u00cc\3\2\2\2"+
		"\35\u00d5\3\2\2\2\37\u00dc\3\2\2\2!\u00e0\3\2\2\2#\u00e6\3\2\2\2%\u00e8"+
		"\3\2\2\2\'\u00ea\3\2\2\2)\u00ec\3\2\2\2+\u00ee\3\2\2\2-\u00f0\3\2\2\2"+
		"/\u00f2\3\2\2\2\61\u00f4\3\2\2\2\63\u00f7\3\2\2\2\65\u00f9\3\2\2\2\67"+
		"\u00fc\3\2\2\29\u00ff\3\2\2\2;\u0102\3\2\2\2=\u0104\3\2\2\2?\u0107\3\2"+
		"\2\2A\u0109\3\2\2\2C\u010c\3\2\2\2E\u010e\3\2\2\2G\u0110\3\2\2\2I\u0112"+
		"\3\2\2\2K\u0114\3\2\2\2M\u0116\3\2\2\2O\u0119\3\2\2\2Q\u011c\3\2\2\2S"+
		"\u011e\3\2\2\2U\u0120\3\2\2\2W\u0122\3\2\2\2Y\u0124\3\2\2\2[\u0126\3\2"+
		"\2\2]\u0128\3\2\2\2_\u012a\3\2\2\2a\u012c\3\2\2\2c\u012f\3\2\2\2e\u0132"+
		"\3\2\2\2g\u0139\3\2\2\2i\u013b\3\2\2\2k\u0149\3\2\2\2m\u0153\3\2\2\2o"+
		"\u0155\3\2\2\2q\u0157\3\2\2\2s\u015c\3\2\2\2u\u0162\3\2\2\2w\u0164\3\2"+
		"\2\2y\u0166\3\2\2\2{\u0169\3\2\2\2}\u0170\3\2\2\2\177\u0176\3\2\2\2\u0081"+
		"\u0178\3\2\2\2\u0083\u0180\3\2\2\2\u0085\u0182\3\2\2\2\u0087\u0185\3\2"+
		"\2\2\u0089\u018c\3\2\2\2\u008b\u0192\3\2\2\2\u008d\u01a0\3\2\2\2\u008f"+
		"\u0090\7g\2\2\u0090\u0091\7n\2\2\u0091\u0092\7u\2\2\u0092\u0093\7g\2\2"+
		"\u0093\4\3\2\2\2\u0094\u0095\7d\2\2\u0095\u0096\7q\2\2\u0096\u0097\7q"+
		"\2\2\u0097\u0098\7n\2\2\u0098\6\3\2\2\2\u0099\u009a\7k\2\2\u009a\u009b"+
		"\7p\2\2\u009b\u009c\7v\2\2\u009c\b\3\2\2\2\u009d\u009e\7u\2\2\u009e\u009f"+
		"\7v\2\2\u009f\u00a0\7t\2\2\u00a0\u00a1\7k\2\2\u00a1\u00a2\7p\2\2\u00a2"+
		"\u00a3\7i\2\2\u00a3\n\3\2\2\2\u00a4\u00a5\7x\2\2\u00a5\u00a6\7q\2\2\u00a6"+
		"\u00a7\7k\2\2\u00a7\u00a8\7f\2\2\u00a8\f\3\2\2\2\u00a9\u00aa\7p\2\2\u00aa"+
		"\u00ab\7w\2\2\u00ab\u00ac\7n\2\2\u00ac\u00ad\7n\2\2\u00ad\16\3\2\2\2\u00ae"+
		"\u00af\7v\2\2\u00af\u00b0\7t\2\2\u00b0\u00b1\7w\2\2\u00b1\u00b2\7g\2\2"+
		"\u00b2\20\3\2\2\2\u00b3\u00b4\7h\2\2\u00b4\u00b5\7c\2\2\u00b5\u00b6\7"+
		"n\2\2\u00b6\u00b7\7u\2\2\u00b7\u00b8\7g\2\2\u00b8\22\3\2\2\2\u00b9\u00ba"+
		"\7k\2\2\u00ba\u00bb\7h\2\2\u00bb\24\3\2\2\2\u00bc\u00bd\7h\2\2\u00bd\u00be"+
		"\7q\2\2\u00be\u00bf\7t\2\2\u00bf\26\3\2\2\2\u00c0\u00c1\7y\2\2\u00c1\u00c2"+
		"\7j\2\2\u00c2\u00c3\7k\2\2\u00c3\u00c4\7n\2\2\u00c4\u00c5\7g\2\2\u00c5"+
		"\30\3\2\2\2\u00c6\u00c7\7d\2\2\u00c7\u00c8\7t\2\2\u00c8\u00c9\7g\2\2\u00c9"+
		"\u00ca\7c\2\2\u00ca\u00cb\7m\2\2\u00cb\32\3\2\2\2\u00cc\u00cd\7e\2\2\u00cd"+
		"\u00ce\7q\2\2\u00ce\u00cf\7p\2\2\u00cf\u00d0\7v\2\2\u00d0\u00d1\7k\2\2"+
		"\u00d1\u00d2\7p\2\2\u00d2\u00d3\7w\2\2\u00d3\u00d4\7g\2\2\u00d4\34\3\2"+
		"\2\2\u00d5\u00d6\7t\2\2\u00d6\u00d7\7g\2\2\u00d7\u00d8\7v\2\2\u00d8\u00d9"+
		"\7w\2\2\u00d9\u00da\7t\2\2\u00da\u00db\7p\2\2\u00db\36\3\2\2\2\u00dc\u00dd"+
		"\7p\2\2\u00dd\u00de\7g\2\2\u00de\u00df\7y\2\2\u00df \3\2\2\2\u00e0\u00e1"+
		"\7e\2\2\u00e1\u00e2\7n\2\2\u00e2\u00e3\7c\2\2\u00e3\u00e4\7u\2\2\u00e4"+
		"\u00e5\7u\2\2\u00e5\"\3\2\2\2\u00e6\u00e7\7*\2\2\u00e7$\3\2\2\2\u00e8"+
		"\u00e9\7+\2\2\u00e9&\3\2\2\2\u00ea\u00eb\7]\2\2\u00eb(\3\2\2\2\u00ec\u00ed"+
		"\7_\2\2\u00ed*\3\2\2\2\u00ee\u00ef\7}\2\2\u00ef,\3\2\2\2\u00f0\u00f1\7"+
		"\177\2\2\u00f1.\3\2\2\2\u00f2\u00f3\7>\2\2\u00f3\60\3\2\2\2\u00f4\u00f5"+
		"\7>\2\2\u00f5\u00f6\7?\2\2\u00f6\62\3\2\2\2\u00f7\u00f8\7@\2\2\u00f8\64"+
		"\3\2\2\2\u00f9\u00fa\7@\2\2\u00fa\u00fb\7?\2\2\u00fb\66\3\2\2\2\u00fc"+
		"\u00fd\7>\2\2\u00fd\u00fe\7>\2\2\u00fe8\3\2\2\2\u00ff\u0100\7@\2\2\u0100"+
		"\u0101\7@\2\2\u0101:\3\2\2\2\u0102\u0103\7-\2\2\u0103<\3\2\2\2\u0104\u0105"+
		"\7-\2\2\u0105\u0106\7-\2\2\u0106>\3\2\2\2\u0107\u0108\7/\2\2\u0108@\3"+
		"\2\2\2\u0109\u010a\7/\2\2\u010a\u010b\7/\2\2\u010bB\3\2\2\2\u010c\u010d"+
		"\7,\2\2\u010dD\3\2\2\2\u010e\u010f\7\61\2\2\u010fF\3\2\2\2\u0110\u0111"+
		"\7\'\2\2\u0111H\3\2\2\2\u0112\u0113\7(\2\2\u0113J\3\2\2\2\u0114\u0115"+
		"\7~\2\2\u0115L\3\2\2\2\u0116\u0117\7(\2\2\u0117\u0118\7(\2\2\u0118N\3"+
		"\2\2\2\u0119\u011a\7~\2\2\u011a\u011b\7~\2\2\u011bP\3\2\2\2\u011c\u011d"+
		"\7`\2\2\u011dR\3\2\2\2\u011e\u011f\7#\2\2\u011fT\3\2\2\2\u0120\u0121\7"+
		"\u0080\2\2\u0121V\3\2\2\2\u0122\u0123\7A\2\2\u0123X\3\2\2\2\u0124\u0125"+
		"\7<\2\2\u0125Z\3\2\2\2\u0126\u0127\7=\2\2\u0127\\\3\2\2\2\u0128\u0129"+
		"\7.\2\2\u0129^\3\2\2\2\u012a\u012b\7?\2\2\u012b`\3\2\2\2\u012c\u012d\7"+
		"?\2\2\u012d\u012e\7?\2\2\u012eb\3\2\2\2\u012f\u0130\7#\2\2\u0130\u0131"+
		"\7?\2\2\u0131d\3\2\2\2\u0132\u0133\7\60\2\2\u0133f\3\2\2\2\u0134\u013a"+
		"\5m\67\2\u0135\u013a\5q9\2\u0136\u013a\5{>\2\u0137\u013a\5i\65\2\u0138"+
		"\u013a\5k\66\2\u0139\u0134\3\2\2\2\u0139\u0135\3\2\2\2\u0139\u0136\3\2"+
		"\2\2\u0139\u0137\3\2\2\2\u0139\u0138\3\2\2\2\u013ah\3\2\2\2\u013b\u013c"+
		"\7p\2\2\u013c\u013d\7w\2\2\u013d\u013e\7n\2\2\u013e\u013f\7n\2\2\u013f"+
		"j\3\2\2\2\u0140\u0141\7v\2\2\u0141\u0142\7t\2\2\u0142\u0143\7w\2\2\u0143"+
		"\u014a\7g\2\2\u0144\u0145\7h\2\2\u0145\u0146\7c\2\2\u0146\u0147\7n\2\2"+
		"\u0147\u0148\7u\2\2\u0148\u014a\7g\2\2\u0149\u0140\3\2\2\2\u0149\u0144"+
		"\3\2\2\2\u014al\3\2\2\2\u014b\u014f\5o8\2\u014c\u014e\5\u0085C\2\u014d"+
		"\u014c\3\2\2\2\u014e\u0151\3\2\2\2\u014f\u014d\3\2\2\2\u014f\u0150\3\2"+
		"\2\2\u0150\u0154\3\2\2\2\u0151\u014f\3\2\2\2\u0152\u0154\7\62\2\2\u0153"+
		"\u014b\3\2\2\2\u0153\u0152\3\2\2\2\u0154n\3\2\2\2\u0155\u0156\t\2\2\2"+
		"\u0156p\3\2\2\2\u0157\u0158\7)\2\2\u0158\u0159\5s:\2\u0159\u015a\7)\2"+
		"\2\u015ar\3\2\2\2\u015b\u015d\5u;\2\u015c\u015b\3\2\2\2\u015d\u015e\3"+
		"\2\2\2\u015e\u015c\3\2\2\2\u015e\u015f\3\2\2\2\u015ft\3\2\2\2\u0160\u0163"+
		"\n\3\2\2\u0161\u0163\5w<\2\u0162\u0160\3\2\2\2\u0162\u0161\3\2\2\2\u0163"+
		"v\3\2\2\2\u0164\u0165\5y=\2\u0165x\3\2\2\2\u0166\u0167\7^\2\2\u0167\u0168"+
		"\t\4\2\2\u0168z\3\2\2\2\u0169\u016b\7$\2\2\u016a\u016c\5}?\2\u016b\u016a"+
		"\3\2\2\2\u016b\u016c\3\2\2\2\u016c\u016d\3\2\2\2\u016d\u016e\7$\2\2\u016e"+
		"|\3\2\2\2\u016f\u0171\5\177@\2\u0170\u016f\3\2\2\2\u0171\u0172\3\2\2\2"+
		"\u0172\u0170\3\2\2\2\u0172\u0173\3\2\2\2\u0173~\3\2\2\2\u0174\u0177\n"+
		"\5\2\2\u0175\u0177\5w<\2\u0176\u0174\3\2\2\2\u0176\u0175\3\2\2\2\u0177"+
		"\u0080\3\2\2\2\u0178\u017d\5\u0083B\2\u0179\u017c\5\u0083B\2\u017a\u017c"+
		"\5\u0085C\2\u017b\u0179\3\2\2\2\u017b\u017a\3\2\2\2\u017c\u017f\3\2\2"+
		"\2\u017d\u017b\3\2\2\2\u017d\u017e\3\2\2\2\u017e\u0082\3\2\2\2\u017f\u017d"+
		"\3\2\2\2\u0180\u0181\t\6\2\2\u0181\u0084\3\2\2\2\u0182\u0183\t\7\2\2\u0183"+
		"\u0086\3\2\2\2\u0184\u0186\t\b\2\2\u0185\u0184\3\2\2\2\u0186\u0187\3\2"+
		"\2\2\u0187\u0185\3\2\2\2\u0187\u0188\3\2\2\2\u0188\u0189\3\2\2\2\u0189"+
		"\u018a\bD\2\2\u018a\u0088\3\2\2\2\u018b\u018d\7\17\2\2\u018c\u018b\3\2"+
		"\2\2\u018c\u018d\3\2\2\2\u018d\u018e\3\2\2\2\u018e\u018f\7\f\2\2\u018f"+
		"\u0190\3\2\2\2\u0190\u0191\bE\2\2\u0191\u008a\3\2\2\2\u0192\u0193\7\61"+
		"\2\2\u0193\u0194\7,\2\2\u0194\u0198\3\2\2\2\u0195\u0197\13\2\2\2\u0196"+
		"\u0195\3\2\2\2\u0197\u019a\3\2\2\2\u0198\u0199\3\2\2\2\u0198\u0196\3\2"+
		"\2\2\u0199\u019b\3\2\2\2\u019a\u0198\3\2\2\2\u019b\u019c\7,\2\2\u019c"+
		"\u019d\7\61\2\2\u019d\u019e\3\2\2\2\u019e\u019f\bF\2\2\u019f\u008c\3\2"+
		"\2\2\u01a0\u01a1\7\61\2\2\u01a1\u01a2\7\61\2\2\u01a2\u01a6\3\2\2\2\u01a3"+
		"\u01a5\n\t\2\2\u01a4\u01a3\3\2\2\2\u01a5\u01a8\3\2\2\2\u01a6\u01a4\3\2"+
		"\2\2\u01a6\u01a7\3\2\2\2\u01a7\u01a9\3\2\2\2\u01a8\u01a6\3\2\2\2\u01a9"+
		"\u01aa\bG\2\2\u01aa\u008e\3\2\2\2\22\2\u0139\u0149\u014f\u0153\u015e\u0162"+
		"\u016b\u0172\u0176\u017b\u017d\u0187\u018c\u0198\u01a6\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}