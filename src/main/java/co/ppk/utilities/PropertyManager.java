package co.ppk.utilities;

import java.util.List;
import java.util.Map;

import co.ppk.exception.PpkException;
import org.apache.commons.configuration.event.ConfigurationListener;

/***
 * Basic functions for PropertyManager integration
 * 
 * @Descripcion
 * @author jmunoz
 * 
 * @version 1.0
 */
public interface PropertyManager extends ConfigurationListener {

    /**
     * given a search string with a property name  will return a property value if exists or null if it's does not exists 
     * 
     * 
     * @param value Property to be queried
     * @return String the value of the property, null if it's does not exists
     */
    String getProperty(String value) throws PpkException;

    /**
     * given a search string with a property name  will return a list of the properties values or null if it's does not exists
     * 
     * @param value property to be queried
     * @return List<String> list of  values assingned to the property, null if it's does not exists
     * 
     * 
     */
    List<String> getPropertyAsList(String value) throws PpkException;

    /**
     * Allow components to register a listener for properties changes
     * 
     * @param pml Custom Object implementing the interface PropertyManagerChangeListener 
     * @return operation status
     * 
     */
    boolean AddListener(PropertyManagerChangeListener pml) throws PpkException;

    
    /**
     * allow components to unregister a listener
     * 
     * @param value property to be queried
     * @return List<String> list of  values assingned to the property, null if it's does not exists
     * 
     */
    boolean removeListener(PropertyManagerChangeListener pml) throws PpkException;

    /**
     * print the properties managed by this component

     */
    void print() throws PpkException;

    /**
     * 
     * @return List<String> properties managed by this component
     */
    List<String> getPropertiesList();
    
    /**
     * 
     * @param propertyName property to be queried
     * @return Map of the matching property null if it does not exists 
     */
    
    Map<String, Object> getWebProperty(String propertyName);
    
    
    /**
     * 
     * @return map with all properties that compliance with web prefix
     */
    Map<String, Object> getWebProperties();

}
