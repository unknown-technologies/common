package com.unknown.util.ui;

public class IntegerFilter extends AbstractDocumentFilter {
	public static final int ANY = 0;
	public static final int POSITIVE = 1;
	public static final int POSITIVE_NONZERO = 2;

	private final int type;

	public IntegerFilter() {
		this(ANY);
	}

	public IntegerFilter(int type) {
		this.type = type;
	}

	@Override
	protected boolean test(String text) {
		try {
			int value = Integer.parseInt(text);
			switch(type) {
			default:
			case ANY:
				return true;
			case POSITIVE:
				return value >= 0;
			case POSITIVE_NONZERO:
				return value > 0;
			}
		} catch(NumberFormatException e) {
			return false;
		}
	}
}