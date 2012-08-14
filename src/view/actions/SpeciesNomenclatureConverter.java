package view.actions;


import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import domainmodel.SpeciesNomenclature;

public class SpeciesNomenclatureConverter implements Converter {

        public boolean canConvert(Class clazz) {
                return clazz.equals(SpeciesNomenclature.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer,
                        MarshallingContext context) {
        		SpeciesNomenclature spnom = (SpeciesNomenclature) value;
                writer.startNode("code");
                writer.setValue("" + spnom.getCode());
                writer.endNode();
        }

        public Object unmarshal(HierarchicalStreamReader reader,
                        UnmarshallingContext context) {
                reader.moveDown();
                SpeciesNomenclature spnom = SpeciesNomenclature.getNomenclature(
                										Integer.parseInt(reader.getValue()));
                reader.moveUp();
                return spnom;
        }

}
