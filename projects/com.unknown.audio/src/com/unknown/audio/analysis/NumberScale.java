package com.unknown.audio.analysis;

import java.util.Iterator;
import java.util.Objects;

public class NumberScale {
	private NumberScaleType type;
	private double value0;
	private double value1;

	public NumberScale() {
		type = NumberScaleType.nstLinear;
		value0 = 0;
		value1 = 1;
	}

	public NumberScale(NumberScaleType type, double value0, double value1) {
		this.type = type;
		switch(type) {
		case nstLinear: {
			this.value0 = value0;
			this.value1 = value1;
		}
			break;
		case nstLogarithmic: {
			this.value0 = Math.log(value0);
			this.value1 = Math.log(value1);
		}
			break;
		case nstMel: {
			this.value0 = Scale.hzToMel(value0);
			this.value1 = Scale.hzToMel(value1);
		}
			break;
		case nstBark: {
			this.value0 = Scale.hzToBark(value0);
			this.value1 = Scale.hzToBark(value1);
		}
			break;
		case nstErb: {
			this.value0 = Scale.hzToErb(value0);
			this.value1 = Scale.hzToErb(value1);
		}
			break;
		case nstPeriod: {
			this.value0 = Scale.hzToPeriod(value0);
			this.value1 = Scale.hzToPeriod(value1);
		}
			break;
		default:
			throw new AssertionError();
		}
	}

	public NumberScale reversal() {
		NumberScale result = new NumberScale();
		result.type = type;
		result.value0 = value1;
		result.value1 = value0;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof NumberScale)) {
			return false;
		}
		NumberScale other = (NumberScale) o;
		return type == other.type && value0 == other.value0 && value1 == other.value1;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value0, value1);
	}

	// Random access
	public double positionToValue(double pp) {
		switch(type) {
		case nstLinear:
			return value0 + pp * (value1 - value0);
		case nstLogarithmic:
			return Math.exp(value0 + pp * (value1 - value0));
		case nstMel:
			return Scale.melToHz(value0 + pp * (value1 - value0));
		case nstBark:
			return Scale.barkToHz(value0 + pp * (value1 - value0));
		case nstErb:
			return Scale.erbToHz(value0 + pp * (value1 - value0));
		case nstPeriod:
			return Scale.periodToHz(value0 + pp * (value1 - value0));
		default:
			throw new AssertionError();
		}
	}

	// STL-idiom iteration
	private class NumberScaleIterator implements Iterator<Double> {
		private NumberScaleType type;
		private double step;
		private double value;

		NumberScaleIterator(NumberScaleType type, double step, double value) {
			this.type = type;
			this.step = step;
			this.value = value;
		}

		private double get() {
			switch(type) {
			case nstLinear:
			case nstLogarithmic:
				return value;
			case nstMel:
				return Scale.melToHz(value);
			case nstBark:
				return Scale.barkToHz(value);
			case nstErb:
				return Scale.erbToHz(value);
			case nstPeriod:
				return Scale.periodToHz(value);
			default:
				throw new AssertionError();
			}
		}

		@Override
		public Double next() {
			switch(type) {
			case nstLinear:
			case nstMel:
			case nstBark:
			case nstErb:
			case nstPeriod:
				value += step;
				break;
			case nstLogarithmic:
				value *= step;
				break;
			default:
				throw new AssertionError();
			}
			return get();
		}

		@Override
		public boolean hasNext() {
			double val;
			switch(type) {
			case nstLinear:
			case nstMel:
			case nstBark:
			case nstErb:
			case nstPeriod:
				val = value + step;
				break;
			case nstLogarithmic:
				val = value * step;
				break;
			default:
				throw new AssertionError();
			}
			if(val > value) {
				return val > value1;
			} else {
				return val < value1;
			}
		}
	}

	public Iterator<Double> iterator(double nPositions) {
		switch(type) {
		case nstLinear:
		case nstMel:
		case nstBark:
		case nstErb:
		case nstPeriod:
			return new NumberScaleIterator(type, nPositions == 1 ? 0 : (value1 - value0) / (nPositions - 1),
					value0);
		case nstLogarithmic:
			return new NumberScaleIterator(type,
					nPositions == 1 ? 1 : Math.exp((value1 - value0) / (nPositions - 1)),
					Math.exp(value0));
		default:
			throw new AssertionError();
		}
	}

	// Inverse
	public double valueToPosition(double val) {
		switch(type) {
		case nstLinear:
			return((val - value0) / (value1 - value0));
		case nstLogarithmic:
			return((Math.log(val) - value0) / (value1 - value0));
		case nstMel:
			return((Scale.hzToMel(val) - value0) / (value1 - value0));
		case nstBark:
			return((Scale.hzToBark(val) - value0) / (value1 - value0));
		case nstErb:
			return((Scale.hzToErb(val) - value0) / (value1 - value0));
		case nstPeriod:
			return((Scale.hzToPeriod(val) - value0) / (value1 - value0));
		default:
			throw new AssertionError();
		}
	}
}
