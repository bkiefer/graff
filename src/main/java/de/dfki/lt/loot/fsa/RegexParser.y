/* -*- Mode: Java -*- */


%{

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "unused", "fallthrough"})
%}

%language "Java"

%define package "de.dfki.lt.loot.fsa"

%define public

%define parser_class_name "RegexParser"

%code {
  protected CharFsa _result = new CharFsa();

  public CharFsa getAutomaton() { return _result; }

  public RegexParser(String pattern) {
    this(new StringReader(pattern));
  }
}

%lex-param { Reader in }

%code lexer {

  private static final String InitSetChars = "-]";

  private Reader _in;
  private int _line;
  private int _charPos;

  private CharFsa.SubAutomaton _lval;

  YYLexer(Reader in) {
    _in = in;
    _line = 1;
    _charPos = 0;
  }

  public void yyerror (String msg) {
    System.out.println("Error: " + msg + " in line " + _line + "." + _charPos);
  }

  int readNext(boolean skipws) throws IOException {
    int res = 0;
    do {
      res = _in.read(); ++_charPos;
      switch (res) {
      case -1: return EOF;
      case '\n': _charPos = 0; ++_line;
      case ' ':
      case '\t':
      case '\u000C':
      case '\r':  if (skipws) { res = 0; } break;
      }
    } while (res == 0);
    return res;
  }

  private Set<Character> getCharSet() throws IOException  {
    Set<Character> chars = new HashSet<Character>();
    // treat chars like '-' and ']' specially at the beginning of a set
    int nextChar = readNext(false);
    if (nextChar == EOF) {
      yyerror("unexpected end of input in character set");
      return null;
    }
    chars.add((char) nextChar); // the first char is safe, even ']'
    do {
      int lastChar = nextChar;
      nextChar = readNext(false);
      if (nextChar == ']') break;
      if (nextChar == '-' && lastChar != 0) {
        nextChar = readNext(false);
        if (nextChar >= lastChar) {
          for (int i = lastChar; i <= nextChar; ++i) {
            chars.add((char) i);
          }
        } else {
          yyerror("Empty character range specified: " + (char) lastChar
                  + "-" + nextChar);
        }
        lastChar = 0;
      } else {
        // add whatever there is
        lastChar =   nextChar;
        chars.add((char) nextChar);
      }
    } while (true);
    return chars;
  }

  public int yylex () throws java.io.IOException {
    int nextChar = readNext(false);
    switch (nextChar) {
    case EOF:
    case '(':
    case ')':
    case '|':
    case '*': _lval = null; return nextChar;
    case '\\': {
      // take next char literal
      nextChar = readNext(false);
      if (nextChar == EOF) {
        yyerror("unexpected end of input after backslash");
        return EOF;
      }
    }
    case '[':
      Set<Character> chars = getCharSet();
      if (chars == null) return EOF;
      _lval = _result.newCharSetAutomaton(chars);
      return CharSet;
    }
    _lval = _result.newCharAutomaton((char) nextChar);
    return Token;
  }

  public Object getLVal () {
    return _lval;
  }
}

%token < CharFsa.SubAutomaton > Token CharSet

%type < CharFsa.SubAutomaton > start regex atom alt sregex

%%

start     : regex { _result.setStates($1); }

/* TODO This grammar has a problem: concatenation (which does not have an
   operator symbol) should have higher priority than alternative. In fact,
   (AB|CD)* is parsed as (A(B|CD))* , rather than ((AB)|(CD))*

   I made several fruitless attempts at changing that without having to resort
   to fully parenthesized structure.

   That's the best i could do, with one (apparently harmless) shift/reduce
   conflict.
*/

regex     : sregex  { $$ = $1 ; }
          | alt { $$ = $1 ; }
          ;

sregex    : atom '*' sregex {
              $$ = _result.concatenate(_result.kleene($1), $3);
            }
          | atom '*' { $$ = _result.kleene($1) ; }
          | atom sregex  { $$ = _result.concatenate($1, $2); }
          | atom { $$ = $1 ; }
          ;

alt       : sregex '*' '|' regex {
              $$ = _result.alternative(_result.kleene($1), $4);
            }
| sregex '|' regex { $$ = _result.alternative($1, $3); }
          ;

atom      : Token { $$ = $1 ; }
          | '(' regex ')' { $$ = $2; }
          | CharSet { $$ = $1 ; }
          ;


%%

