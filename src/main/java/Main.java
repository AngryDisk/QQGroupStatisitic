import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.MyStaticValue;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private final static int YEAR = 365;
    private static DateFormat weekNum = new SimpleDateFormat("u");
    private static DateFormat hourNum = new SimpleDateFormat("HH");
    private static int total = 0;

    public static void main(String[] args) throws Exception {
        //read orgin file str
        MyStaticValue.isRealName = true;
        String filepath = "D:/underwindreal2.txt";
//        String filepath = "D:/underwind.txt";
        File file = new File(filepath);
        FileInputStream fi = new FileInputStream(file);
        byte[] bt = new byte[fi.available()];
        fi.read(bt);
        String str = new String(bt, "utf-8");

        //split the str correct
        String txt2strArr = "\\r\\n\\r\\n2017-\\d\\d-\\d\\d [\\d]+:[\\d]+:[\\d]+ [^<(\\r\\n]+[<(][^>)\\r\\n]+[>)]\\r\\n[\\r\\n\\S\\s]*?(?=\\r\\n\\r\\n)";
        Matcher m = Pattern.compile(txt2strArr).matcher(str);
        List<String> s = new ArrayList<String>();
        while (m.find()) {
            s.add(m.group(0));
        }
//        for (int i = 0; i < s.size(); i++) {
//            System.out.println(s.get(i));
//        }
        Map<String, String> realPersonMap = new HashMap<String, String>() {
            {
                //添加其他人信息
                put("1000000", "系统消息");
                put("10000", "系统消息");
            }
        };
        //
        String regexDate = "(2017-\\d\\d-\\d\\d [\\d]+:[\\d]+:\\d\\d)";
        String regexNickname = "(?<=:\\d\\d )([^<(\\r\\n]+)(?=[<(])";//使用group的方式，获得括号
        String regexUid = "(?<=[<(])([\\S]+)(?=[>)])";
        String regexContext = "(?<=[>)]\\r\\n)([\\s\\S]*)";

        String regexStrAt = "(?<=@)([\\S]+(?=\\s)|[\\S]+)";
        String regexStrEmoji = "\\[表情]";
        String regexStrPic = "\\[图片]";
        String regexStrHttp = "(http[\\S]+(?=[\\s])|http[\\S]+)";//http with blank end or http simple end


        String dateformat = "yyyy-MM-dd HH:mm:ss";
        DateFormat datef = new SimpleDateFormat(dateformat);
        Set<String> uidHashSet = new TreeSet<>();
        List<Meta> metaList = new ArrayList<>();
        for (int i = 0; i < s.size(); i++) {
            Meta me = new Meta();
            Matcher dateMatcher = Pattern.compile(regexDate).matcher(s.get(i));
            if (dateMatcher.find()) {
                me.setDate(datef.parse(dateMatcher.group(0)));
            }
//            System.out.println(dateMatcher.group(0));
            Matcher nicknameMatcher = Pattern.compile(regexNickname).matcher(s.get(i));
            if (nicknameMatcher.find()) {
                me.setNickname(nicknameMatcher.group(0));

            }
//           System.out.println(nicknameMatcher.group(0));
            Matcher uidMatcher = Pattern.compile(regexUid).matcher(s.get(i));
            if (uidMatcher.find()) {
                me.setUid(uidMatcher.group(0));
                uidHashSet.add(uidMatcher.group(0));
            }
            Matcher contextMatcher = Pattern.compile(regexContext).matcher(s.get(i));
            if (contextMatcher.find()) {
                me.setStr(contextMatcher.group(0));
            }
            //cut the str
            Matcher strAtMatcher = Pattern.compile(regexStrAt).matcher(me.getStr());
            List<String> strAt = new ArrayList<>();
            while (strAtMatcher.find()) {
                strAt.add(strAtMatcher.group(0));
            }
            me.setAt(strAt);
            me.setStr(strAtMatcher.replaceAll(""));
            me.setStr(me.getStr().replace("@", ""));

            Matcher strEmojiMatcher = Pattern.compile(regexStrEmoji).matcher(me.getStr());
            while (strEmojiMatcher.find()) {
                me.setEmoji(me.getEmoji() + 1);
            }
            me.setStr(strEmojiMatcher.replaceAll(""));

            Matcher strPicMatcher = Pattern.compile(regexStrPic).matcher(me.getStr());
            while (strPicMatcher.find()) {
                me.setPic(me.getPic() + 1);
            }
            me.setStr(strPicMatcher.replaceAll(""));

            Matcher strHttpMatcher = Pattern.compile(regexStrHttp).matcher(me.getStr());
            List<String> strHttp = new ArrayList<>();
            while (strHttpMatcher.find()) {
                strHttp.add(strHttpMatcher.group(0));
            }
            me.setUrl(strHttp);
            me.setStr(strHttpMatcher.replaceAll(""));


            metaList.add(me);
        }

        //group by real person string-realName value-message
        Map<String, List<Meta>> personMap = new HashMap<>();
        for (Meta meta : metaList) {
            List<Meta> tempMeta;
            if (personMap.containsKey(realPersonMap.get(meta.getUid()))) {
                tempMeta = personMap.get(realPersonMap.get(meta.getUid()));
            } else {
                tempMeta = new ArrayList<>();
            }
            tempMeta.add(meta);
            personMap.put(realPersonMap.get(meta.getUid()), tempMeta);
        }


//        for (Meta me : metaList) {
//            System.out.println(me.toString());
//        }
//        uidHashSet.stream().sorted((val1,val2)->val1.compareTo(val2)).collect(Collectors.toCollection());
        final List<String> sorted = new ArrayList<>();
//        Collections.copy(sorted,uidHashSet);
//uidHashSet.forEach(val->sorted.add(val));
//        Collections.sort(uidHashSet, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return o1.compareTo(o2);
//            }
//        });

//        for (String me : uidHashSet) {
//            System.out.println(me.toString());
//        }

        //总数纬度
        System.out.println("据不完全统计：");
        total = metaList.size();
        System.out.println("本群2017一共产生了聊天记录 " + total + " 条");
        Set<Map.Entry<String, List<Meta>>> personMapEntry = personMap.entrySet();

        int meta1 = 0, meta2 = 0, meta3 = 0;
        String metaName1 = null, metaName2 = null, metaName3 = null;
        for (Map.Entry<String, List<Meta>> entry : personMapEntry) {
            if (meta1 < entry.getValue().size()) {
                meta3 = meta2;
                meta2 = meta1;
                meta1 = entry.getValue().size();
                metaName3 = metaName2;
                metaName2 = metaName1;
                metaName1 = entry.getKey();
            }
//            metaMax=metaMax>entry.getValue().size()?metaMax:entry.getValue().size();
        }

        System.out.println("其中 " + metaName1 + " 说的最多（" + meta1 + "），占了总数的百分之 " + (meta1 * 100 / total));
        System.out.println("前三名分别是 " + metaName1 + "（" + meta1 + "）" + metaName2 + "（" + meta2 + "）" + metaName3 + "（" + meta3 + "）占了总数的百分之 " + ((meta1 + meta2 + meta3) * 100 / metaList.size()));

        Map<String, Set<String>> personNickMap = new HashMap<>();
        for (Map.Entry<String, List<Meta>> entry : personMapEntry) {
            Set<String> nickList = new HashSet<>();
            for (Meta me : entry.getValue()) {
                nickList.add(me.getNickname());
            }
            if (nickList.size() != 1) {
                personNickMap.put(entry.getKey(), nickList);
            }
        }
        System.out.println("其中有 " + personNickMap.size() + " 人改过自己的昵称");
        Set<Map.Entry<String, Set<String>>> personNickMapEntry = personNickMap.entrySet();
        Set<String> nickNameMaxs = null;
        String nickNameMaxName = null;
        int nickNameMax = 0;
        for (Map.Entry<String, Set<String>> entry : personNickMapEntry) {
            if (nickNameMax < entry.getValue().size()) {
                nickNameMax = entry.getValue().size();
                nickNameMaxName = entry.getKey();
                nickNameMaxs = entry.getValue();
            }
        }
//        nickNameMaxs.stream().collect(Collectors.joining(""))
        StringBuilder nickNameSB = new StringBuilder();
        for (String nickStr : nickNameMaxs) {
            nickNameSB.append(nickStr).append(" ");
        }
        System.out.println("最爱改昵称的是 " + nickNameMaxName + " 他使用过的昵称如下：" + nickNameSB.toString());

        Map<String, Integer> personPicOrEmojiMap = new HashMap<>();
        for (Map.Entry<String, List<Meta>> entry : personMapEntry) {
            int num = 0;
            for (Meta me : entry.getValue()) {
                num = num + me.getPic() + me.getEmoji();
            }
            personPicOrEmojiMap.put(entry.getKey(), num);
        }

        Set<Map.Entry<String, Integer>> personPicOrEmojiEntry = personPicOrEmojiMap.entrySet();
        String personPicOrEmojiName = null;
        int personPicOrEmojiMax = 0;
        for (Map.Entry<String, Integer> entry : personPicOrEmojiEntry) {
            if (personPicOrEmojiMax < entry.getValue()) {
                personPicOrEmojiMax = entry.getValue();
                personPicOrEmojiName = entry.getKey();
            }
        }

        System.out.println("最爱发图片/表情的是 " + personPicOrEmojiName + " 一年内发了" + personPicOrEmojiMax + "个,平均每天发" + (personPicOrEmojiMax / YEAR) + "个");
        System.out.println("全体成员在");
        timeStatistic(metaList);
//时间纬度，高峰时间啊，周一到首日哪天消息最多

//个人纬度，每个人的口癖
        for (Map.Entry<String, List<Meta>> entry : personMapEntry) {
            System.out.println("当前人是 " + entry.getKey());
            if (entry.getKey().equals("赵璇")){
                personStatistic(entry.getValue());
            }

        }
    }

    private static void timeStatistic(List<Meta> list) {
        //week
        Map<Integer, List<Meta>> weekMap = new HashMap<>();
        //24h
        Map<Integer, List<Meta>> hourMap = new HashMap<>();

        for (Meta met : list) {
            Integer week = Integer.valueOf(weekNum.format(met.getDate()));
            Integer hour = Integer.valueOf(hourNum.format(met.getDate()));
            List<Meta> weekList = null;
            if (weekMap.containsKey(week)) {
                weekList = weekMap.get(week);
            } else {
                weekList = new ArrayList<>();
            }
            weekList.add(met);
            weekMap.put(week, weekList);

            List<Meta> hourList = null;
            if (hourMap.containsKey(hour)) {
                hourList = hourMap.get(hour);
            } else {
                hourList = new ArrayList<>();
            }
            hourList.add(met);
            hourMap.put(hour, hourList);

        }
        //max week
        int maxweek = 0, maxweekNum = 0;
        Set<Map.Entry<Integer, List<Meta>>> weekEntry = weekMap.entrySet();
        for (Map.Entry<Integer, List<Meta>> entry : weekEntry) {
            if (maxweekNum < entry.getValue().size()) {
                maxweekNum = entry.getValue().size();
                maxweek = entry.getKey();
            }
//            System.out.println("星期" + entry.getKey() + "发了" + entry.getValue().size() + "条消息，占总数的百分之" + (entry.getValue().size() * 100 / total));
        }
        System.out.println("星期" + maxweek + "最爱摸鱼,占总数的百分之" + (maxweekNum * 100 / total));
        int maxhour = 0, maxhourNum = 0;
        Set<Map.Entry<Integer, List<Meta>>> hourEntry = hourMap.entrySet();
        for (Map.Entry<Integer, List<Meta>> entry : hourEntry) {
            if (maxhourNum < entry.getValue().size()) {
                maxhourNum = entry.getValue().size();
                maxhour = entry.getKey();
            }
//            System.out.println("" + entry.getKey() + "点发了" + entry.getValue().size() + "条消息，占总数的百分之" + (entry.getValue().size() * 100 / total));
        }
        System.out.println("，最常在" + maxhour + "点说话,占总数的百分之" + (maxhourNum * 100 / total));
    }

    private static void personStatistic(List<Meta> list) {
        Map<String, Integer> sortedWords = new TreeMap<>();

        Comparator<Map.Entry<String, Integer>> comparable = (o1, o2) -> o2.getValue() - o1.getValue();

        for (Meta me : list) {
            if (me.getStr() != null && !me.getStr().equals("")) {
                Result parse = ToAnalysis.parse(me.getStr());
                for (Term t : parse) {
                    if (t.getRealName().length() == 1) {
                        continue;
                    }
                    if (sortedWords.containsKey(t.getRealName())) {
                        sortedWords.put(t.getRealName(), sortedWords.get(t.getRealName()) + 1);
                    } else {
                        sortedWords.put(t.getRealName(), 1);
                    }
                }
            }
        }
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(sortedWords.entrySet());
        sortedList.sort(comparable);
        System.out.println("最常说的词是:");
        for (int i = 0; i < (sortedList.size() > 100 ? 100 : sortedList.size()); i++) {
            System.out.println(sortedList.get(i).getKey() + "--" + sortedList.get(i).getValue());
        }
    }
}
