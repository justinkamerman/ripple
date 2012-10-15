package io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;


public class PageRankWritable implements Writable
{
    // PageRank Mass
    private DoubleWritable __mass = new DoubleWritable (0);
    // Node
    private DoubleWritable __pageRank = new DoubleWritable (0);
    private ArrayWritable __adjList = new ArrayWritable (LongWritable.class);
    

    public PageRankWritable ()
    {
    }


    public PageRankWritable (DoubleWritable mass)
    {
        __mass = mass;
        __adjList.set (new LongWritable[0]);
    }

    public PageRankWritable (DoubleWritable pageRank, LongWritable[] adjList)
    {
        __pageRank = pageRank;
        __adjList.set (adjList);
    }


    public DoubleWritable getPageRank () { return __pageRank; }
    public void setPageRank (DoubleWritable pageRank) { __pageRank = pageRank; }
    public ArrayWritable getAdjList () { return __adjList; }
    public int getAdjListSize () { return __adjList.get().length; }
    public DoubleWritable getMass () { return __mass; }


    public boolean isNode ()
    {
        return __mass.equals (new DoubleWritable(0));
    }


    @Override
    public void write (DataOutput out) throws IOException
    {
        __pageRank.write (out);
        __adjList.write (out);
        __mass.write (out);
    }

    @Override
    public void readFields (DataInput in) throws IOException
    {
        __pageRank.readFields (in);
        __adjList.readFields (in);
        __mass.readFields(in);
    }
    

    public static PageRankWritable read (DataInput in) throws IOException
    {
        PageRankWritable pageRank = new PageRankWritable ();
        pageRank.readFields (in);
        return pageRank;
    }   


    @Override
    public int hashCode ()
    {
        return ((__mass.hashCode() * 33) ^ (__pageRank.hashCode() * 163) ^ __adjList.hashCode());
    }

    @Override
    public boolean equals (Object o)
    {
        if (o instanceof PageRankWritable)
        {
            PageRankWritable tuple = (PageRankWritable) o;
            return (__pageRank.equals(tuple.getPageRank())
                    && __adjList.equals(tuple.getAdjList())
                    && __mass.equals(tuple.getMass()));
        }
        return false;
    }

    @Override
    public String toString ()
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < __adjList.get().length; i++)
        {
            LongWritable friendId = (LongWritable)(__adjList.get())[i];
            sb.append (friendId.get());
            if ( i < __adjList.get().length - 1) sb.append (", ");
        }
        return String.format ("%f    [%s]", __pageRank.get(), sb.toString());
    }

    
    /**
     * Copy contructor: deep copy for writing to context
     */
    public PageRankWritable (PageRankWritable orig)
    {
        // Deep copy adjacency list
        int adjListLength = orig.getAdjList().get().length;
        Writable[] origAdjList = orig.getAdjList().get();
        LongWritable[] newAdjList = new LongWritable [adjListLength];
        for (int i = 0; i < adjListLength; i++)
        {
            LongWritable follower = (LongWritable) origAdjList[i];
            newAdjList[i] = new LongWritable (follower.get());
        }
        
        __mass = new DoubleWritable (orig.getMass().get()); 
        __pageRank = new DoubleWritable (orig.getPageRank().get()); 
        __adjList = new ArrayWritable (LongWritable.class);
        __adjList.set (newAdjList);
    }
}

    
    