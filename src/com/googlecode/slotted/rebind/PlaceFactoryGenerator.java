package com.googlecode.slotted.rebind;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.googlecode.slotted.client.SlottedPlace;
import com.googlecode.slotted.client.rebind.PlaceFactory;

import java.io.PrintWriter;

public class PlaceFactoryGenerator extends Generator {

    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {


        TypeOracle typeOracle = context.getTypeOracle();
        JClassType clazz = typeOracle.findType(typeName);

        if (clazz == null) {
            throw new UnableToCompleteException();
        }

        try {

            String markerName = SlottedPlace.class.getName();
            JClassType placeType = typeOracle.getType(markerName);

            SourceWriter sourceWriter = getSourceWriter(clazz, context, logger);

            if (sourceWriter != null) {
                sourceWriter.println("public " +
                        placeType.getQualifiedSourceName() +
                        " newInstance(Class placeClass) {");

                JClassType[] types = typeOracle.getTypes();

                int count = 0;
                for (int i = 0; i < types.length; i++) {
                    if (!types[i].isAbstract() && types[i].isDefaultInstantiable() &&
                            types[i].isAssignableTo(placeType))
                    {
                        if (count == 0) {
                            sourceWriter.print("   if(");
                        } else {
                            sourceWriter.print("   else if(");
                        }
                        sourceWriter.println(types[i].getQualifiedSourceName()
                                + ".class.equals(placeClass)) {"
                                + " return GWT.create( "
                                + types[i].getQualifiedSourceName() + ".class);"
                                + "}");

                        count++;
                    }
                }

                sourceWriter.println("return null;");
                sourceWriter.println("}");
                sourceWriter.commit(logger);
                logger.log(TreeLogger.INFO, "Done Generating source for "
                        + clazz.getName(), null);

                return clazz.getQualifiedSourceName() + "Wrapper";

            }

        } catch (NotFoundException e) {

            e.printStackTrace();

        }

        return null;

    }

    public SourceWriter getSourceWriter(JClassType classType,
            GeneratorContext context, TreeLogger logger) {

        String packageName = classType.getPackage().getName();
        String simpleName = classType.getSimpleSourceName() + "Wrapper";
        ClassSourceFileComposerFactory composer =
                new ClassSourceFileComposerFactory(packageName, simpleName);

        String implementName = PlaceFactory.class.getName();
        composer.addImplementedInterface(implementName);
        composer.addImport(GWT.class.getCanonicalName());

        PrintWriter printWriter = context.tryCreate(logger, packageName,simpleName);

        if (printWriter == null) {
            return null;
        } else {
            SourceWriter sw = composer.createSourceWriter(context, printWriter);
            return sw;
        }

    }

}
