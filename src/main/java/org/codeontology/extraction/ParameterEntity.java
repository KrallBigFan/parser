package org.codeontology.extraction;


import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.codeontology.Ontology;
import org.codeontology.commentparser.DocCommentParser;
import org.codeontology.commentparser.ParamTag;
import org.codeontology.commentparser.Tag;
import org.codeontology.exceptions.NullTypeException;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

public class ParameterEntity extends NamedElementEntity<CtParameter<?>> implements TypedElementEntity<CtParameter<?>> {

    private int position;
    private ExecutableEntity<? extends CtExecutable> parent;
    private boolean parameterAvailable = true;
    private static final String TAG = "parameter";

    public ParameterEntity(CtParameter<?> parameter) {
        super(parameter);
        parameterAvailable = true;
    }

    public ParameterEntity(CtTypeReference<?> reference) {
        super(reference);
        parameterAvailable = false;
        if (reference.getQualifiedName().equals(CtTypeReference.NULL_TYPE_NAME)) {
            throw new NullTypeException();
        }
    }

    @Override
    public void extract() {
        tagType();
        tagJavaType();
        tagPosition();
        if (isDeclarationAvailable()) {
            tagAnnotations();
            tagName();
            tagComment();
        }
    }

    @Override
    public String buildRelativeURI() {
        return getParent().getRelativeURI() + SEPARATOR + TAG + SEPARATOR + position;
    }

    public void tagPosition() {
        getLogger().addTriple(this, Ontology.POSITION_PROPERTY, getModel().createTypedLiteral(position));
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setParent(ExecutableEntity<?> parent) {
        this.parent = parent;
    }

    public ExecutableEntity<?> getParent() {
        return this.parent;
    }

    @Override
    protected RDFNode getType() {
        return Ontology.PARAMETER_ENTITY;
    }

    @Override
    public TypeEntity<?> getJavaType() {
        if (isDeclarationAvailable()) {
            return getFactory().wrap(getElement().getType());
        } else {
            return getFactory().wrap((CtTypeReference<?>) getReference());
        }
    }

    public void tagJavaType() {
        new JavaTypeTagger(this).tagJavaType(parent);
    }

    @Override
    public boolean isDeclarationAvailable() {
        return parameterAvailable;
    }

    @Override
    public void tagComment() {
        if (parent.isDeclarationAvailable()) {
            String methodComment = parent.getElement().getDocComment();
            if (methodComment != null) {
                DocCommentParser parser = new DocCommentParser(methodComment);
                List<Tag> tags = parser.getTags("@param");
                for (Tag tag : tags) {
                    ParamTag paramTag = (ParamTag) tag;
                    if (paramTag.getParameterName().equals(getElement().getSimpleName())) {
                        Literal comment = getModel().createLiteral(paramTag.getParameterComment());
                        getLogger().addTriple(this, Ontology.COMMENT_PROPERTY, comment);
                        break;
                    }
                }
            }
        }
    }
}