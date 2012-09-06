package view;

import java.awt.Color;
import cytoscape.Cytoscape;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.PassThroughMapping;


public class IRegulonVisualStyle extends IRegulonResourceBundle {
    public static final String NAME = BUNDLE.getString("vizmap_name");
    private static final Color GRAY = new Color(220, 220, 220);

    private IRegulonVisualStyle() {
	}

    public static VisualStyle getVisualStyle() {
        final VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		return manager.getCalculatorCatalog().getVisualStyle(NAME);
    }
	
	public static void installVisualStyle() {
		final VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		final CalculatorCatalog catalog = manager.getCalculatorCatalog();
		if (catalog.getVisualStyle(NAME) != null) catalog.removeVisualStyle(NAME);
        final VisualStyle vs = createVisualStyle();
        catalog.addVisualStyle(vs);
	}
	
    private static VisualStyle createVisualStyle() {
        final VisualStyle style = new VisualStyle(NAME);
        style.setGlobalAppearanceCalculator(createGlobalAppearanceCalculator());

        final NodeAppearanceCalculator nodeAppCalc = style.getNodeAppearanceCalculator();
        // Node label = ID
        final Calculator nodeLabelCalculator = new BasicCalculator("iRegulon Visual Style Node Label Calculator",
                new PassThroughMapping(String.class, CytoscapeNetworkUtilities.ID_ATTRIBUTE_NAME),
                VisualPropertyType.NODE_LABEL);
        nodeAppCalc.setCalculator(nodeLabelCalculator);

        // Node shape =
        // 1. Ellipse when regulator
        // 2. Rectangle when target gene
        final DiscreteMapping nodeShapeMapper = new DiscreteMapping(NodeShape.class, CytoscapeNetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME);
        nodeShapeMapper.putMapValue(CytoscapeNetworkUtilities.REGULATORY_FUNCTION_TARGET_GENE, NodeShape.RECT);
        nodeShapeMapper.putMapValue(CytoscapeNetworkUtilities.REGULATORY_FUNCTION_REGULATOR, NodeShape.ELLIPSE);
        final Calculator nodeShapeCalculator = new BasicCalculator("iRegulon Visual Style Node Shape Calculator",
                nodeShapeMapper,
                VisualPropertyType.NODE_SHAPE);
        nodeAppCalc.setCalculator(nodeShapeCalculator);

        // Node color =
        final DiscreteMapping nodeColorMapper = new DiscreteMapping(Color.class, CytoscapeNetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME);
        nodeColorMapper.putMapValue(CytoscapeNetworkUtilities.REGULATORY_FUNCTION_REGULATOR, Color.GREEN);
        nodeColorMapper.putMapValue("", Color.BLUE);
        final Calculator nodeColorCalculator = new BasicCalculator("iRegulon Visual Style Node Shape Calculator",
                nodeColorMapper, VisualPropertyType.NODE_FILL_COLOR);
        nodeAppCalc.setCalculator(nodeColorCalculator);

        final EdgeAppearanceCalculator edgeAppCalc = style.getEdgeAppearanceCalculator();
        // Edge target arrow shape =
        final DiscreteMapping edgeArrowShapeMapper = new DiscreteMapping(ArrowShape.class, CytoscapeNetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME);
        edgeArrowShapeMapper.putMapValue(CytoscapeNetworkUtilities.REGULATORY_FUNCTION_PREDICTED, ArrowShape.ARROW);

        final Calculator edgeArrowCalculator = new BasicCalculator("iRegulon Visual Style Edge Arrow Shape Calculator",
                edgeArrowShapeMapper, VisualPropertyType.EDGE_TGTARROW_SHAPE);
        edgeAppCalc.setCalculator(edgeArrowCalculator);

        // Edge color =
        final Motif2EdgeColorMapping edgeColorMapping = new Motif2EdgeColorMapping();
        final Calculator edgeColorCalculator = new BasicCalculator("iRegulon Visual Style Edge Color Calculator",
                edgeColorMapping, VisualPropertyType.EDGE_COLOR);
        final Calculator arrowColorCalculator = new BasicCalculator("iRegulon Visual Style Edge Color Calculator",
                edgeColorMapping, VisualPropertyType.EDGE_TGTARROW_COLOR);
        edgeAppCalc.setCalculator(edgeColorCalculator);
        edgeAppCalc.setCalculator(arrowColorCalculator);

        return style;
    }

    private static GlobalAppearanceCalculator createGlobalAppearanceCalculator() {
        final GlobalAppearanceCalculator globalAppCalc = new GlobalAppearanceCalculator();
        globalAppCalc.setDefaultBackgroundColor(GRAY);
        return globalAppCalc;
    }
}