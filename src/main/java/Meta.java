import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * 单元数据格式如下
 * 2016-06-28 12:01:46 wuli超超(234067334)
 *  以前大厂会因为网速不好放假
 */
public class Meta {

    private String uid;//uid is person realname;

    private Date date;
    private String nickname;
    private String id;// qq or email

    private List<String> at;//who that person want to at ,string is nickname or uid
    private int pic;
    private int emoji;
    private List<String> url;

    private String str;//other str

    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
        return format.format(date) + " " + nickname + " " + uid + " " + str;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAt() {
        return at;
    }

    public void setAt(List<String> at) {
        this.at = at;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public int getEmoji() {
        return emoji;
    }

    public void setEmoji(int emoji) {
        this.emoji = emoji;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
