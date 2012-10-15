package mapreduce;

import java.io.IOException;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.conf.Configuration;
import io.PageRankWritable;

/**
 * +---------------------------------+--------------------------------+
 * |        REDUCE FUNCTION                                           |
 * +---------------------------------+--------------------------------+
 * |      (inputKey, inputValue)     |    (outputKey, outputValue)    |
 * +---------------------------------+--------------------------------+
 */
public class PageRankReducer 
    extends Reducer<LongWritable, PageRankWritable, LongWritable, PageRankWritable>
{
    private static Log log = LogFactory.getLog (PageRankReducer.class);
    private static Double __initialPageRank;

    
    public void setup (Context context)
    {
        // Set counters to zero
        context.getCounter(PageRankCounters.TOTAL_RANK_ADJUSTMENT);

        // Get job parameters
        Configuration conf = context.getConfiguration();
        String param = conf.get("InitialPageRank");
        __initialPageRank = Double.parseDouble (param);
        log.info ("Initial PageRank value set via job configuration to " + __initialPageRank);
    }


    public void reduce (LongWritable nodeId, Iterable<PageRankWritable> pageRankIter, Context context)
        throws IOException, InterruptedException
    {
        log.debug ("Processing node " + nodeId);

        PageRankWritable node = null;
        Double massTotal = new Double(0);
        for (PageRankWritable pageRank : pageRankIter)
        {
            if (pageRank.isNode())
            {
                // Got the node passed from the mapper: put it
                // aside so we can update the pagerank after
                // summing the mass contributions and emit.
                node = new PageRankWritable (pageRank);
            }
            else
            {
                // Sum mass transfer contributions
                massTotal += pageRank.getMass().get();
            }
        }

        // Need to 'artificially' create a node to represent
        // leaf. This should only happen on first run.
        if ( null == node )
        {
            log.debug (String.format("Creating leaf node %d", nodeId.get()));
            node = new PageRankWritable (new DoubleWritable(__initialPageRank), new LongWritable[0]);
        }

        Long rankAdjustment = Math.round (1000 * Math.abs (node.getPageRank().get() - massTotal));
        log.debug (String.format("Adjusted node %d PageRank from %f to %f (%d/1000)", 
                                nodeId.get(), node.getPageRank().get(), massTotal, rankAdjustment));
        context.getCounter(PageRankCounters.TOTAL_RANK_ADJUSTMENT).increment (rankAdjustment);
        node.setPageRank (new DoubleWritable (massTotal));
        context.write (nodeId, node);
    }
}