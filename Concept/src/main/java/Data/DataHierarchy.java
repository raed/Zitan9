package Data;

import DAGs.DAG;

/**
 */
public class DataHierarchy {
    private final static DAG<DataNode> dataHierarchy    = new DAG<DataNode>("DataHierarchy",(node->false));
}
