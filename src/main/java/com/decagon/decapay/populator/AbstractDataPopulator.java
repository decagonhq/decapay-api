/**
 * 
 */
package com.decagon.decapay.populator;


import com.decagon.decapay.exception.ConversionRuntimeException;

import java.util.Locale;


/**
 * @author Oloba
 *
 */
public abstract class AbstractDataPopulator<Source,Target> implements DataPopulator<Source, Target>
{

    private Locale locale;

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public Locale getLocale() {
		return locale;
	}
	


	@Override
	public Target populate(Source source) throws ConversionRuntimeException {
	   return populate(source,createTarget());
	}
	protected abstract Target createTarget();

   

}
