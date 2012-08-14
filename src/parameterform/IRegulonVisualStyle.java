package parameterform;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public class IRegulonVisualStyle extends IRegulonResourceBundle{

	public final String vsName;
	
	public IRegulonVisualStyle(){
		vsName = getBundle().getString("vizmap_name");
		this.refreshVisualStyle();
	}
	
	public void refreshVisualStyle(){
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();
		VisualStyle vs = catalog.getVisualStyle(vsName);
		if (vs != null){
			catalog.removeVisualStyle(vsName);
		}
		this.createVizMap();
	}
	
	
	public void createVizMap(){
		// get the network and view
		CyNetwork network = Cytoscape.getCurrentNetwork();

        // get the VisualMappingManager and CalculatorCatalog
        VisualMappingManager manager = Cytoscape.getVisualMappingManager();
        CalculatorCatalog catalog = manager.getCalculatorCatalog();

        // check to see if a visual style with this name already exists
        VisualStyle vs = catalog.getVisualStyle(vsName);
        if (vs == null) {
                // if not, create it and add it to the catalog
                vs = createVisualStyle(network);
                catalog.addVisualStyle(vs);
        }

        //networkView.setVisualStyle(vs.getName()); // not strictly necessary

        // actually apply the visual style
        //manager.setVisualStyle(vs);
        //networkView.redrawGraph(true,true);

	}
	
	
    public VisualStyle createVisualStyle(CyNetwork network) {

            NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
            EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
            GlobalAppearanceCalculator globalAppCalc = new GlobalAppearanceCalculator();
            
            
            //gray background
            globalAppCalc.setDefaultBackgroundColor(new Color(220, 220, 220));


            // Passthrough Mapping - set node label
            //PassThroughMapping pm = new PassThroughMapping(new String(), "attr2");
            PassThroughMapping pm = new PassThroughMapping(new String(), "ID");
            Calculator nlc = new BasicCalculator("iRegulon Visual Style Node Label Calculator",
                                                 pm, VisualPropertyType.NODE_LABEL);
            nodeAppCalc.setCalculator(nlc);


            // Discrete Mapping - set node shapes
            DiscreteMapping disMapping = new DiscreteMapping(NodeShape.ELLIPSE,
                                                             ObjectMapping.NODE_MAPPING);
            disMapping.setControllingAttributeName("Regulatory function", network, false);
            disMapping.putMapValue(new String("Regulator"), NodeShape.ELLIPSE);
            //disMapping.putMapValue(new String(""), NodeShape.ELLIPSE);
            //disMapping.putMapValue(new Integer(3), NodeShape.TRIANGLE);

            Calculator shapeCalculator = new BasicCalculator("iRegulon Visual Style Node Shape Calculator",
                                                              disMapping, VisualPropertyType.NODE_SHAPE);
            nodeAppCalc.setCalculator(shapeCalculator);

            // Discrete Mapping - set node color
            disMapping = new DiscreteMapping(Color.BLUE, ObjectMapping.NODE_MAPPING);
            disMapping.setControllingAttributeName("Regulatory function", network, false);
            disMapping.putMapValue(new String("Regulator"), Color.GREEN);
            disMapping.putMapValue(new String(""), Color.BLUE);

            Calculator nodColorCalculator = new BasicCalculator("iRegulon Visual Style Node Shape Calculator",
                                                              disMapping, VisualPropertyType.NODE_FILL_COLOR);
            nodeAppCalc.setCalculator(nodColorCalculator);
            

            // Continuous Mapping - set node color
            /*ContinuousMapping continuousMapping = new ContinuousMapping(Color.WHITE,
                                                        ObjectMapping.NODE_MAPPING);
            continuousMapping.setControllingAttributeName("Regulatory function", network, false);

            Interpolator numToColor = new LinearNumberToColorInterpolator();
            continuousMapping.setInterpolator(numToColor);

            Color underColor = Color.GRAY;
            Color minColor = Color.RED;
            Color midColor = Color.WHITE;
            Color maxColor = Color.GREEN;
            Color overColor = Color.BLUE;

            // Create boundary conditions                  less than,   equals,  greater than
            BoundaryRangeValues bv0 = new BoundaryRangeValues(underColor, minColor, minColor);
            BoundaryRangeValues bv1 = new BoundaryRangeValues(midColor, midColor, midColor);
            BoundaryRangeValues bv2 = new BoundaryRangeValues(maxColor, maxColor, overColor);

            // Set the attribute point values associated with the boundary values
            continuousMapping.addPoint(0.0, bv0);
            continuousMapping.addPoint(1.0, bv1);
            continuousMapping.addPoint(2.0, bv2);

            Calculator nodeColorCalculator = new BasicCalculator("iRegulon Visual Style Node Color Calc",
                                                            continuousMapping, VisualPropertyType.NODE_FILL_COLOR);
            nodeAppCalc.setCalculator(nodeColorCalculator);
			*/

            // Discrete Mapping - Set edge target arrow shape
            DiscreteMapping arrowMapping = new DiscreteMapping(ArrowShape.NONE,
                                                               ObjectMapping.EDGE_MAPPING);
            arrowMapping.setControllingAttributeName("Regulatory function", network, false);
            arrowMapping.putMapValue("Predicted", ArrowShape.ARROW);
            //arrowMapping.putMapValue("pd", ArrowShape.CIRCLE);

            Calculator edgeArrowCalculator = new BasicCalculator("iRegulon Visual Style Edge Arrow Shape Calculator",
                                          arrowMapping, VisualPropertyType.EDGE_TGTARROW_SHAPE);
            edgeAppCalc.setCalculator(edgeArrowCalculator);
            
            
            
            
            // edge color
            
            DiscreteMapping edgeColorMapping = new DiscreteMapping(Color.BLUE, 
            														ObjectMapping.EDGE_MAPPING);
            edgeColorMapping.setControllingAttributeName("Motif", network, false);
            CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
            int edgesAmount = network.getEdgeCount();
            int[] indices = network.getEdgeIndicesArray();
            List<Object> motifs = new ArrayList();
    		for (int index = 0; index < edgesAmount; index++){
    			CyEdge edge = (CyEdge) network.getEdge(indices[index]);
    			//System.out.println(edge.getIdentifier());
    			List<Object> edgeAtr = new ArrayList<Object>();
    			edgeAtr.add(cyEdgeAttrs.getStringAttribute(edge.getIdentifier(), "Motif"));
    			boolean isIn = false;
    			for (Object edgeMotif : edgeAtr){
    				String motifString = "";
    				String edgeMotifString = (String) edgeMotif;
    				for (Object motif : motifs){
    					motifString = (String) motif;
    					if (motifString.equals(edgeMotifString)){
    						isIn = true;
    					}
    				}
    				if (! isIn){
        				motifs.add(edgeMotifString);
        			}
    				isIn = false;
    			}
    		}
    		
    		Object[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, 
    				Color.GRAY, Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, 
    				Color.ORANGE, Color.PINK, Color.RED, Color.WHITE, Color.YELLOW};
    		
    		
    		
    		//int index = 0;
    		Random generator = new Random();
    		for (Object motif : motifs){
    			int red = generator.nextInt(256);
    			int green = generator.nextInt(256);
    			int blue = generator.nextInt(256);
    			Color randomColor = new Color(red, green, blue);
    			//if (index == colors.length){
    			//	index = 0;
    			//}
    			edgeColorMapping.putMapValue(motif, randomColor);
    			//index++;
    		}

    		Calculator edgeColorCalculator = new BasicCalculator("iRegulon Visual Style Edge Color Calculator",
    				edgeColorMapping, VisualPropertyType.EDGE_COLOR);
    		Calculator arrowColorCalc = new BasicCalculator("iRegulon Visual Style Edge Color Calculator",
    				edgeColorMapping, VisualPropertyType.EDGE_TGTARROW_COLOR);
    		edgeAppCalc.setCalculator(edgeColorCalculator);
    		edgeAppCalc.setCalculator(arrowColorCalc);


            // Create the visual style
            VisualStyle visualStyle = new VisualStyle(vsName, nodeAppCalc, edgeAppCalc, globalAppCalc);

            return visualStyle;
    }


}
