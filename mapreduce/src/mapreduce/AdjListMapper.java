package mapreduce;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


/**
 * +---------------------------------+--------------------------------+
 * |          MAP FUNCTION           |           CONTEXT              |
 * +---------------------------------+--------------------------------+
 * |      (inputKey, inputValue)     |    (outputKey, outputValue)    |
 * +---------------------------------+--------------------------------+
 */
public class AdjListMapper 
    extends Mapper <LongWritable, Text, LongWritable, LongWritable>
{
    private static Log log = LogFactory.getLog (AdjListMapper.class);

    
    protected void setup (Context context) 
    {
        // Sets counters to zero
        context.getCounter(AdjListCounters.DROPPED_RECORDS);
        context.getCounter(AdjListCounters.MAPPER_LINES_PROCESSED);
    }


    public void map (LongWritable offset,
                     Text text,
                     Context context) throws IOException, InterruptedException
    {

        String[] fields = text.toString().split ("\\s+");
        if ( fields.length != 2 )
        {
            context.getCounter(AdjListCounters.DROPPED_RECORDS).increment(1);
            log.warn (String.format("Ignoring line %d as it has %d fields: %s",
                                    context.getCounter(AdjListCounters.MAPPER_LINES_PROCESSED).getValue(),
                                    fields.length, text.toString()));
        }
        else
        {
            Long followeeId;
            Long followerId;
            try
            {
                followerId = Long.parseLong (fields[0]);
                followeeId = Long.parseLong (fields[1]);
                context.write (new LongWritable (followerId),
                               new LongWritable (followeeId));
                log.debug (String.format("Emitted line %d as (%d, %d)",
                                         context.getCounter(AdjListCounters.MAPPER_LINES_PROCESSED).getValue(),
                                         followerId, followeeId));
                
            }
            catch (NumberFormatException ex)
            {
                context.getCounter(AdjListCounters.DROPPED_RECORDS).increment(1);
                log.warn (String.format("Ignoring line %d (%s): %s", 
                                        context.getCounter(AdjListCounters.MAPPER_LINES_PROCESSED).getValue(),
                                        text, ex.getMessage()));
            }            
        }
        context.getCounter(AdjListCounters.MAPPER_LINES_PROCESSED).increment(1);
    }
}