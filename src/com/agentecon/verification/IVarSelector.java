package com.agentecon.verification;

import org.jacop.floats.core.FloatVar;

public interface IVarSelector<T> {

	public FloatVar extract(T con);

}
