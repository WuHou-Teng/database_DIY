package WuHou.org.warehouse;

import WuHou.org.attributes.ReWr;
import WuHou.org.flags.*;
import WuHou.org.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DataBase {
    // 数据库名字
    private String name;
    /**
     * 这里有一个问题，作为一个数据库，它是允许相同的键值的。。。
     * 为了保留顺序，我是不是应该用 treeMap？
     * <p>
     * 我选择继续使用List，
     * 在 flag 中添加编号属性。
     * 如果相同，则自动为编号加1.
     */
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
     * 内部矩阵 Map格式，用于完全使用 key 来获得相应的 value
     * 例如：get(Target).get(key)
     */
    private Map<Target, Map<Key, String>> valueMatrixMap;

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

    // 修改权限
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

    public ArrayList<ArrayList<String>> getValuesList() {
        return new ArrayList<>(this.valueMatrixList);
    }

    public Map<Target, Map<Key, String>> getValuesMap() {
        return new HashMap<>(this.valueMatrixMap);
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public String getLastEditedTime() {
        return lastEditedTime;
    }

    public void setLastEditedTime(String lastEditedTime) {
        this.lastEditedTime = lastEditedTime;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ReWr getPermission() {
        return permission;
    }

    public void setPermission(ReWr permission) {
        this.permission = permission;
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
     * 提供已经创建好的 Tar 来添加 Tar【默认】
     * 如果有名字重复，则直接报错
     *
     * @param tar Target 对象
     */
    public void addTar(Target tar) throws
            NoExistTargetException, NoExistKeyException, FlagDuplicateException {
        for (Target tars : getTargets()) {
            if (tars.getName().equals(tar.getName())) {
                throw new FlagDuplicateException("<ERROR> addTar: Target name duplicated.");
            }
        }
        this.targetList.add(tar);
        if (getKeys().size() > 0) {
            for (Key key : getKeys()) {
                addValue("Null", tar, key);
            }
        }
    }

    /**
     * 以名字添加Tar 【默认】
     * 如果有名字重复，则直接报错
     *
     * @param tarName 新Target的对象的名字
     */
    public void addTar(String tarName) throws
            NoExistTargetException, NoExistKeyException, FlagDuplicateException {
        Target tar = new Target(tarName);
        addTar(tar);
    }

    /**
     * 提供已经创建好的 Key 来添加 key 【默认】
     * 如果有名称重复，则直接报错
     *
     * @param key Key 对象
     */
    public void addKey(Key key) throws
            NoExistTargetException,
            NoExistKeyException,
            FlagDuplicateException {
        for (Key keys : getKeys()) {
            if (keys.getName().equals(key.getName())) {
                throw new FlagDuplicateException("<ERROR> addKey: Key name duplicated.");
            }
        }
        this.keyList.add(key);
        if (getKeys().size() > 0) {
            for (Target tar : getTargets()) {
                addValue("Null", tar, key);
            }
        }
    }

    /**
     * 直接提供 key 名 来添加key 【默认】
     * 如果有名称重复，则直接报错
     *
     * @param keyName 新的key的名字
     */
    public void addKey(String keyName) throws
            NoExistTargetException,
            NoExistKeyException,
            FlagDuplicateException {
        Key key = new Key(keyName);
        addKey(key);
    }

    /**
     * 提供已经创建好的 Tar 来添加 Tar【高级】
     * 即使有名字重复，会输出警告，但更新 id 后依旧添加。
     *
     * @param tar Target 对象
     */
    public void addTarAdv(Target tar) throws NoExistTargetException, NoExistKeyException {
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
        if (getKeys().size() > 0) {
            for (Key key : getKeys()) {
                addValue("Null", tar, key);
            }
        }
    }

    /**
     * 以名字添加 Tar 【高级】
     * 即使有名字重复，会输出警告，但更新 id 后依旧添加。
     *
     * @param tarName 新Target的对象的名字
     */
    public void addTarAdv(String tarName) throws NoExistTargetException, NoExistKeyException {
        Target tar = new Target(tarName);
        addTarAdv(tar);
    }

    /**
     * 提供已经创建好的 Key 来添加 key 【高级】
     * 即使有名字重复，会输出警告，但更新 id 后依旧添加。
     *
     * @param key Key 对象
     */
    public void addKeyAdv(Key key) throws NoExistTargetException, NoExistKeyException {
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
        if (getKeys().size() > 0) {
            for (Target tar : getTargets()) {
                addValue("Null", tar, key);
            }
        }
    }

    /**
     * 直接提供 key 名 来添加key 【高级】
     * 即使有名字重复，会输出警告，但更新 id 后依旧添加。
     *
     * @param keyName 新的key的名字
     */
    public void addKeyAdv(String keyName) throws NoExistTargetException, NoExistKeyException {
        Key key = new Key(keyName);
        addKeyAdv(key);
    }

    /**
     * 自动添加 length 长度的 tar，tar的名字采用默认值 tar+[0-99]
     * <p>
     * TODO
     */
    public void addDefaultKeys(int length) {
        
    }

    /**
     * 自动添加 length 长度的 key，key的名字采用默认值 key+[0-99]
     * <p>
     * TODO
     */
    public void addDefaultTars(int length) {

    }

    /**
     * 自动创建表格，方式是分别调用 addDefaultKeys 和 addDefaultTars
     * <p>
     * TODO
     */
    public void createFormTK(int tarLength, int keyLength) {

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

        if (tar == null){
            throw new NoExistTargetException("<ERROR> addValue: Input tar is null when adding "
                    + value + " with Key:" + key.getName());
        }
        if (key == null) {
            throw new NoExistKeyException("<ERROR> addValue: Input key is null when adding "
                    + value + " with Tar:" + tar.getName());
        }

        int keyPosition;
        int tarPosition;

        if (this.keyList.contains(key)) {
            keyPosition = this.keyList.indexOf(key);
        } else {
            throw new NoExistKeyException("<ERROR> addValue: No key in list fits when adding "
                    + value + " with Tar:" + tar.getName() + " and Key:" + key.getName());
        }

        if (this.targetList.contains(tar)) {
            tarPosition = this.targetList.indexOf(tar);
        } else {
            throw new NoExistTargetException("<ERROR> addValue: No target in list fits when adding "
                    + value + " with Tar:" + tar.getName() + " and Key:" + key.getName());
        }

        this.valueMatrixList.get(tarPosition).set(keyPosition, value);
        this.valueMatrixMap.get(tar).replace(key, value);
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
     */
    public void addValueKeyFits(String value, Target tar, String keyName) throws
            NoExistTargetException, NoExistKeyException {
        ArrayList<Key> keysFit;
        if ((keysFit = getKeysFit(keyName)).size() >= 1) {
            for (Key keys: keysFit) {
                addValue(value, tar, keys);
            }
        } else {
            throw new NoExistTargetException("<ERROR> addValueKeyFits: No Key fit keyName :"
                    + keyName);
        }
    }

    /**
     * 为所有符合名字的 (tar, key) 都加上相同的 value
     */
    public void addValueAll(String value, String tarName, String keyName) throws
            NoExistTargetException, NoExistKeyException {
        ArrayList<Target> tarsFit = getTarsFit(tarName);
        ArrayList<Key> keysFit = getKeysFit(keyName);
        if (tarsFit.size() >= 1) {
            if (keysFit.size() >= 1) {
                for (Target tars : tarsFit) {
                    for (Key keys : keysFit) {
                        addValue(value, tars , keys);
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
     * TODO 我不确定是否需要重新写。
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

