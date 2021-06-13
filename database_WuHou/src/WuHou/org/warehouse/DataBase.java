package WuHou.org.warehouse;

import WuHou.org.flags.*;
import WuHou.org.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DataBase {
    // 数据库名字
    private String name;
    /**
     * TODO
     * 这里有一个问题，作为一个数据库，它是允许相同的键值的。。。
     * 为了保留顺序，我是不是应该用 treeMap？
     *
     * 我选择继续使用List，
     * 在 flag 中添加编号属性。
     * 如果相同，则自动为编号加1.
     */
    // key 列表
    private ArrayList<Key> keys;
    // Tar 列表
    private ArrayList<Target> targets;

    // 创建日期
    private String timeCreated;
    // 描述
    private String reference;

    /**
     * 内部矩阵 Map格式，用于完全使用 key 来获得相应的 value
     * 例如：get(Target).get(key)
     */
    private Map<Target, Map<Key, String>> FSMap;

    /**
     * 内部矩阵的 ArrayList 格式，用于完全使用坐标来获得相应的 value
     * 例如: get(2,3)
     *
     * 亦或者 get(Tar, 2)
     * 亦或者 get(Key, 2)
     * 后两种方法都是将 Tar 或者 从 ArrayList中找出来，然后丢进下面的 List 来获得Value
     *
     * Tar在外层，Key在里层
     */
    private final ArrayList<ArrayList<String>> FSList;

    /**
     * 构造函数
     */
    public DataBase(String name, String timeCreated) {
        this.name = name;
        this.keys = new ArrayList<>();
        this.targets = new ArrayList<>();
        this.FSList = new ArrayList<>();
        this.FSMap = new HashMap<>();
        this.timeCreated = timeCreated;
        this.reference = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Key> getKeys() {
        return new ArrayList<>(keys);
    }

    public ArrayList<Target> getTargets() {
        return new ArrayList<>(targets);
    }

    public ArrayList<ArrayList<String>> getValues() {
        return new ArrayList<>(this.FSList);
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    // TODO
    public void addTar (String tarName) {}

    public void addTar (Target tar) {}


    /**
     * 直接提供 key 名 来添加key
     */
    // TODO 待完善
    public void addKey (String keyName) {
        this.keys.add(new Key(keyName));

    }

    /**
     * 提供已经创建好的 Key 来添加 key
     * TODO
     */
    public void addKey (Key key) {
        this.keys.add(key);
    }

    /**
     * 添加value
     * TODO
     */
    public void addValue (String value, Target tar, Key key) {
        int keyPosition = 0;
        int tarPosition = 0;
        for (Key keyS : this.keys) {
            if (keyS.equals(key)) {
                break;
            } else {
                keyPosition ++;
            }
        }
    }
}

