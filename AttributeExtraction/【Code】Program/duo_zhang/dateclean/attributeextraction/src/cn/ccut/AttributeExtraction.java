package cn.ccut;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * MapReduce程序,属性提取主类
 */
public class AttributeExtraction {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "MyFirstJob");

        job.setJarByClass(AttributeExtraction.class);
        job.setMapperClass(EnterpriesMapper.class);
        job.setReducerClass(EnterpriesReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Enterprise.class);

        job.setOutputKeyClass(Enterprise.class);
        job.setOutputValueClass(NullWritable.class);

        //FileInputFormat.setInputPaths(job, args[0]);
        //FileOutputFormat.setOutputPath(job, new Path(args[1]));
        // String inputDataPath = "C:\\Users\\zhipeng-Tong\\Desktop\\异常企业资料\\信息3";
        String inputDataPath = "D:\\视频诗词\\异常企业资料\\信息3";
        FileInputFormat.setInputPaths(job, inputDataPath);

        String s = UUID.randomUUID().toString();
        String outputDataPath = "D:\\视频诗词\\异常企业资料\\result\\" + s;

        FileOutputFormat.setOutputPath(job, new Path(outputDataPath));

        job.waitForCompletion(true);

        if(inputDataPath.contains("3")) {
            upDateTrainAndTest(outputDataPath);
        }
    }

    private static void upDateTrainAndTest(String path) throws Exception {
        path = path + "\\" + "part-r-00000";
        List<String> list = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            list.add(line);
        }

        List<String> trainList = new ArrayList<>();
        List<String> testList = new ArrayList<>();
        List<Integer> sampleRandomNum = new ArrayList<>();
        Random random = new Random();
        //取出总数的0.7作为训练数据
        int trainSample = (int) (list.size() * 0.7);
        int temp = 0;

        for (int i = 0; i < trainSample; ) {
            temp = random.nextInt(list.size());

            if(!sampleRandomNum.contains(temp)) {
                sampleRandomNum.add(temp);
                i++;
            }
        }

        //取出
        for(int dataIndex : sampleRandomNum) {
            String sample = list.get(dataIndex);
            trainList.add(sample);
        }
        //取出
        for(int i = 0; i < list.size(); i++) {
            if(!sampleRandomNum.contains(i)) {
                testList.add(list.get(i));
            }
        }

        //将数据输出
        String trainOutPath = "D:\\视频诗词\\异常企业资料\\out\\train.dat";
        String testOutPath = "D:\\视频诗词\\异常企业资料\\out\\test.dat";

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(trainOutPath));

        for(String s : trainList) {
            bufferedWriter.write(s);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        bufferedWriter.close();

        BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(testOutPath));
        for(String s : testList) {
            bufferedWriter2.write(s);
            bufferedWriter2.newLine();
            bufferedWriter2.flush();
        }

    }
}
