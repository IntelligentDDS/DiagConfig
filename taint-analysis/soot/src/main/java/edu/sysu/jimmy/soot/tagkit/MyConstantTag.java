package edu.sysu.jimmy.soot.tagkit;

import soot.jimple.Constant;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class MyConstantTag implements Tag {
    private final Constant myConstant;

    public MyConstantTag(Constant myConstant) {
        this.myConstant = myConstant;
    }

    public Constant getConstant() {
        return myConstant;
    }

    @Override
    public String toString() {
        return "ConstantTag contains value: " +
                myConstant.toString();
    }

    @Override
    public String getName() {
        return "MyConstantTag";
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return new byte[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyConstantTag that = (MyConstantTag) o;

        return myConstant.equals(that.myConstant);
    }

    @Override
    public int hashCode() {
        return myConstant.hashCode();
    }
}
