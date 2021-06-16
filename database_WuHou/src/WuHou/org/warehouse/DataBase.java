package WuHou.org.warehouse;

import WuHou.org.attributes.ReWr;
import WuHou.org.flags.*;
import WuHou.org.util.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DataBase {
    // 数据库名字
    private String name;
    // key 列表
    private ArrayList<Key> keyList;
    // Tar 列表
    private ArrayList<Target> targetList;

    // 创建日期
    private final String timeCreated;

    // 修改日期
    private String lastEditedTime;

    // 描述
    private String reference;

    /**
     * 时间反馈
     */
    public static SimpleDateFormat Fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:ms");

    /**
     * 内部矩阵 Map格式，用于完全使用 key 来获得相应的 value
     * 例如：get(Target).get(key)
     */
    private final Map<Target, Map<Key, String>> valueMatrixMap;

    /**
     * 内部矩阵的 ArrayList 格式，用于完全使用坐标来获得相应的 value
     * 例如: get(2,3)
     * <p>
     * 亦或者 get(Tar, 2)
     * 亦或者 get(Key, 2)
     * 后两种方法都是将 Tar 或者 从 ArrayList中找出来，然后丢进下面的 List 来获得Value
     * <p>
     * Tar在外层，Key在里层
     */
    private final ArrayList<ArrayList<String>> valueMatrixList;

    // 权限
    private ReWr permission;

    /**
     * 构造函数
     */
    public DataBase(String name, String timeCreated) {
        this.name = name;
        this.keyList = new ArrayList<>();
        this.targetList = new ArrayList<>();
        this.valueMatrixList = new ArrayList<>();
        this.valueMatrixMap = new HashMap<>();
        this.timeCreated = timeCreated;
        this.lastEditedTime = timeCreated;
        this.reference = "";
        this.permission = ReWr.READ_WRITE;
    }

    /**
     * 用于复制的构造函数
     */
    public DataBase(DataBase dataBase) {
        this.name = dataBase.getName();
        this.keyList = new ArrayList<>(dataBase.getKeys());
        this.targetList = new ArrayList<>(dataBase.getTargets());
        this.valueMatrixList = new ArrayList<>(dataBase.getValuesList());
        this.valueMatrixMap = new HashMap<>(dataBase.getValuesMap());
        this.timeCreated = dataBase.getTimeCreated();
        this.lastEditedTime = dataBase.getLastEditedTime();
        this.reference = dataBase.getReference();
        this.permission = dataBase.getPermission();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 获得Key列表
    public ArrayList<Key> getKeys() {
        return new ArrayList<>(keyList);
    }

    // 获得Target列表
    public ArrayList<Target> getTargets() {
        return new ArrayList<>(targetList);
    }

    // 获得value矩阵的List形式
    public ArrayList<ArrayList<String>> getValuesList() {
        return new ArrayList<>(this.valueMatrixList);
    }

    // 获得value矩阵的Map形式
    public Map<Target, Map<Key, String>> getValuesMap() {
        return new HashMap<>(this.valueMatrixMap);
    }

    // 获得数据库创建时间
    public String getTimeCreated() {
        return timeCreated;
    }

    // 获得数据库最后更新的时间
    public String getLastEditedTime() {
        return lastEditedTime;
    }

    // 获取时间字符串
    protected String getTime() {
        return String.format("|%s|", Fmt.format(new Date()));
    }

    // 更新数据库最后更新的时间
    public void updateLastEditedTime() {
        this.lastEditedTime = getTime();
    }

    // 设定数据库最后更新的时间
    public void setLastEditedTime(String lastEditedTime) {
        this.lastEditedTime = lastEditedTime;
    }

    // 获取数据库描述
    public String getReference() {
        return reference;
    }

    // 设定数据库描述
    public void setReference(String reference) {
        this.reference = reference;
    }

    // 查询数据库读写权限
    public ReWr getPermission() {
        return permission;
    }

    // 设定数据库读写权限
    public void setPermission(ReWr permission) {
        this.permission = permission;
    }

    /**
     * 获取输入的 Key在 KeyList中的位置
     */
    public int getKeyPosition(Key key) throws NoExistKeyException {
        if (getKeys().contains(key)) {
            return getKeys().indexOf(key);
        } else {
            throw new NoExistKeyException(
                    "<ERROR> getKeyPosition: Key to be search not exit: " + key.toString());
        }
    }

    /**
     * 获取输入的 Target在 TarList中的位置
     */
    public int getTarPosition(Target tar) throws NoExistTargetException {
        if (getTargets().contains(tar)) {
            return getTargets().indexOf(tar);
        } else {
            throw new NoExistTargetException(
                    "<ERROR> getTarPosition: Target to be search not exit: " + tar.toString());
        }
    }

    /**
     * 返回一个 包含所有在 keyList中的拥有和输入的 Key名字相同的 Key。
     *
     * @param keyName 查询的 key的名字
     * @return keysFit 所有名字为 keyName的 key
     */
    public ArrayList<Key> getKeysFit(String keyName) {
        ArrayList<Key> keysFit = new ArrayList<>();
        for (Key keys : this.keyList) {
            if (keys.getName().equals(keyName)) {
                keysFit.add(keys);
            }
        }
        return keysFit;
    }

    /**
     * 返回一个 包含所有在 tarList中的拥有和输入的 Tar名字相同的 Tar。
     *
     * @param tarName 查询的 tar的名字
     * @return tarsFit 所有名字为 tarName的 target
     */
    public ArrayList<Target> getTarsFit(String tarName) {
        ArrayList<Target> tarsFit = new ArrayList<>();
        for (Target tars : this.targetList) {
            if (tars.getName().equals(tarName)) {
                tarsFit.add(tars);
            }
        }
        return tarsFit;
    }

    /**
     * value矩阵的数据更新
     *
     * @param tar 更新方式是新加入了一个tar
     */
    private void updateValueMatrix(Target tar) {
        ArrayList<String> newValueList = new ArrayList<>();
        HashMap<Key, String> newValueMap = new HashMap<>();
        if (getKeys().size() > 0) {
            for (Key key : getKeys()) {
                newValueMap.put(key, "null");
                newValueList.add("null");
            }
        }
        this.valueMatrixList.add(newValueList);
        this.valueMatrixMap.put(tar, newValueMap);
    }

    /**
     * value矩阵的数据更新
     *
     * @param key 更新方式是新加入了一个key
     */
    private void updateValueMatrix(Key key) {
        if (getTargets().size() > 0) {
            for (Target tars : getTargets()) {
                this.valueMatrixMap.get(tars).put(key, "null");
            }
            for (int i = 0; i < getTargets().size(); i++) {
                this.valueMatrixList.get(i).add("null");
            }
        }
    }

    /**
     * 提供已经创建好的 Tar 来添加 Tar【默认】
     * 如果有名字重复，则直接报错
     *
     * @param tar Target 对象
     */
    public void addTar(Target tar) throws FlagDuplicateException {
        for (Target tars : getTargets()) {
            if (tars.getName().equals(tar.getName())) {
                throw new FlagDuplicateException("<ERROR> addTar: Target name duplicated.");
            }
        }
        this.targetList.add(tar);
        updateValueMatrix(tar);
    }

    /**
     * 以名字添加Tar 【默认】
     * 如果有名字重复，则直接报错
     *
     * @param tarName 新Target的对象的名字
     */
    public void addTar(String tarName) throws FlagDuplicateException {
        Target tar = new Target(tarName);
        addTar(tar);
    }

    /**
     * 提供已经创建好的 Key 来添加 key 【默认】
     * 如果有名称重复，则直接报错
     *
     * @param key Key 对象
     */
    public void addKey(Key key) throws FlagDuplicateException {
        for (Key keys : getKeys()) {
            if (keys.getName().equals(key.getName())) {
                throw new FlagDuplicateException("<ERROR> addKey: Key name duplicated.");
            }
        }
        this.keyList.add(key);
        updateValueMatrix(key);
    }

    /**
     * 直接提供 key 名 来添加key 【默认】
     * 如果有名称重复，则直接报错
     *
     * @param keyName 新的key的名字
     */
    public void addKey(String keyName) throws FlagDuplicateException {
        Key key = new Key(keyName);
        addKey(key);
    }

    /**
     * 提供已经创建好的 Tar 来添加 Tar【高级】
     * 即使有名字重复，会输出警告，但更新 id 后依旧添加。
     *
     * @param tar Target 对象
     */
    public void addTarAdv(Target tar) {
        for (Target tars : getTargets()) {
            if (tars.equals(tar)) {
                tar.updateId();
            }
        }
        if (tar.getId() != 0) {
            System.err.printf("<WARN> addTar: Target name duplicated. Name: %s, Id: %d%n",
                    tar.getName(), tar.getId());
        }
        this.targetList.add(tar);
        updateValueMatrix(tar);
    }

    /**
     * 以名字添加 Tar 【高级】
     * 即使有名字重复，会输出警告，但更新 id 后依旧添加。
     *
     * @param tarName 新Target的对象的名字
     */
    public void addTarAdv(String tarName) {
        Target tar = new Target(tarName);
        addTarAdv(tar);
    }

    /**
     * 提供已经创建好的 Key 来添加 key 【高级】
     * 即使有名字重复，会输出警告，但更新 id 后依旧添加。
     *
     * @param key Key 对象
     */
    public void addKeyAdv(Key key) {
        for (Key keys : getKeys()) {
            if (keys.equals(key)) {
                key.updateId();
            }
        }
        if (key.getId() != 0) {
            System.err.printf("<WARN> addKey: Key name duplicated. Name: %s, Id: %d%n",
                    key.getName(), key.getId());
        }
        this.keyList.add(key);
        updateValueMatrix(key);
    }

    /**
     * 直接提供 key 名 来添加key 【高级】
     * 即使有名字重复，会输出警告，但更新 id 后依旧添加。
     *
     * @param keyName the name of New key
     */
    public void addKeyAdv(String keyName) {
        Key key = new Key(keyName);
        addKeyAdv(key);
    }

    /**
     * 自动添加 length 数量的 key，key的名字采用 name+[0-99]
     *
     * @param length the number of keys gonna be add to database
     * @param name   the base name of keys
     */
    public void addNamedKeys(int length, String name) {
        int i = 0;
        while (i < length) {
            addKeyAdv(name + i);
            i++;
        }
    }

    /**
     * 自动添加 length 数量的 key，key的名字采用默认值 Key+[0-99]
     *
     * @param length the number of keys gonna be add to database
     */
    public void addDefaultKeys(int length) {
        addNamedKeys(length, "Key");
    }

    /**
     * 自动添加 length 数量的 tar，tar的名字采用默认值 name+[0-99]
     *
     * @param length the number of targets gonna be add to database
     * @param name   the base name of targets
     */
    public void addNamedTars(int length, String name) {
        int i = 0;
        while (i < length) {
            addTarAdv(name + i);
            i++;
        }
    }

    /**
     * 自动添加 length 数量的 tar，tar的名字采用默认值 Tar+[0-99]
     *
     * @param length the number of targets gonna be add to database
     */
    public void addDefaultTars(int length) {
        addNamedTars(length, "Tar");
    }


    /**
     * 创建表格，方式是分别调用 addDefaultKeys 和 addDefaultTars
     *
     * @param tarLength the number of targets gonna be add to database
     * @param keyLength the number of keys gonna be add to database
     */
    public void form(int tarLength, int keyLength) {
        addDefaultTars(tarLength);
        addDefaultKeys(keyLength);
    }

    /**
     * 创建表格，该方法允许自定义表格的横竖名称
     *
     * @param tarLength the number of targets gonna be add to database
     * @param keyLength the number of keys gonna be add to database
     */
    public void formNamed(int tarLength, int keyLength,
                                  String tarBase, String keyBase) {
        addNamedTars(tarLength, tarBase);
        addNamedKeys(keyLength, keyBase);
    }

    /**
     * 创建表格，并将表格内的数据初始化为输入的数值value
     */
    public void formValued(int tarLength, int keyLength, String value) throws
            NoExistTargetException,
            NoExistKeyException,
            FlagDuplicateException {
        String ts = "Tar" + "0";
        String te = "Tar" + (tarLength - 1);
        String ks = "Key" + "0";
        String ke = "Key" + (keyLength - 1);
        addDefaultKeys(keyLength);
        addDefaultTars(tarLength);
        memsetValueRange(value, ts, te, ks, ke);
    }

    /**
     * 创建表格，该方法允许自定义表格的横竖名称，并将表格内的数据初始化为输入的数值value
     */
    public void formNamedValued (int tarLength,
                                 int keyLength,
                                 String value,
                                 String tarBase,
                                 String keyBase) throws
            NoExistTargetException,
            NoExistKeyException,
            FlagDuplicateException {
        String ts = tarBase + "0";
        String te = tarBase + (tarLength - 1);
        String ks = keyBase + "0";
        String ke = keyBase + (keyLength - 1);
        addNamedTars(tarLength, tarBase);
        addNamedKeys(keyLength, keyBase);
        memsetValueRange(value, ts, te, ks, ke);
    }

    /**
     * 添加value, 带入参数为确定的 Key对象 和 Tar对象
     *
     * @param value the value to be added to database
     * @param tar   specific which target to add
     * @param key   specific which key to add
     * @throws NoExistKeyException    when key input is not exist in list
     * @throws NoExistTargetException when target is not exist in list
     * @require tar and key are not null
     */
    public void addValue(String value, Target tar, Key key) throws
            NoExistTargetException, NoExistKeyException {
        if (getTargets().size() == 0) {
            throw new NoExistTargetException("<ERROR> addValue: Tar List is empty when adding"
                    + value + " with Tar:" + tar.getName() + " and Key:" + key.getName());
        }

        if (getKeys().size() == 0) {
            throw new NoExistKeyException("<ERROR> addValue: Key List is empty when adding "
                    + value + " with Tar:" + tar.getName() + " and Key:" + key.getName());
        }

        if (tar == null) {
            throw new NoExistTargetException("<ERROR> addValue: Input tar is null when adding "
                    + value + " with Key:" + key.getName());
        }
        if (key == null) {
            throw new NoExistKeyException("<ERROR> addValue: Input key is null when adding "
                    + value + " with Tar:" + tar.getName());
        }

        int keyPosition;
        int tarPosition;
        try {
            keyPosition = getKeyPosition(key);
            tarPosition = getTarPosition(tar);
            this.valueMatrixList.get(tarPosition).set(keyPosition, value);
            this.valueMatrixMap.get(tar).replace(key, value);
        } catch (NoExistTargetException e1) {
            throw new NoExistKeyException("<ERROR> addValue: No key in list fits when adding "
                    + value + " with Tar:" + tar.toString() + " and Key:" + key.toString());
        } catch (NoExistKeyException e2) {
            throw new NoExistTargetException("<ERROR> addValue: No target in list fits when adding "
                    + value + " with Tar:" + tar.toString() + " and Key:" + key.toString());
        }
    }

    /**
     * 添加 value，其中 tar是单纯的String, key则是key对象
     * 因此会首先测试，TarList中是否存在多个tar，如果是，则抛出异常，
     * 否则调用 addValue(String, Target, Key)
     *
     * @param value   the value to be added to database
     * @param tarName specific which target to add
     * @param key     specific which key to add
     * @throws NoExistKeyException    when key input is not exist in list
     * @throws NoExistTargetException when target is not exist in list
     * @throws FlagDuplicateException when more than one Target has been found by tarName
     */
    public void addValue(String value, String tarName, Key key) throws
            NoExistTargetException, NoExistKeyException, FlagDuplicateException {
        if (getTarsFit(tarName).size() == 1) {
            addValue(value, getTarsFit(tarName).get(0), key);
        } else {
            throw new FlagDuplicateException(
                    "<ERROR> addValue(SKS): No or More than one Target fit tarName :" + tarName);
        }
    }

    /**
     * 添加 value，其中 tar是单纯的String, key则是key对象
     * 因此会首先测试，TarList中是否存在多个tar，如果是，则抛出异常，
     * 否则调用 addValue(String, Target, Key)
     *
     * @param value   the value to be added to database
     * @param tar     specific which target to add
     * @param keyName specific which key to add
     * @throws NoExistKeyException    when key input is not exist in list
     * @throws NoExistTargetException when target is not exist in list
     * @throws FlagDuplicateException when more than one Target has been found by tarName
     */
    public void addValue(String value, Target tar, String keyName) throws
            NoExistTargetException, NoExistKeyException, FlagDuplicateException {
        if (getTarsFit(keyName).size() == 1) {
            addValue(value, tar, getKeysFit(keyName).get(0));
        } else {
            throw new FlagDuplicateException(
                    "<ERROR> addValue(STS): No or More than one Key fit tarName :" + keyName);
        }
    }

    /**
     * 添加 value，其中 tar是单纯的String, key则是key对象
     * 因此会首先测试，TarList中是否存在多个tar，如果是，则抛出异常，
     * 否则调用 addValue(String, Target, Key)
     *
     * @param value   the value to be added to database
     * @param tarName specific which target to add
     * @param keyName specific which key to add
     * @throws NoExistKeyException    when key input is not exist in list
     * @throws NoExistTargetException when target is not exist in list
     * @throws FlagDuplicateException when more than one Target has been found by tarName
     */
    public void addValue(String value, String tarName, String keyName) throws
            NoExistTargetException, NoExistKeyException, FlagDuplicateException {
        if (getTarsFit(keyName).size() == 1) {
            addValue(value, getTarsFit(tarName).get(0), getKeysFit(keyName).get(0));
        } else {
            throw new FlagDuplicateException(
                    "<ERROR> addValue(SSS): No or More than one flags fit the names given :"
                            + tarName + ", " + keyName);
        }
    }

    /**
     * 添加 value，为对应 Key，以及所有符合名字的 Tar 添加相同的 value
     *
     * @param value   the value to be add to database
     * @param tarName the name of tar to be search in database
     * @param key     the key to be search in database
     * @throws NoExistTargetException when No Target fit tarName
     */
    public void addValueTarFits(String value, String tarName, Key key) throws
            NoExistTargetException, NoExistKeyException {
        ArrayList<Target> tarsFit;
        //如果有一个以上的Target符合名称查询，则为每个符合的Tar和key 添加value
        if ((tarsFit = getTarsFit(tarName)).size() >= 1) {
            for (Target tars : tarsFit) {
                addValue(value, tars, key);
            }
        } else {
            throw new NoExistTargetException("<ERROR> addValueTarFits: No Target fit tarName :"
                    + tarName);
        }
    }

    /**
     * 添加 value，为对应 Tar，以及所有符合名字的 Key 添加相同的 value
     *
     * @param value   the value to be add to database
     * @param tar     the tar to be search in database
     * @param keyName the name of key to be search in database
     * @throws NoExistKeyException when No Key fit keyName
     */
    public void addValueKeyFits(String value, Target tar, String keyName) throws
            NoExistTargetException, NoExistKeyException {
        ArrayList<Key> keysFit;
        if ((keysFit = getKeysFit(keyName)).size() >= 1) {
            for (Key keys : keysFit) {
                addValue(value, tar, keys);
            }
        } else {
            throw new NoExistKeyException("<ERROR> addValueKeyFits: No Key fit keyName :"
                    + keyName);
        }
    }

    /**
     * 为所有符合名字的 (tar, key) 都加上相同的 value
     *
     * @param value   the value to be add to database
     * @param tarName the name of target to be search in database
     * @param keyName the name of key to be search in database
     * @throws NoExistTargetException when No Target fit tarName
     * @throws NoExistKeyException    when No Key fit keyName
     */
    public void addValueBoth(String value, String tarName, String keyName) throws
            NoExistTargetException, NoExistKeyException {
        ArrayList<Target> tarsFit = getTarsFit(tarName);
        ArrayList<Key> keysFit = getKeysFit(keyName);
        if (tarsFit.size() >= 1) {
            if (keysFit.size() >= 1) {
                for (Target tars : tarsFit) {
                    for (Key keys : keysFit) {
                        addValue(value, tars, keys);
                    }
                }
            } else {
                throw new NoExistTargetException("<ERROR> addValueAll: No Key fit keyName :"
                        + keyName);
            }
        } else {
            throw new NoExistTargetException("<ERROR> addValueAll: No Target fit tarName :"
                    + tarName);
        }
    }

    /**
     * 区域化的更改数据，根据输入的数字确定区域，并将区域内所有的 value都改为输入的 value
     *
     * @param value    待更改的数据
     * @param tarStart TarList的开始位置
     * @param tarEnd   TarList的结束位置
     * @param keyStart KeyList的开始位置
     * @param keyEnd   KeyList的结束位置
     * @throws NoExistKeyException    keyStart 或者 keyEnd 不存在
     * @throws NoExistTargetException tarStart 或者 tarEnd 不存在
     */
    public void memsetValueRange(String value,
                                 int tarStart,
                                 int tarEnd,
                                 int keyStart,
                                 int keyEnd) throws
            NoExistTargetException, NoExistKeyException {
        if (tarStart >= getTargets().size() || tarEnd >= getTargets().size()
                || tarStart < 0 || tarEnd < 0) {
            throw new NoExistTargetException(
                    "<ERROR> memsetValueRange(SIIII): Given tarPosition out of range: ["
                            + tarStart + ", " + tarEnd + "]");
        }
        if (keyStart >= getKeys().size() || keyEnd >= getKeys().size()
                || keyStart < 0 || keyEnd < 0) {
            throw new NoExistKeyException(
                    "<ERROR> memsetValueRange(SIIII): Given keyPosition out of range: ["
                            + keyStart + ", " + keyEnd + "]");
        }
        int buffer;
        if (tarStart >= tarEnd) {
            buffer = tarEnd;
            tarEnd = tarStart;
            tarStart = buffer;
        }
        if (keyStart >= keyEnd) {
            buffer = keyEnd;
            keyEnd = keyStart;
            keyStart = buffer;
        }

        for (int t = tarStart; t <= tarEnd; t++) {
            for (int k = keyStart; k <= keyEnd; k++) {
                this.valueMatrixList.get(t).set(k, value);
                //this.valueMatrixMap.get(this.targetList.get(t)).replace(this.keyList.get(k), value);
                this.valueMatrixMap.get(getTargets().get(t)).replace(getKeys().get(k), value);
            }
        }
    }

    /**
     * 区域化的更改数据，根据输入的 Flag对象 确定区域，并将区域内所有的 value都改为输入的 value
     *
     * @param value    待更改的数据
     * @param tarStart 开始位置的 target 对象
     * @param tarEnd   结束位置的 target 对象
     * @param keyStart 开始位置的 key 对象
     * @param keyEnd   结束位置的 key 对象
     * @throws NoExistKeyException    keyStart 或者 keyEnd 不存在
     * @throws NoExistTargetException tarStart 或者 tarEnd 不存在
     */
    public void memsetValueRange(String value,
                                 Target tarStart,
                                 Target tarEnd,
                                 Key keyStart,
                                 Key keyEnd) throws
            NoExistTargetException, NoExistKeyException {
        try {
            int ts = getTarPosition(tarStart);
            int te = getTarPosition(tarEnd);
            int ks = getKeyPosition(keyStart);
            int ke = getKeyPosition(keyEnd);
            // 调用上面那个
            memsetValueRange(value, ts, te, ks, ke);
        } catch (NoExistTargetException e1) {
            throw new NoExistTargetException(
                    "<ERROR> memsetValueRange(STTKK): Tars to be search not exit.\ntarStart: "
                            + tarStart.toString() + " tarEnd: " + tarEnd.toString());
        } catch (NoExistKeyException e2) {
            throw new NoExistKeyException(
                    "<ERROR> memsetValueRange(STTKK): Keys to be search not exit.\nkeyStart: "
                            + keyStart.toString() + " keyEnd: " + keyEnd.toString());
        }
    }

    /**
     * 区域化的更改数据，根据输入的字符串分别在 TarList 和 KeyList 中查找符合的 Flag,
     * 以此确定区域，并将区域内所有的 value都改为输入的 value
     *
     * @param value    待更改的数据
     * @param tarStart 开始位置的 targetName
     * @param tarEnd   结束位置的 targetName
     * @param keyStart 开始位置的 keyName
     * @param keyEnd   结束位置的 keyName
     * @throws NoExistKeyException    keyStart 或者 keyEnd 不存在
     * @throws NoExistTargetException tarStart 或者 tarEnd 不存在
     * @throws FlagDuplicateException 当在列表中有多余1个的 Flag 符合所给名字的时候
     */
    public void memsetValueRange(String value,
                                 String tarStart,
                                 String tarEnd,
                                 String keyStart,
                                 String keyEnd) throws
            NoExistTargetException, NoExistKeyException, FlagDuplicateException {
        ArrayList<Target> tarStarts = getTarsFit(tarStart);
        ArrayList<Target> tarEnds = getTarsFit(tarEnd);
        ArrayList<Key> keyStarts = getKeysFit(keyStart);
        ArrayList<Key> keyEnds = getKeysFit(keyEnd);
        if (tarStarts.size() == 1 && tarEnds.size() == 1 &&
                keyStarts.size() == 1 && keyEnds.size() == 1) {
            memsetValueRange(value,
                    tarStarts.get(0),
                    tarEnds.get(0),
                    keyStarts.get(0),
                    keyEnds.get(0));
        } else if (tarStarts.size() < 1 || tarEnds.size() < 1) {
            throw new NoExistTargetException(
                    "<ERROR> memsetValueRange(SSSSS): Tars to be search not exit.\ntarStart: "
                            + tarStart + " tarEnd: " + tarEnd);
        } else if (keyStarts.size() < 1 || keyEnds.size() < 1) {
            throw new NoExistKeyException(
                    "<ERROR> memsetValueRange(SSSSS): Keys to be search not exit.\nkeyStart: "
                            + keyStart + " keyEnd: " + keyEnd);
        } else {
            throw new FlagDuplicateException(
                    "<ERROR> memsetValueRange(SSSSS): More than one Flag has the same name");
        }
    }

    /**
     * TODO 我不确定是否需要重新写。
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

