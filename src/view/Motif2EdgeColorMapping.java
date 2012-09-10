package view;


import cytoscape.visual.converter.ValueToStringConverterManager;
import cytoscape.visual.mappings.AbstractMapping;
import cytoscape.visual.mappings.MappingUtil;
import cytoscape.visual.parsers.ValueParser;
import infrastructure.CytoscapeNetworkUtilities;

import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.*;


final class Motif2EdgeColorMapping extends AbstractMapping {
    private final Random generator = new Random();

    private final SortedMap<Integer,Color> map = new TreeMap<Integer, Color>();

    public Motif2EdgeColorMapping() {
        super(Color.class, CytoscapeNetworkUtilities.MOTIF_ATTRIBUTE_NAME);
    }

    private Color generateRandomColor() {
        final int red = generator.nextInt(256);
        final int green = generator.nextInt(256);
        final int blue = generator.nextInt(256);
        return new Color(red, green, blue);
    }

    @Override
    public void applyProperties(Properties props, String baseKey, ValueParser parser) {
        final String contKey = baseKey + ".controller";
        setControllingAttributeName(props.getProperty(contKey));

        final String contTypeKey = baseKey + ".controllerType";
        final String attrTypeString = props.getProperty(contTypeKey);
        byte attrType = -1; // UNDEFINED defaults to string
        if (attrTypeString != null) attrType = new Byte(attrTypeString);

        map.clear();
        final String mapKey = baseKey + ".map.";
        final Enumeration eProps = props.propertyNames();
        while (eProps.hasMoreElements()) {
            final String key = (String) eProps.nextElement();

            if (key.startsWith(mapKey)) {
                final String value = props.getProperty(key);
                final Integer domainVal = (Integer) MappingUtil.parseObjectType(
                        key.substring(mapKey.length()),
                        attrType);
                final Color parsedVal = (Color) parser.parseStringValue(value);
                map.put(domainVal, parsedVal);
            }
        }

    }

    @Override
    public Properties getProperties(String baseKey) {
        final Properties result = new Properties();

		final String contKey = baseKey + ".controller";
		result.setProperty(contKey, getControllingAttributeName());

		final String contTypeKey = baseKey + ".controllerType";
		result.setProperty(contTypeKey, MappingUtil.getAttributeTypeString(baseKey, getControllingAttributeName()));

		final String mapKey = baseKey + ".map.";
        for (final Integer code: map.keySet()) {
            final Color color = map.get(code);
            if (color != null) {
			   result.setProperty(mapKey + code.toString(), ValueToStringConverterManager.manager.toString(color));
            }
        }

		return result;
    }

    @Override
    public Object calculateRangeValue(Map<String, Object> attrBundle) {
        if (attrBundle == null) return null;

        // Extract the data value for our controlling attribute name
        final Object attrValue = attrBundle.get(getControllingAttributeName());
        if (attrValue == null) return null;
        final Integer code = attrValue.hashCode(); //More efficient when dealing with List<String>
        if (!map.containsKey(code)) map.put(code, generateRandomColor());
        return map.get(code);
    }

    @Override
    public Object clone() {
        final Motif2EdgeColorMapping clone = new Motif2EdgeColorMapping();

		// Copy over all listeners...
		for (ChangeListener listener : observers) clone.addChangeListener(listener);

		// Copy key-value pairs
		for (final Integer key : this.map.keySet()) clone.map.put(key, map.get(key));

		return clone;
    }
}
