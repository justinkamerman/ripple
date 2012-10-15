package mapreduce;

import java.io.IOException;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer;


/**
 * +---------------------------------+--------------------------------+
 * |        REDUCE FUNCTION                                           |
 * +---------------------------------+--------------------------------+
 * |      (inputKey, inputValue)     |    (outputKey, outputValue)    |
 * +---------------------------------+--------------------------------+
 */
public class AdjListReducer 
    extends Reducer<LongWritable, LongWritable, LongWritable, Iterable<LongWritable>>
{
    private static Log log = LogFactory.getLog (AdjListReducer.class);

    
    public void setup (Context context)
    {
    }


    public void reduce (LongWritable follower, Iterable<LongWritable> followeesIter, Context context)
        throws IOException, InterruptedException
    {
        Vector<LongWritable> followees = new Vector<LongWritable>();
        for (LongWritable followeeId : followeesIter)
        {
            followees.add (new LongWritable(followeeId.get()));
        }
        context.write (follower, followees);
    }
}