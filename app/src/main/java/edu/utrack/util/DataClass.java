package edu.utrack.util;

import java.util.Objects;

public abstract class DataClass {

    public String toString() {
        String[] names = getFieldNames();
        Object[] values = getFields();

        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(": {");
        for (int i = 0; i < names.length; i++) {
            if (i != 0) {
                builder.append(", ");
            }
            builder.append(names[i]).append(": ");
            builder.append(values[i]);
        }
        builder.append("}");
        return builder.toString();
    }

    public int hashCode() {
        return Objects.hash(getFields());
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (getClass() != obj.getClass()) return false;

        DataClass other = (DataClass) obj;
        Object[] fields = getFields();
        Object[] otherFields = other.getFields();
        if (fields.length != otherFields.length) {
            return false;
        }
        for (int i = 0; i < fields.length; i++) {
            Object val1 = fields[i];
            Object val2 = otherFields[i];
            if (val1 != val2 && !Objects.equals(val1, val2)) return false;
        }
        return true;
    }

    protected abstract String[] getFieldNames();

    protected abstract Object[] getFields();

    protected static String[] combineNames(String[] superFields, String...thisFields) {
        int superLen = superFields.length, fieldsLen = thisFields.length;
        String[] newFields = new String[superLen + fieldsLen];
        for(int i = 0; i < superLen; i++) newFields[i] = superFields[i];
        for(int i = 0; i < fieldsLen; i++) newFields[i + superLen] = thisFields[i];
        return newFields;
    }

    protected static Object[] combineFields(Object[] superFields, Object...thisFields) {
        int superLen = superFields.length, fieldsLen = thisFields.length;
        Object[] newFields = new Object[superLen + fieldsLen];
        for(int i = 0; i < superLen; i++) newFields[i] = superFields[i];
        for(int i = 0; i < fieldsLen; i++) newFields[i + superLen] = thisFields[i];
        return newFields;
    }
}
