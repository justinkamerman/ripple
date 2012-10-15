package mapreduce;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
import io.PageRankWritable;


/**
 * +---------------------------------+--------------------------------+
 * |          MAP FUNCTION           |           CONTEXT              |
 * +---------------------------------+--------------------------------+
 * |      (inputKey, inputValue)     |    (outputKey, outputValue)    |
 * +---------------------------------+--------------------------------+
 */
public class PageRankMapper 
    extends Mapper <LongWritable, Text, LongWritable, PageRankWritable>
{
    private static Log log = LogFactory.getLog (PageRankMapper.class);
    private static final Pattern __pattern = Pattern.compile (new String("(\\d+)\\s*?([\\d.]+)?\\s+\\[(.*)\\]"));
    private static Double __initialPageRank;
    
    protected void setup (Context context) 
    {
        // Sets counters to zero
        context.getCounter(PageRankCounters.DROPPED_RECORDS);
        context.getCounter(PageRankCounters.MAPPER_LINES_PROCESSED);

        // Get job parameters
        Configuration conf = context.getConfiguration();
        String param = conf.get("InitialPageRank");
        __initialPageRank = Double.parseDouble (param);
        log.info ("Initial PageRank value set via job configuration to " + __initialPageRank);
    }


    public void map (LongWritable offset,
                     Text text,
                     Context context) throws IOException, InterruptedException
    {
        Matcher matcher = __pattern.matcher (text.toString());
        if (matcher.lookingAt())
        {
            //if (log.isInfoEnabled())
            //{
            //    for (int i = 1; i <= matcher.groupCount(); i++)
            //    {
            //        log.info ("match " + i + ": \'" + matcher.group (i) + "\'");
            //    }
            //}

            if (null == matcher.group(1)) // no nodeId
            {
                context.getCounter(PageRankCounters.DROPPED_RECORDS).increment(1);
                log.warn (String.format("Cannot parse line %d (nodeId is null): %s",
                                    context.getCounter(PageRankCounters.MAPPER_LINES_PROCESSED).getValue(),
                                    text.toString()));
            }
            else
            {
                if (null == matcher.group(3)) // no adjacency list
                {
                    context.getCounter(PageRankCounters.DROPPED_RECORDS).increment(1);
                    log.warn (String.format("Cannot parse line %d (adjacency list is null): %s",
                                            context.getCounter(PageRankCounters.MAPPER_LINES_PROCESSED).getValue(),
                                            text.toString()));
                }
                else // got everything we need
                {
                    Long nodeId = Long.parseLong (matcher.group(1));
                    Double pageRank = __initialPageRank;
                    String adjListString = matcher.group(3);
                    log.debug ("Processing node " + nodeId);
                    if (null != matcher.group(2))
                    {
                        pageRank = Double.parseDouble (matcher.group(2));
                    }
                    else
                    {
                        log.debug ("PageRank not available in input; using " + __initialPageRank);
                    }

                    // Parse adjacency list
                    String[] adjListArray = adjListString.split (", ");
                    
                    // If there were no splits found, the split()
                    // function will return an array with one element,
                    // the original string, so we need to check for
                    // this explicitly
                    LongWritable[] adjListWritable;
                    if ( adjListString.equals (adjListArray[0]) )
                    {
                        adjListWritable = new LongWritable[0];
                    }
                    else
                    {
                        adjListWritable = new LongWritable[adjListArray.length];
                        for (int i = 0; i < adjListArray.length; i++)
                        {
                            // Create adjacency list for node emit
                            adjListWritable[i] = new LongWritable (Long.parseLong(adjListArray[i]));
                            
                            // Emit mass transfer contributions
                            Double massContrib = pageRank / (double) adjListArray.length;
                            PageRankWritable mass = new PageRankWritable (new DoubleWritable (massContrib));
                            context.write (adjListWritable[i], mass);
                        }
                    }

                    // Emit node
                    PageRankWritable node = new PageRankWritable (new DoubleWritable (pageRank), adjListWritable);
                    context.write (new LongWritable (nodeId), node);
                }

            }
        
        }
        else // no regex match
        {
            context.getCounter(PageRankCounters.DROPPED_RECORDS).increment(1);
            log.warn (String.format("Cannot parse line %d: %s",
                                    context.getCounter(PageRankCounters.MAPPER_LINES_PROCESSED).getValue(),
                                    text.toString()));
        }

        context.getCounter(AdjListCounters.MAPPER_LINES_PROCESSED).increment(1);
    }
}