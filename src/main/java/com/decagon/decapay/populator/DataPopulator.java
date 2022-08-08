/**
 * 
 */
package com.decagon.decapay.populator;


import com.decagon.decapay.exception.ConversionRuntimeException;

public interface DataPopulator<Source,Target> {

    Target populate(Source source,Target target) throws ConversionRuntimeException;
    Target populate(Source source) throws ConversionRuntimeException;

}
