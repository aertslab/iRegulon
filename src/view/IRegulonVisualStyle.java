package view;

import infrastructure.IRegulonResourceBundle;
import infrastructure.NetworkUtilities;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.ArrowShape;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

import java.awt.*;


public final class IRegulonVisualStyle extends IRegulonResourceBundle {
    public static final String NAME = RESOURCE_BUNDLE.getString("vizmap_name");
    //private static final Color GRAY = new Color(220, 220, 220);

    private static VisualStyle STYLE;

    private IRegulonVisualStyle() {
    }

    public static VisualStyle getVisualStyle() {
        if (STYLE == null) throw new IllegalStateException();
        return STYLE;
    }

    public static void applyVisualStyle(final CyNetworkView view) {
        getVisualStyle().apply(view);
    }

    public static void install(final CyServiceRegistrar serviceRegistrar) {
        if (STYLE != null) throw new IllegalStateException();

        final VisualMappingManager manager = serviceRegistrar.getService(VisualMappingManager.class);
        final VisualStyle oldStyle = findStyle(manager, NAME);
        if (oldStyle != null) manager.removeVisualStyle(oldStyle);
        STYLE = createVisualStyle(serviceRegistrar);
        manager.addVisualStyle(STYLE);
    }

    private static VisualStyle findStyle(final VisualMappingManager manager, final String name) {
        for (VisualStyle style : manager.getAllVisualStyles()) {
            if (name.equals(style.getTitle())) return style;
        }
        return null;
    }

    private static VisualStyle createVisualStyle(final CyServiceRegistrar serviceRegistrar) {
        final VisualStyleFactory factory = serviceRegistrar.getService(VisualStyleFactory.class);
        //TODO: This doesn't seem to work ...
        final VisualMappingFunctionFactory continuousMappingFactory = serviceRegistrar.getService(VisualMappingFunctionFactory.class, "(mapping.type=continuous)");
        final VisualMappingFunctionFactory discreteMappingFactory = serviceRegistrar.getService(VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
        final VisualMappingFunctionFactory passthroughMappingFactory = serviceRegistrar.getService(VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");

        final VisualStyle style = factory.createVisualStyle(NAME);
        style.setDefaultValue(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT, Color.GRAY);

        // Node label = name
        style.addVisualMappingFunction(passthroughMappingFactory.createVisualMappingFunction(
                NetworkUtilities.ID_ATTRIBUTE_NAME,
                String.class,
                BasicVisualLexicon.NODE_LABEL));

        // Node shape =
        // 1. Ellipse when regulator
        // 2. Rectangle when target gene
        final DiscreteMapping<String, NodeShape> nodeShapeMapper = (DiscreteMapping) discreteMappingFactory.createVisualMappingFunction(
                NetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME,
                String.class,
                BasicVisualLexicon.NODE_SHAPE);
        nodeShapeMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_TARGET_GENE, NodeShapeVisualProperty.RECTANGLE);
        nodeShapeMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_REGULATOR, NodeShapeVisualProperty.ELLIPSE);
        style.addVisualMappingFunction(nodeShapeMapper);

        // Node color =
        final DiscreteMapping<String, Color> nodeColorMapper = (DiscreteMapping) discreteMappingFactory.createVisualMappingFunction(
                NetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME,
                String.class,
                BasicVisualLexicon.NODE_FILL_COLOR);
        nodeColorMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_REGULATOR, Color.GREEN);
        nodeColorMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_TARGET_GENE, Color.WHITE);
        nodeColorMapper.putMapValue("", Color.BLUE);
        style.addVisualMappingFunction(nodeColorMapper);

        //final EdgeAppearanceCalculator edgeAppCalc = style.getEdgeAppearanceCalculator();
        // Edge target arrow shape =
        final DiscreteMapping<String, ArrowShape> edgeArrowShapeMapper = (DiscreteMapping) discreteMappingFactory.createVisualMappingFunction(
                NetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME,
                String.class,
                BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE);
        edgeArrowShapeMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_PREDICTED, ArrowShapeVisualProperty.ARROW);
        edgeArrowShapeMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_METATARGETOME, ArrowShapeVisualProperty.ARROW);
        style.addVisualMappingFunction(edgeArrowShapeMapper);

        // Edge color =
        /*final ContinuousMapping<Integer, Color> edgeColorMapping =
                (ContinuousMapping) continuousMappingFactory.createVisualMappingFunction(
                        NetworkUtilities.FEATURE_ID_ATTRIBUTE_NAME,
                        Integer.class,
                        BasicVisualLexicon.EDGE_PAINT);
        edgeColorMapping.addPoint(0, new BoundaryRangeValues<Color>(Color.BLUE, Color.BLUE, Color.BLUE));
        edgeColorMapping.addPoint(Integer.MAX_VALUE / 2, new BoundaryRangeValues<Color>(Color.RED, Color.RED, Color.RED));
        edgeColorMapping.addPoint(Integer.MAX_VALUE, new BoundaryRangeValues<Color>(Color.GREEN, Color.GREEN, Color.GREEN)); */
        final PassthroughMapping<Integer, Color> edgeColorMapping = (PassthroughMapping) passthroughMappingFactory.createVisualMappingFunction(
                NetworkUtilities.FEATURE_ID_ATTRIBUTE_NAME,
                Integer.class,
                BasicVisualLexicon.EDGE_PAINT);

        style.addVisualMappingFunction(edgeColorMapping);

        return style;
    }
/*
    private static GlobalAppearanceCalculator createGlobalAppearanceCalculator() {
        final GlobalAppearanceCalculator globalAppCalc = new GlobalAppearanceCalculator();
        globalAppCalc.setDefaultBackgroundColor(GRAY);
        return globalAppCalc;
    }*/
}
