package github.josedoce.anotador.model;

import android.database.Cursor;

import github.josedoce.anotador.annotations.Enc;

public class Field {
    public enum Type {
        TEXT(0),
        PASSWORD(1),
        EMAIL(2),
        LINK(3),
        NUMERIC(4);

        final int id;
        Type(int id){
            this.id = id;
        }
        public int getId() {
            return id;
        }

    }
    private Integer id;
    private Integer annotationId;
    private String label;
    @Enc
    private String value;
    private Integer type;

    public Field(){}

    public Field(Cursor cursor) {
        int cId = cursor.getColumnIndex("id");
        int cAnnotationId = cursor.getColumnIndex("annotationId");
        int cLabel = cursor.getColumnIndex("label");
        int cValue = cursor.getColumnIndex("value");
        int cType = cursor.getColumnIndex("type");
        this.id = cursor.getInt(cId);
        this.annotationId = cursor.getInt(cAnnotationId);
        this.label = cursor.getString(cLabel);
        this.value = cursor.getString(cValue);
        this.type = cursor.getInt(cType);
    }

    public Field(Integer annotationId, String label, String value, Integer type) {
        this.annotationId = annotationId;
        this.label = label;
        this.value = value;
        this.type = type;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(Integer annotationId) {
        this.annotationId = annotationId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Type typeFrom(int id){
        switch (id){
            case 0:
                return Type.TEXT;
            case 1:
                return Type.PASSWORD;
            case 2:
                return Type.EMAIL;
            case 3:
                return Type.LINK;
            case 4:
                return Type.NUMERIC;
            default:
                return Type.TEXT;
        }
    }
}
