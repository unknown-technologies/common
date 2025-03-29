package com.unknown.util.json;

import java.util.Objects;

public class Token {
	public static enum Type {
		TRUE, FALSE, NULL, ADD, SUB, STR, INT, FLOAT, COLON, COMMA, LBRAC, RBRAC, LBRACE, RBRACE, NONE
	}

	public final Type type;
	public final String sval;
	public final long ival;
	public final double fval;

	public Token(Type type) {
		this.type = type;
		this.sval = null;
		this.ival = 0;
		this.fval = 0;
	}

	public Token(String value) {
		this.type = Type.STR;
		this.sval = value;
		this.ival = 0;
		this.fval = 0;
	}

	public Token(long value) {
		this.type = Type.INT;
		this.sval = null;
		this.ival = value;
		this.fval = 0;
	}

	public Token(double value) {
		this.type = Type.FLOAT;
		this.sval = null;
		this.ival = 0;
		this.fval = value;
	}

	@Override
	public String toString() {
		return "Token[type=" + type + ",sval='" + sval + "',ival=" + ival + ",fval=" + fval + "]";
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(!(o instanceof Token)) {
			return false;
		}
		Token t = (Token) o;
		return t.type == type && Objects.equals(t.sval, sval) && t.ival == ival && t.fval == fval;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, sval, ival, fval);
	}
}
