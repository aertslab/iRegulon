package view;

import domainmodel.ClusterColors;
import infrastructure.IRegulonResourceBundle;
import infrastructure.NetworkUtilities;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.ArrowShape;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.*;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;

import java.awt.*;


public final class IRegulonVisualStyle extends IRegulonResourceBundle {
    public static final String VISUAL_STYLE_NAME = RESOURCE_BUNDLE.getString("vizmap_name");

    private static VisualStyle VISUAL_STYLE;

    /* Default network background color: light gray. */
    private static final Color DEFAULT_NETWORK_BACKGROUND_PAINT = new Color(0xCED7D7);

    /* Default node size */
    private static final double DEFAULT_NODE_HEIGHT = 40.0;
    private static final double DEFAULT_NODE_WIDTH = 80.0;
    /* Default transparency of a node: 0 - 255 */
    private static final int DEFAULT_NODE_TRANSPARENCY = 200;

    /* Default node border size. */
    private static final double DEFAULT_NODE_BORDER_WIDTH = 6.0;
    /* Default transparency of the border of a node: 0 - 255 */
    private static final int DEFAULT_NODE_BORDER_TRANSPARENCY = 200;

    /* Default node color: light blue. */
    private static final Color DEFAULT_NODE_FILL_COLOR = new Color(0x99FFFF);
    /* Default node border color: darker blue. */
    private static final Color DEFAULT_NODE_BORDER_PAINT_COLOR = new Color(0x0099CC);

    /* Regulator node color: green. */
    private static final Color REGULATOR_NODE_FILL_COLOR = new Color(0x00FF00);
    /* Regulator border node color: dark green. */
    private static final Color REGULATOR_NODE_BORDER_PAINT_COLOR = new Color(0x009900);

    /* Target gene node color: pink. */
    private static final Color TARGET_GENE_NODE_FILL_COLOR = new Color(0xFF33FF);
    /* Target gene border node color: purple. */
    private static final Color TARGET_GENE_BORDER_PAINT_COLOR = new Color(0x993399);

    /* Node label text size */
    private static final int DEFAULT_NODE_LABEL_FONT_SIZE = 15;
    /* Node label text color for a default node: blue. */
    private static final Color DEFAULT_NODE_LABEL_COLOR = new Color(0x0066CC);
    /* Node label text color when node is a regulator: black. */
    private static final Color REGULATOR_NODE_LABEL_COLOR = new Color(0x000000);
    /* Node label text color when node is target: black. */
    private static final Color TARGET_NODE_LABEL_COLOR = new Color(0x000000);

    /* Default edge width. */
    private static final double DEFAULT_EDGE_WIDTH = 3.0;


    private IRegulonVisualStyle() {
    }

    public static VisualStyle getVisualStyle() {
        if (VISUAL_STYLE == null) throw new IllegalStateException();
        return VISUAL_STYLE;
    }

    public static void applyVisualStyle(final CyNetworkView view) {
        getVisualStyle().apply(view);
    }

    public static void install(final CyServiceRegistrar serviceRegistrar) {
        if (VISUAL_STYLE != null) throw new IllegalStateException();

        final VisualMappingManager vmmServiceRef = serviceRegistrar.getService(VisualMappingManager.class);
        final VisualStyle oldStyle = findStyle(vmmServiceRef, VISUAL_STYLE_NAME);
        if (oldStyle != null) vmmServiceRef.removeVisualStyle(oldStyle);

        VISUAL_STYLE = createVisualStyle(serviceRegistrar);

        /* Add the new style to the VisualMappingManager. */
        vmmServiceRef.addVisualStyle(VISUAL_STYLE);
    }

    private static VisualStyle findStyle(final VisualMappingManager manager, final String name) {
        for (VisualStyle style : manager.getAllVisualStyles()) {
            if (name.equals(style.getTitle())) return style;
        }
        return null;
    }

    private static VisualStyle createVisualStyle(final CyServiceRegistrar serviceRegistrar) {
        final VisualStyleFactory visualStyleFactoryServiceRef = serviceRegistrar.getService(VisualStyleFactory.class);

        final VisualMappingFunctionFactory vmfFactoryDiscrete = serviceRegistrar.getService(VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
        final VisualMappingFunctionFactory vmfFactoryPassthrough = serviceRegistrar.getService(VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");

        /* Create a new visual style. */
        final VisualStyle visualStyle = visualStyleFactoryServiceRef.createVisualStyle(VISUAL_STYLE_NAME);


        /* Disable "Lock node width and height", so we can set a custom width and height. */
        for (VisualPropertyDependency<?> visualPropertyDependency : visualStyle.getAllVisualPropertyDependencies()) {
            if (visualPropertyDependency.getIdString().equals("nodeSizeLocked")) {
                visualPropertyDependency.setDependency(false);
                break;
            }
        }


        /* Set default network background color. */
        visualStyle.setDefaultValue(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT, DEFAULT_NETWORK_BACKGROUND_PAINT);

        /* Set default height, width, color, transparency and shape of the nodes. */
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_HEIGHT, DEFAULT_NODE_HEIGHT);
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_WIDTH, DEFAULT_NODE_WIDTH);
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, DEFAULT_NODE_FILL_COLOR);
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_TRANSPARENCY, DEFAULT_NODE_TRANSPARENCY);
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE);

        /* Set default node border width, color and transparency. */
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_BORDER_WIDTH, DEFAULT_NODE_BORDER_WIDTH);
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_BORDER_PAINT, DEFAULT_NODE_BORDER_PAINT_COLOR);
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_BORDER_TRANSPARENCY, DEFAULT_NODE_BORDER_TRANSPARENCY);

        /* Set default font size and color for the node labels. */
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_LABEL_FONT_SIZE, DEFAULT_NODE_LABEL_FONT_SIZE);
        visualStyle.setDefaultValue(BasicVisualLexicon.NODE_LABEL_COLOR, DEFAULT_NODE_LABEL_COLOR);

        /* Set default width for an edge. */
        visualStyle.setDefaultValue(BasicVisualLexicon.EDGE_WIDTH, DEFAULT_EDGE_WIDTH);

        /* Set node label to gene name. */
        visualStyle.addVisualMappingFunction(vmfFactoryPassthrough.createVisualMappingFunction(
                NetworkUtilities.ID_ATTRIBUTE_NAME,
                String.class,
                BasicVisualLexicon.NODE_LABEL));

        /* Set node shape based on the gene regulatory function. */
        final DiscreteMapping<String, NodeShape> nodeShapeMapper = (DiscreteMapping) vmfFactoryDiscrete.createVisualMappingFunction(
                NetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME,
                String.class,
                BasicVisualLexicon.NODE_SHAPE);
        nodeShapeMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_TARGET_GENE, NodeShapeVisualProperty.ELLIPSE);
        nodeShapeMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_REGULATOR, NodeShapeVisualProperty.OCTAGON);
        visualStyle.addVisualMappingFunction(nodeShapeMapper);

        /* Set node color based on the gene regulatory function. */
        final DiscreteMapping<String, Color> nodeColorMapper = (DiscreteMapping) vmfFactoryDiscrete.createVisualMappingFunction(
                NetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME,
                String.class,
                BasicVisualLexicon.NODE_FILL_COLOR);
        nodeColorMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_REGULATOR, REGULATOR_NODE_FILL_COLOR);
        nodeColorMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_TARGET_GENE, TARGET_GENE_NODE_FILL_COLOR);

        visualStyle.addVisualMappingFunction(nodeColorMapper);

        /* Set node border color based on the gene regulatory function. */
        final DiscreteMapping<String, Color> nodeBorderColorMapper = (DiscreteMapping) vmfFactoryDiscrete.createVisualMappingFunction(
                NetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME,
                String.class,
                BasicVisualLexicon.NODE_BORDER_PAINT);
        nodeBorderColorMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_REGULATOR, REGULATOR_NODE_BORDER_PAINT_COLOR);
        nodeBorderColorMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_TARGET_GENE, TARGET_GENE_BORDER_PAINT_COLOR);

        visualStyle.addVisualMappingFunction(nodeBorderColorMapper);

        /* Set different node label color when a node is selected or unselected. */
        final DiscreteMapping<String, Color> nodeLabelColorMapper = (DiscreteMapping) vmfFactoryDiscrete.createVisualMappingFunction(
                NetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME,
                String.class,
                BasicVisualLexicon.NODE_LABEL_COLOR);
        nodeLabelColorMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_REGULATOR, REGULATOR_NODE_LABEL_COLOR);
        nodeLabelColorMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_TARGET_GENE, TARGET_NODE_LABEL_COLOR);

        visualStyle.addVisualMappingFunction(nodeLabelColorMapper);

        /* Set edge target arrow shape. */
        final DiscreteMapping<String, ArrowShape> edgeArrowShapeMapper = (DiscreteMapping) vmfFactoryDiscrete.createVisualMappingFunction(
                NetworkUtilities.REGULATORY_FUNCTION_ATTRIBUTE_NAME,
                String.class,
                BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE);
        edgeArrowShapeMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_PREDICTED, ArrowShapeVisualProperty.ARROW);
        edgeArrowShapeMapper.putMapValue(NetworkUtilities.REGULATORY_FUNCTION_METATARGETOME, ArrowShapeVisualProperty.ARROW);
        visualStyle.addVisualMappingFunction(edgeArrowShapeMapper);

        /* Set edge color */
        final DiscreteMapping<Integer, Color> edgeStrokeUnselectedColorMapping = (DiscreteMapping) vmfFactoryDiscrete.createVisualMappingFunction(
                NetworkUtilities.CLUSTER_COLOR_ATTRIBUTE_NAME,
                Integer.class,
                BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT);

        final ClusterColors clusterColors = new ClusterColors();
        for (int clusterColor : clusterColors.getAllClusterColors()) {
            edgeStrokeUnselectedColorMapping.putMapValue(clusterColor, new Color(clusterColor));
        }

        visualStyle.addVisualMappingFunction(edgeStrokeUnselectedColorMapping);

        return visualStyle;
    }
}
