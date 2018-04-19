package be.ugent.rml;

import be.ugent.rml.records.Record;
import be.ugent.rml.store.Quad;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<String> applyTemplate(List<Element> template, Record record) {
        List<String> result = new ArrayList<>();
        result.add("");
        //we only return a result when all elements of the template are found
        boolean allValuesFound = true;

        //we iterate over all elements of the template, unless one is not found
        for (int i = 0; allValuesFound && i < template.size(); i ++) {
            //if the element is constant, we don't need to look at the data, so we can just add it to the result
            if (template.get(i).getType() == TEMPLATETYPE.CONSTANT) {
                for (int j = 0; j < result.size(); j ++) {
                    result.set(j, result.get(j) + template.get(i).getValue());
                }
            } else {
                //we need to get the variables from the data
                //we also need to keep all combinations of multiple results are returned for variable; pretty tricky business
                List<String> temp = new ArrayList<>();
                List<String> values = record.get(template.get(i).getValue());

                for (String value : values) {
                    for (String aResult : result) {
                        temp.add(aResult + value);
                    }
                }

                if (!values.isEmpty()) {
                    result = temp;
                }

                if (values.isEmpty()) {
                    //TODO logger warn
                    //logger.warn(`Not all values for a template where found. More specific, the variable ${template[i].value} did not provide any results.`);
                    allValuesFound = false;
                }
            }
        }

        if (allValuesFound) {
            if (countVariablesInTemplate(template) > 0) {
                String emptyTemplate = getEmptyTemplate(template);
                result.removeIf(s -> s.equals(emptyTemplate));
            }

            return result;
        } else {
            return new ArrayList<>();
        }
    }

    private static String getEmptyTemplate(List<Element> template) {
        String output = "";

        for (Element t : template) {
            if (t.getType() == TEMPLATETYPE.CONSTANT) {
                output += t.getValue();
            }
        }

        return output;
    }

    private static int countVariablesInTemplate(List<Element> template) {
        int counter = 0;

        for (Element aTemplate : template) {
            if (aTemplate.getType() == TEMPLATETYPE.VARIABLE) {
                counter++;
            }
        }

        return counter;
    }

    public static List<String> getSubjectsFromQuads(List<Quad> quads) {
        ArrayList<String> subjects = new ArrayList<String>();

        for (Quad quad : quads) {
            subjects.add(quad.getSubject());
        }

        return subjects;
    }

    public static List<String> getObjectsFromQuads(List<Quad> quads) {
        ArrayList<String> objects = new ArrayList<String>();

        for (Quad quad : quads) {
            objects.add(quad.getObject());
        }

        return objects;
    }

    public static List<String> getLiteralObjectsFromQuads(List<Quad> quads) {
        ArrayList<String> objects = new ArrayList<String>();

        for (Quad quad : quads) {
            objects.add(getLiteral(quad.getObject()));
        }

        return objects;
    }

    public static String getLiteral(String value) {
        return value;
    }

    public static boolean isLiteral(String value) {
        return true;
    }
}
