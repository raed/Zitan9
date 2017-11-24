package DAGs;

import java.util.ArrayList;
import java.util.Formatter;

/**
 * This class allows one to add metadata to a DAG's root nodes
 * Created on 29.05.2016.
 */

public class MetaData<N> {
    /** The root node to which the metadata is added. */
    private Node<N> node;
    /** an info string */
    private String info = null;
    /** some tags */
    private ArrayList<String> tags = null;
    /** a list of authors of the DAG */
    private ArrayList<String> authors;

    /** sets the root node
     *
     * @param node the root node
     * @return this
     */
    public MetaData<N> setNode(Node<N> node) {
        this.node = node;
        return this;}

    /** sets the info string
     *
     * @param info any info string
     * @return this
     */
    public MetaData<N> setInfo(String info) {
        this.info = info;
        return this;}

    /** sets some tags.
     * The method can be called several times to add more tags
     *
     * @param tags the tags to be added
     * @return this
     */
    public MetaData<N> setTags(String... tags) {
        if(this.tags == null) {this.tags = new ArrayList<>();}
        for(String tag : tags) {this.tags.add(tag);}
        return this;}

    /** checks if the metadata contains the tag
     *
     * @param tag any string
     * @return true if the string is contained in the tag list
     */
    public boolean containsTag(String tag) {
        return tags.contains(tag);}

    /** checks if the metadata contains contains the author
     *
     * @param author any string
     * @return true if the string is contained in the authors list.
     */
    public boolean containsAuthor(String author) {
        return authors.contains(author);}

    /** sets some authors
     * The method can be called several times to add new authors
     *
     * @param authors some authors
     * @return this
     */
    public MetaData<N> setAuthors(String... authors) {
        if(this.authors == null) {this.authors = new ArrayList<>();}
        for(String author : authors) {this.authors.add(author);}
        return this;}

    /** @return the root node */
    public Node<N> getNode() {return node;}

    /** @return the info string */
    public String getInfo() {return info;}

    /** @return the tags */
    public ArrayList<String> getTags() {return tags;}

    /** @return the authors */
    public ArrayList<String> getAuthors() {return authors;}

    /** @return the metadata as string */
    public String toString() {
        Formatter formatter = new Formatter();
        String format = "%1$-10s %2$s\n";
        formatter.format(format,"Node:", node.label.toString());
        if(authors != null){formatter.format(format,"Authors:",String.join(",",authors));}
        if(tags != null) {formatter.format(format,"Tags:", String.join(",",tags));}
        if(info != null) {formatter.format("\n%s",info);}
        return formatter.out().toString();
    }

    public static void main(String[] args) {
        InnerNode<String> n = new InnerNode<>("Student");
        MetaData<String> md = new MetaData<>();
        md.setNode(n).setAuthors("Ohlbach","Raed");
        md.setTags("TAG1", "TAG2");
        md.setInfo("Dies\nist\nein\ntest");
        System.out.println(md);

    }
}
