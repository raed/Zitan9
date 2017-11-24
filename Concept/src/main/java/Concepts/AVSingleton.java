package Concepts;


import AbstractObjects.DataObject;
import Attributes.AttributeValueList;

/** This is the superclass for constrained and unconstrained singleton attribute values.
 */
public abstract class AVSingleton extends AVObject {

    public abstract void setValue(DataObject value, AttributeValueList constraint);
}
