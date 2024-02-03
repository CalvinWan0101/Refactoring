package tw.teddysoft.ezdoc.report.readme.visitor;

import com.github.javaparser.ast.expr.*;
import tw.teddysoft.ezdoc.annotation.Definition;
import tw.teddysoft.ezdoc.report.readme.PlaceholderLookupTable;

import java.lang.annotation.Annotation;
import java.util.Set;

public class DefinitionVisitor extends AbstractEzDocAnnotationVisitor {

    public DefinitionVisitor(PlaceholderLookupTable placeholderLookupTable,
                             Set<Class<? extends Annotation>> annotationsToBeErased) {
        super(placeholderLookupTable, annotationsToBeErased);
    }

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return Definition.class;
    }

    @Override
    protected String buildPlaceholder(NormalAnnotationExpr annotationExpr) {
        StringBuilder sb = new StringBuilder();
        sb.append("@")
                .append(Definition.class.getSimpleName())
                .append("(")
                .append(getAnnotationField(annotationExpr, "targetClass"))
                .append(")");
        return sb.toString();
    }

    @Override
    protected String buildReplacingValue(NormalAnnotationExpr annotationExpr) {
        StringBuilder sb = new StringBuilder();
        sb.append(getAnnotationField(annotationExpr, "brief"))
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(getAnnotationField(annotationExpr, "narrative"));
        return sb.toString();
    }
}