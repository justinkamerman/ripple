import java.io.IOException;
import java.io.StringWriter;
import java.util.Map.Entry;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import mapreduce.PageRankMapper;
import mapreduce.PageRankReducer;
import mapreduce.PageRankCounters;
import io.PageRankWritable;



public class PageRank extends Configured implements Tool
{
    private Options __opt;
    private CommandLine __cl;


    private PageRank ()
    {
        __opt = new Options(); 
        __opt.addOption("h", false, "Print help");
        __opt.addOption("i", true, "Input file");
        __opt.addOption("o", true, "Output directory");
        __opt.addOption("r", true, "Initial PageRank score. Defaults to 1");
        __opt.addOption("c", false, "Print configuration");
    }


    private void printUsage (String message, int rc)
    {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp (message, __opt);
        System.exit (rc);
    }

    
    private void printConfig () throws IOException
    {
        StringWriter out = new StringWriter();
        Configuration conf = getConf();
        for (Entry<String, String> entry: conf)
        {
            System.out.printf ("%s=%s\n", entry.getKey(), entry.getValue());
        }
        System.exit (0);
    }


    public int run (String[] args) throws Exception
    {
        String inputPath = "input";
        String outputPath = "output";
        Double initialPageRank = new Double (1);

        try
        {        
            __cl = (new BasicParser()).parse (__opt, args); 
            if ( __cl.hasOption ('h') ) printUsage ("help", 0);
            if ( __cl.hasOption ('i') ) inputPath = __cl.getOptionValue ('i');  
            if ( __cl.hasOption ('o') ) outputPath = __cl.getOptionValue ('o');
            if ( __cl.hasOption ('r') ) initialPageRank = Double.parseDouble(__cl.getOptionValue ('r'));
            if ( __cl.hasOption ('c') ) printConfig ();
        }
        catch (ParseException ex)
        {
            printUsage (ex.getMessage(), 1);
            System.exit (1);
        }
        
        Configuration conf = new Configuration();
        conf.set("InitialPageRank", initialPageRank.toString());
        Job job = new Job(conf);
        job.setJarByClass (PageRank.class);
        
        FileInputFormat.addInputPath (job, new Path (inputPath));
        FileOutputFormat.setOutputPath (job, new Path (outputPath));
        
        job.setMapperClass (PageRankMapper.class);
        job.setReducerClass (PageRankReducer.class);
        
        job.setOutputKeyClass (LongWritable.class);
        job.setOutputValueClass (PageRankWritable.class);
        
        return (job.waitForCompletion (true) ? 0 : 1);
    }
    
    
    public static void main (String[] args) throws Exception
    {
        int exitCode = ToolRunner.run (new PageRank(), args);
        System.exit (exitCode);
    }
}