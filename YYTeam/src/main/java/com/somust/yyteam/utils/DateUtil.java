package com.somust.yyteam.utils;

import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.TeamNews;
import com.somust.yyteam.bean.TeamNewsMessage;
import com.somust.yyteam.bean.TeamTask;
import com.somust.yyteam.constant.Constant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by 13160677911 on 2017-4-21.
 */

public class DateUtil {

    /**
     * date转换为String
     * formatType格式为yyyy-MM-dd HH:mm:ss
     * //yyyy年MM月dd日 HH时mm分ss秒
     *
     * @param data
     * @param formatType
     * @return
     */
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }


    /**
     * String转换为Date
     * strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
     * strTime的时间格式必须要与formatType的时间格式相同
     * HH时mm分ss秒，
     *
     * @param strTime
     * @param formatType
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String strTime, String formatType) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }


    /**
     * 对社团新闻时间的排序
     * @param teamNewses
     */

    public static void TeamNewsSortDate(List<TeamNews> teamNewses){

        System.out.println("排序前："+teamNewses);

        Collections.sort(teamNewses, new Comparator<TeamNews>(){

            /*
             * int compare(Student o1, Student o2) 返回一个基本类型的整型，
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和o2相等，
             * 返回正数表示：o1大于o2。
             */
            public int compare(TeamNews o1, TeamNews o2) {


                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    Date date1 = df.parse(o1.getNewsTime());
                    Date date2 = df.parse(o2.getNewsTime());
                    //按照学生的年龄进行升序排列
                    if (date1.getTime() < date2.getTime()) {   //date1 在date2前
                        return 1;
                    } else if (date1.getTime() == date2.getTime()) {
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
        System.out.println("排序后："+teamNewses);
    }


    /**
     * 对社团新闻时间的排序
     * @param teams
     */

    public static void TeamSortDate(List<Team> teams){

        System.out.println("排序前："+teams);

        Collections.sort(teams, new Comparator<Team>(){

            /*
             * int compare(Student team1, Student team2) 返回一个基本类型的整型，
             * 返回负数表示：team1 team2，
             * 返回0 表示：team1和team2相等，
             * 返回正数表示：team1大于team2。
             */
            public int compare(Team team1, Team team2) {


                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    Date date1 = df.parse(team1.getTeamTime());
                    Date date2 = df.parse(team2.getTeamTime());
                    //按照学生的年龄进行升序排列
                    if (date1.getTime() < date2.getTime()) {   //date1 在date2前
                        return 1;
                    } else if (date1.getTime() == date2.getTime()) {
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
        System.out.println("排序后："+teams);
    }



    /**
     * 对社团活动时间的排序
     * @param teamTasks
     */

    public static void TeamTaskSortDate(List<TeamTask> teamTasks){

        System.out.println("排序前："+teamTasks);

        Collections.sort(teamTasks, new Comparator<TeamTask>(){

            /*
             * int compare(Student team1, Student team2) 返回一个基本类型的整型，
             * 返回负数表示：team1 team2，
             * 返回0 表示：team1和team2相等，
             * 返回正数表示：team1大于team2。
             */
            public int compare(TeamTask teamTask1, TeamTask teamTask2) {


                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    Date date1 = df.parse(teamTask1.getTaskCreateTime());
                    Date date2 = df.parse(teamTask2.getTaskCreateTime());
                    //按照学生的年龄进行升序排列
                    if (date1.getTime() < date2.getTime()) {   //date1 在date2前
                        return 1;
                    } else if (date1.getTime() == date2.getTime()) {
                        return 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
        System.out.println("排序后："+teamTasks);
    }






}
