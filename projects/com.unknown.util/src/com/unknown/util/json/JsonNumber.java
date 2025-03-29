package com.unknown.util.json;

public class JsonNumber extends JsonValue {
	private boolean isint;
	private long ival;
	private double fval;

	public JsonNumber(long ival) {
		this.ival = ival;
		this.fval = ival;
		this.isint = true;
	}

	public JsonNumber(double fval) {
		this.ival = (long) fval;
		this.fval = fval;
		this.isint = false;
	}

	@Override
	public long intValue() {
		if(!isint) {
			throw new UnsupportedOperationException("not an integer value");
		}
		return ival;
	}

	@Override
	public double floatValue() {
		if(isint) {
			throw new UnsupportedOperationException("not a float value");
		}
		return fval;
	}

	public boolean isInteger() {
		return isint;
	}

	@Override
	public String toString() {
		return isint ? Long.toString(ival) : Double.toString(fval);
	}
}
