package org.codeontology.extraction.statement;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import spoon.reflect.code.CtSwitch;

public class SwitchEntity extends StatementEntity<CtSwitch<?>> {

    public SwitchEntity(CtSwitch element) {
        super(element);
    }

    @Override
    protected RDFNode getType() {
        return Ontology.SWITCH_ENTITY;
    }

    @Override
    public void extract() {
        super.extract();
    }
}
