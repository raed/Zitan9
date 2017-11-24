package DAGs;

import Attributes.AttributeValueList;
import MISC.Context;

public class AttributedLabel<Label,Owner extends SimpleHierarchy,DataBlock> {
    private Label label;
    private Owner owner;
    private DataBlock dataBlock;
    private AttributeValueList attributeList;

    public AttributedLabel(Label label, Owner owner, DataBlock dataBlock, AttributeValueList attributeList) {
        this.label = label;
        this.owner = owner;
        this.dataBlock = dataBlock;
        this.attributeList = attributeList;
    }

    public AttributedLabel setLabel(Label label) {
        this.label = label;
        return this;}

    public AttributedLabel setOwner(Owner owner) {
        this.owner = owner;
        return this;}

    public AttributedLabel setDataBlock(DataBlock dataBlock) {
        this.dataBlock = dataBlock;
        return this;}

    public AttributedLabel setAttributeList(AttributeValueList attributeList) {
        this.attributeList = attributeList;
        return this;}

    public Label getLabel() {
        return label;}

    public Owner getOwner() {
        return owner;}

    public DataBlock getDataBlock() {
        return dataBlock;}

    public AttributeValueList getAttributeList() {
        return attributeList;}

    public boolean isOwnedBy(Owner owner) {
        if(owner == null) {return this.owner == null;}
        if(this.owner == null) {return true;}
        return owner.isBelowOrEqual(this.owner);}


    public boolean matches(AttributeValueList attributeList, Context context) {
        if(this.attributeList == null || attributeList == null) {return true;}
        return this.attributeList.implies(attributeList, context);
    }





}
