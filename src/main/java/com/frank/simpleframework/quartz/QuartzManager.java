package com.frank.simpleframework.quartz;

import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * Created by Frank （wx:F451209123） on 2017/12/23.
 */
public class QuartzManager {

    private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
    private static String JOB_GROUP_NAME = "SIMPLE_FRAMEWORK_JOB_GROUP_NAME";
    private static String TRIGGER_GROUP_NAME = "SIMPLE_FRAMEWORK_TRIGGER_GROUP_NAME";
    /**
     * @Description: 添加一个定时任务
     * @param jobName 任务名
     * @param jobGroupName 任务组名 可以为空，当空时则采用默认值
     * @param triggerName 触发器名
     * @param triggerGroupName 触发器组名 可以为空，当空时则采用默认值
     * @param jobClass 任务
     * @param time 时间设置，参考quartz说明文档
     *
     */
    @SuppressWarnings("unchecked")
    public static void addJob(String jobName, String jobGroupName,
                              String triggerName, String triggerGroupName, Class<? extends AbstractJob> jobClass,
                              String time,Object params) {
        try {
            if(StringUtils.isBlank(time)){
                throw new RuntimeException("time 参数为空");
            }
            if(null == jobClass){
                throw new RuntimeException("jobClass 参数为空");
            }
            Scheduler sched = gSchedulerFactory.getScheduler();
            if(StringUtils.isBlank(jobGroupName)){
                jobGroupName = JOB_GROUP_NAME;
            }
            if(StringUtils.isBlank(triggerGroupName)){
                triggerGroupName = TRIGGER_GROUP_NAME;
            }
            JobDetail jobDetail = newJob(jobClass).withIdentity(jobName,jobGroupName).build();
            jobDetail.getJobDataMap().put("params", params);
            // 触发器
            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(time);
            Trigger trigger = newTrigger().withIdentity(triggerKey(triggerName, triggerGroupName)).withSchedule(scheduleBuilder).build();
            sched.scheduleJob(jobDetail, trigger);
            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 修改一个任务的触发时间
     * @param jobName job名称
     * @param triggerName 触发器名称
     * @param jobGroupName job组名称
     * @param triggerGroupName 触发器组名称
     * @param time 执行时间
     *
     */
    @SuppressWarnings("unchecked")
    public static void modifyJobTime(String jobName,String triggerName, String triggerGroupName,String jobGroupName, String time) {
        try {
            if(StringUtils.isBlank(triggerGroupName)){
                triggerGroupName = TRIGGER_GROUP_NAME;
            }
            if(StringUtils.isBlank(jobGroupName)){
                jobGroupName = JOB_GROUP_NAME;
            }
            Scheduler sched = gSchedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) sched.getTrigger(new TriggerKey(triggerName,triggerGroupName));
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                JobDetail jobDetail = sched.getJobDetail(new JobKey(jobName,jobGroupName));
                Class objJobClass = jobDetail.getJobClass();
                removeJob(jobName,triggerName,triggerGroupName,jobGroupName);
                addJob(jobName,jobGroupName,triggerName,triggerGroupName, objJobClass, time ,jobDetail.getJobDataMap());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param jobName
     *
     */
    public static void removeJob(String jobName,String triggerName, String triggerGroupName,String jobGroupName) {
        try {
            if(StringUtils.isBlank(triggerGroupName)){
                triggerGroupName = TRIGGER_GROUP_NAME;
            }
            if(StringUtils.isBlank(jobGroupName)){
                jobGroupName = JOB_GROUP_NAME;
            }
            Scheduler sched = gSchedulerFactory.getScheduler();
            sched.pauseTrigger(new TriggerKey(triggerName,triggerGroupName));// 停止触发器
            sched.unscheduleJob(new TriggerKey(triggerName,triggerGroupName));// 移除触发器
            sched.deleteJob(new JobKey(jobName,jobGroupName));// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:启动所有定时任务
     */
    public static void startJobs() {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:关闭所有定时任务
     *
     */
    public static void shutdownJobs() {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
